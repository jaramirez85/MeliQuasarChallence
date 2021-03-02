package com.mercadolibre.challenge.quasar.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TopSecretRequest {
    private List<Satellite> satellites;
}
