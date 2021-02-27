package com.mercadolibre.challenge.quasar.service.location;

import com.mercadolibre.challenge.quasar.service.location.exception.InvalidSizeException;
import com.mercadolibre.challenge.quasar.service.location.exception.NegativePositionsException;
import org.junit.jupiter.api.Test;

import static com.mercadolibre.challenge.quasar.service.location.Trilateration2DSolver.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Trilateration2DSolverTest {

    public static final double[][] VALID_POSITIONS = new double[][]{
            new double[]{4, 8},
            new double[]{11, 7},
            new double[]{16, 17}
    };

    @Test
    public void givenValidPositionAndDistancesThenReturnsCoordinates(){
        double[] distances = new double[]{5, 5, 10};
        double[] coordinates = solve(VALID_POSITIONS, distances);

        assertThat(coordinates).hasSize(2);
        assertThat(coordinates).isEqualTo(new double[]{8, 11});
    }

    @Test
    public void givenSamePositionsAndDistancesThenReturnsNaN(){
        double[][] positions = new double[][]{
                new double[]{10, 10},
                new double[]{10, 10},
                new double[]{10, 10}
        };
        double[] distances = new double[]{5, 5, 5};
        double[] coordinates = solve(positions, distances);

        assertThat(coordinates).hasSize(2);
        assertThat(coordinates).isEqualTo(new double[]{Double.NaN, Double.NaN});
    }

    @Test
    public void givenPositionsWithWrongSizeThenThrowsInvalidSizeException(){
        double[][] positions = new double[][]{
                new double[]{11,7},
                new double[]{16,17}
        };
        double[] distances = new double[]{5, 5, 5};

        assertThatThrownBy(() -> solve(positions, distances))
            .isInstanceOf(InvalidSizeException.class)
        .hasMessage("positions must have 3 coordinates");
    }

    @Test
    public void givenDistancesWithWrongSizeThenThrowsInvalidSizeException(){
        double[] distances = new double[]{5};
        assertThatThrownBy(() -> solve(VALID_POSITIONS, distances))
                .isInstanceOf(InvalidSizeException.class)
                .hasMessage("distances must have 3 values");
    }

    @Test
    public void givenNegativeDistancesThenThrowsNegativePositionsException(){
        double[] distances = new double[]{5, -5, 5};

        assertThatThrownBy(() -> solve(VALID_POSITIONS, distances))
                .isInstanceOf(NegativePositionsException.class)
                .hasMessage("distances should be greater than zero");
    }

}