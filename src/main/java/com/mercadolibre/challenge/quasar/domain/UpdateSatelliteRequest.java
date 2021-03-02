package com.mercadolibre.challenge.quasar.domain;

import lombok.Data;

import java.util.List;

@Data
public class UpdateSatelliteRequest {
    private String name;
    private double distance;
    private List<String> message;
}
