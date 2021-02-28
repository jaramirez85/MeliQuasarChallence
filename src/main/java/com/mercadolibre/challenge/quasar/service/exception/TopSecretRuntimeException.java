package com.mercadolibre.challenge.quasar.service.exception;

public class TopSecretRuntimeException extends RuntimeException {

    public TopSecretRuntimeException(String message) {
        super(message);
    }
}
