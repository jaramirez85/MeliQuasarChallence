package com.mercadolibre.challenge.quasar.service.location.exception;

import com.mercadolibre.challenge.quasar.service.exception.TopSecretRuntimeException;

public class InvalidSizeException extends TopSecretRuntimeException {

    public InvalidSizeException(String message) {
        super(message);
    }
}
