package com.mercadolibre.challenge.quasar.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;


@Builder
@Getter
public class Position {
    private final double x;
    private final double y;
}
