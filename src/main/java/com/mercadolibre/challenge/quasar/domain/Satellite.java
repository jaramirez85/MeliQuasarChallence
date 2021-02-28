package com.mercadolibre.challenge.quasar.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Satellite {
    private String name;
    private double distance;
    private List<String> message;
    private Position position;
}
