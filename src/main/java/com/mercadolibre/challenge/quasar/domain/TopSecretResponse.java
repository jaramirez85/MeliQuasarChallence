package com.mercadolibre.challenge.quasar.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Builder
@Getter
public class TopSecretResponse {
    private final Position position;
    private final String message;
}
