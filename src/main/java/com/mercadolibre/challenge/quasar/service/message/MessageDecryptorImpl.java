package com.mercadolibre.challenge.quasar.service.message;

import com.mercadolibre.challenge.quasar.service.message.exception.IndeterminateMessageException;
import com.mercadolibre.challenge.quasar.utils.CloneUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * @author Javier Ramirez
 */
@Component
public class MessageDecryptorImpl implements MessageDecryptor {

    @Override
    public String decript(List<List<String>> messagesParam){

        if(messagesParam.size() != 3)
            throw new IllegalArgumentException("Message array must be 3 rows");

        List<List<String>> messages = CloneUtils.copyList(messagesParam);
        int maxColumnSize = messages.stream().mapToInt(List::size).max().orElse(0);

        matchColumns(messages, maxColumnSize);

        List<String> finalMessage = new ArrayList<>(0);
        IntStream.range(0, maxColumnSize).forEach(i -> {
            List<String> columnsMessages = Stream.of(messages.get(0).get(i), messages.get(1).get(i), messages.get(2).get(i))
                    .map(String::toLowerCase)
                    .map(String::trim)
                    .filter(s -> !s.trim().isEmpty())
                    .distinct()
                    .collect(toList());
            if (columnsMessages.size() > 1)
                throw new IndeterminateMessageException(String.format("Different Strings in the same column(%d) [%s]", i, String.join(" - ", columnsMessages)));
            if (!columnsMessages.isEmpty())
                finalMessage.add(columnsMessages.get(0));
        });

        return String.join(" ", finalMessage);
    }

    private static void matchColumns(List<List<String>> messages, int maxSize) {
        messages.stream().filter(allMessage -> allMessage.size() < maxSize).forEachOrdered(messageBySatellite -> {
            int elementsToCreate = maxSize - messageBySatellite.size();
            IntStream.range(0, elementsToCreate).forEach(j -> messageBySatellite.add(0, ""));
        });
    }
}
