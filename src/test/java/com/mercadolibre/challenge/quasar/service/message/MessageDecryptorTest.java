package com.mercadolibre.challenge.quasar.service.message;

import com.mercadolibre.challenge.quasar.service.message.exception.IndeterminateMessageException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MessageDecryptorTest {

    private final MessageDecryptor messageDecryptor = new MessageDecryptorImpl();

    @Test
    void givenValidMessages_Then_ReturnsDecryptedMessage() {
        List<List<String>> messages = new ArrayList<>(3);
        messages.add(createMessage("este", "",   "",   "mensaje", "secreto"));
        messages.add(createMessage("", "es",   "",   "", "secreto"));
        messages.add(createMessage("este", "",   "un",   "", ""));

        String messageDecrypted = decrypt(messages);
        assertThat(messageDecrypted).isEqualTo("este es un mensaje secreto");
    }

    @Test
    void givenMessagesWithBlanks_Then_ReturnsDecryptedMessage() {
        List<List<String>> messages = new ArrayList<>(3);
        messages.add(createMessage("  este  ", "",   "",   "mensaje", "secreto"));
        messages.add(createMessage("", "es",   "",   "", "  secreto  "));
        messages.add(createMessage("    ", "",   "un",   "", "  "));

        String messageDecrypted = decrypt(messages);
        assertThat(messageDecrypted).isEqualTo("este es un mensaje secreto");
    }

    @Test
    void givenListMessagesWithDifferentSizesBlanks_Then_ShouldMatchColumnsAndDecryptedMessage() {
        List<List<String>> messages = new ArrayList<>(3);
        messages.add(createMessage("este", "",   "",   "", "secreto"));
        messages.add(createMessage("un",   "secreto"));
        messages.add(createMessage("", "es",   " ",   "", ""));

        String messageDecrypted = decrypt(messages);
        assertThat(messageDecrypted).isEqualTo("este es un secreto");
    }


    @Test
    void givenColumnsMessagesWithDifferentStrings_Then_ThrowsException() {
        List<List<String>> messages = new ArrayList<>(2);
        messages.add(createMessage("hola", "Meli"));
        messages.add(createMessage("Hi!",  "Meli"));
        messages.add(createMessage("",  "Meli"));

        assertThatThrownBy(() -> decrypt(messages))
                .isInstanceOf(IndeterminateMessageException.class)
                .hasMessage("Different Strings in the same column(0) [hola - hi!]");
    }

    @Test
    void givenWrongListSizeMessages_Then_ThrowsException() {
        List<List<String>> messages = new ArrayList<>(1);
        messages.add(createMessage("hola", "Meli"));

        assertThatThrownBy(() -> decrypt(messages))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenAllMessagesEmpty_Then_ReturnsEmptyMessage() {
        List<List<String>> messages = new ArrayList<>(3);
        messages.add(createMessage("", " ",   "  "));
        messages.add(createMessage(""));
        messages.add(createMessage(" ", " "));

        String messageDecrypted = decrypt(messages);
        assertThat(messageDecrypted).isEmpty();
    }

    @Test
    void givenMessagesList_Then_ReferenceShouldNotBeModified() {
        List<String> messageS1 = createMessage("", " ", "  ");
        List<String> messageS2 = createMessage("");
        List<String> messageS3 = createMessage(" ", " ");

        List<List<String>> messages = new ArrayList<>(3);
        messages.add(messageS1);
        messages.add(messageS2);
        messages.add(messageS3);

        String messageDecrypted = decrypt(messages);
        assertThat(messageDecrypted).isEmpty();
        assertThat(messageS1).hasSize(3);
        assertThat(messageS2).hasSize(1);
        assertThat(messageS3).hasSize(2);
    }

    private String decrypt(List<List<String>> messages) {
        return messageDecryptor.decript(messages);
    }

    private static List<String> createMessage(String... messages){
        return Stream.of(messages).collect(toList());
    }
}