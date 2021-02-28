package com.mercadolibre.challenge.quasar.service.location;

import com.mercadolibre.challenge.quasar.service.location.exception.InvalidSizeException;
import com.mercadolibre.challenge.quasar.service.location.exception.NegativePositionsException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 *
 * Based on the 2D trilateral concept where
 * if the coordinates of three points (A, B and C) are known
 * and their distances from a fourth point U (with unknown coordinates) are also known,
 * the we can determinate the coordinates of point U.
 *
 * @See <a href = "https://books.google.co.uk/books?id=Ki2DMaeeHpUC&pg=PA78#v=onepage&q&f=false" />
 * @See <a href = "https://math.stackexchange.com/questions/884807/find-x-location-using-3-known-x-y-location-using-trilateration/884851#884851?newreg=bd7d92b8caba4dd188df0488c32668b8" />
 * @author Javier Ramirez
 */
@Component
public class Trilateration2DSolverImpl implements Trilateration2DSolver {

    @Override
    public double[] solve(double[][] positions, double[] distances) {

        validateData(positions, distances);

        double x1 = positions[0][0];
        double y1 = positions[0][1];

        double x2 = positions[1][0];
        double y2 = positions[1][1];

        double x3 = positions[2][0];
        double y3 = positions[2][1];


        double r1 = distances[0];
        double r2 = distances[1];
        double r3 = distances[2];


        double A = -2 * x1 + 2 * x2;
        double B = -2 * y1 + 2 * y2;

        double C = square(r1) - square(r2) - square(x1) + square(x2) - square(y1) + square(y2);

        double D = -2 * x2 + 2 * x3;
        double E = -2 * y2 + 2 * y3;

        double F = square(r2) - square(r3) - square(x2) + square(x3) - square(y2) + square(y3);

        double x = (C * E - F * B) / (E * A - B * D);
        double y = (C * D - A * F) / (B * D - A * E);

        return new double[] {x, y};
    }

    private static void validateData(double[][] positions, double[] distances) {
        if(positions.length != 3)
            throw new InvalidSizeException("positions must have 3 coordinates");
        if(distances.length != 3)
            throw new InvalidSizeException("distances must have 3 values");
        if(Arrays.stream(distances).filter(d -> d < 0.0).findAny().isPresent())
            throw new NegativePositionsException("distances should be greater than zero");
    }

    private static double square(double number) {
        return number * number;
    }

}