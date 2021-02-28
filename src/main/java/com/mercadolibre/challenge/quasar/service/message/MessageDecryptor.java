package com.mercadolibre.challenge.quasar.service.message;

import java.util.List;

public interface MessageDecryptor {

    String decript(List<List<String>> messagesParam);

}
