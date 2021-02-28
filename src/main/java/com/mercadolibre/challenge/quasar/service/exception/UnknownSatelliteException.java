package com.mercadolibre.challenge.quasar.service.exception;

public class UnknownSatelliteException extends TopSecretRuntimeException {

    public UnknownSatelliteException(String message) {
        super(message);
    }
}
