package com.mercadolibre.challenge.quasar.service.message.exception;

import com.mercadolibre.challenge.quasar.service.exception.TopSecretRuntimeException;

public class IndeterminateMessageException extends TopSecretRuntimeException {

    public IndeterminateMessageException(String message) {
        super(message);
    }
}
