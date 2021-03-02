package com.mercadolibre.challenge.quasar.service;

import com.mercadolibre.challenge.quasar.domain.TopSecretRequest;
import com.mercadolibre.challenge.quasar.domain.TopSecretResponse;
import com.mercadolibre.challenge.quasar.domain.UpdateSatelliteRequest;
import com.mercadolibre.challenge.quasar.service.exception.TopSecretException;

public interface TopSecretService {

    TopSecretResponse processData(TopSecretRequest topSecretRequest) throws TopSecretException;
    void updateSatellite(UpdateSatelliteRequest updateSatelliteRequest) throws TopSecretException;
    TopSecretResponse tryProcessData() throws TopSecretException;

}
