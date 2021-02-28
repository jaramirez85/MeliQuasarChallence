package com.mercadolibre.challenge.quasar.service;

import com.mercadolibre.challenge.quasar.domain.Position;
import com.mercadolibre.challenge.quasar.domain.Satellite;
import com.mercadolibre.challenge.quasar.domain.TopSecretRequest;
import com.mercadolibre.challenge.quasar.domain.TopSecretResponse;
import com.mercadolibre.challenge.quasar.repository.SatelliteRepository;
import com.mercadolibre.challenge.quasar.service.exception.TopSecretException;
import com.mercadolibre.challenge.quasar.service.location.Trilateration2DSolver;
import com.mercadolibre.challenge.quasar.service.message.MessageDecryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class TopSecretServiceImplTest {

    @Mock
    private SatelliteRepository satelliteRepository;
    @Mock
    private MessageDecryptor messageDecryptor;
    @Mock
    private Trilateration2DSolver trilateration2DSolver;

    private TopSecretService topSecretService;

    @BeforeEach
    void init() {
        topSecretService = new TopSecretServiceImpl(satelliteRepository, messageDecryptor, trilateration2DSolver);
    }

    @Test
    void givenEmptySatellitesThenThrowsException() {
        List<Satellite> satellites = new ArrayList<>();
        var topSecretRequest = new TopSecretRequest();
        topSecretRequest.setSatellites(satellites);

        assertThatThrownBy(() -> topSecretService.processData(topSecretRequest))
                .isInstanceOf(TopSecretException.class)
                .hasMessage("The number of satellites must be 3");
    }

    @Test
    void givenUnknownSatellitesThenThrowsException() {
        List<Satellite> satellites = Arrays.asList(
                Satellite.builder().name("stranger 1").build(),
                Satellite.builder().name("stranger 2").build(),
                Satellite.builder().name("stranger 3").build());

        var topSecretRequest = new TopSecretRequest();
        topSecretRequest.setSatellites(satellites);

        assertThatThrownBy(() -> topSecretService.processData(topSecretRequest))
                .isInstanceOf(TopSecretException.class)
                .hasMessage("Satellite [stranger 1] is Unknown");
    }

    @Test
    void givenKnownSatellitesButWithIndeterminatePosition_Then_ThrowsException() {
        var kenobi = Satellite.builder().name("kenobi").message(new ArrayList<>()).position(new Position(-500, -200)).build();
        var skywalker = Satellite.builder().name("skywalker").message(new ArrayList<>()).position(new Position(-400, -100)).build();
        var sato = Satellite.builder().name("sato").message(new ArrayList<>()).position(new Position(-300, -50)).build();
        var satellites = Arrays.asList(kenobi,skywalker,sato);

        mockServices(kenobi, skywalker, sato, new double[]{Double.NaN, Double.NaN});

        var topSecretRequest = new TopSecretRequest();
        topSecretRequest.setSatellites(satellites);

        assertThatThrownBy(() -> topSecretService.processData(topSecretRequest))
                .isInstanceOf(TopSecretException.class)
                .hasMessage("Indeterminate position");
    }

    @Test
    void givenValidSatellitesAndDataOk_Then_ReturnResponseOk() throws TopSecretException {
        var kenobi = Satellite.builder().name("kenobi").message(new ArrayList<>()).position(new Position(-500, -200)).build();
        var skywalker = Satellite.builder().name("skywalker").message(new ArrayList<>()).position(new Position(-400, -100)).build();
        var sato = Satellite.builder().name("sato").message(new ArrayList<>()).position(new Position(-300, -50)).build();
        var satellites = Arrays.asList(kenobi,skywalker,sato);

        mockServices(kenobi, skywalker, sato, new double[]{1, 2});

        var topSecretRequest = new TopSecretRequest();
        topSecretRequest.setSatellites(satellites);

        var response = topSecretService.processData(topSecretRequest);
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("message");
        assertThat(response.getPosition()).isEqualTo(new Position(1, 2));
    }

    private void mockServices(Satellite kenobi, Satellite skywalker, Satellite sato, double[] point) {
        when(satelliteRepository.findByName("kenobi")).thenReturn(Optional.of(kenobi));
        when(satelliteRepository.findByName("skywalker")).thenReturn(Optional.of(skywalker));
        when(satelliteRepository.findByName("sato")).thenReturn(Optional.of(sato));
        when(messageDecryptor.decript(anyList())).thenReturn("message");
        when(trilateration2DSolver.solve(any(), any())).thenReturn(point);
    }

}