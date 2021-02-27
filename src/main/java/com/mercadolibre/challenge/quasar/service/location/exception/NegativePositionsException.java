package com.mercadolibre.challenge.quasar.service.location.exception;

public class NegativePositionsException extends RuntimeException {

    public NegativePositionsException(String message) {
        super(message);
    }
}
