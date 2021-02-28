package com.mercadolibre.challenge.quasar.domain;

import lombok.Value;

@Value
public class Position {
    double x;
    double y;

    public double[] toArray(){
        return new double[]{x,y};
    }
}
