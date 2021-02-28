package com.mercadolibre.challenge.quasar.service.location.exception;

import com.mercadolibre.challenge.quasar.service.exception.TopSecretRuntimeException;

public class NegativePositionsException extends TopSecretRuntimeException {

    public NegativePositionsException(String message) {
        super(message);
    }
}
