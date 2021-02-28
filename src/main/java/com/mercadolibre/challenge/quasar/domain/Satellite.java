package com.mercadolibre.challenge.quasar.domain;

import lombok.Data;

import java.util.List;

@Data
public class Satellite {
    private final String name;
    private final double distance;
    private final List<String> message;
}
