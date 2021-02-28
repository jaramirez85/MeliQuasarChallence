package com.mercadolibre.challenge.quasar.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TopSecretRequest {
    private List<Satellite> satellites = new ArrayList<>();
}
