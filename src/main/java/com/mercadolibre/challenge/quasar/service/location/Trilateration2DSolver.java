package com.mercadolibre.challenge.quasar.service.location;

public interface Trilateration2DSolver {
    double[] solve(double[][] positions, double[] distances);
}
