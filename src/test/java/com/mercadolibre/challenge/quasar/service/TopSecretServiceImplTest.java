package com.mercadolibre.challenge.quasar.service;

import com.mercadolibre.challenge.quasar.domain.*;
import com.mercadolibre.challenge.quasar.repository.SatelliteRepository;
import com.mercadolibre.challenge.quasar.service.exception.TopSecretException;
import com.mercadolibre.challenge.quasar.service.location.Trilateration2DSolver;
import com.mercadolibre.challenge.quasar.service.location.exception.NegativePositionsException;
import com.mercadolibre.challenge.quasar.service.message.MessageDecryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class TopSecretServiceImplTest {

    @Mock
    private SatelliteRepository satelliteRepository;
    @Mock
    private MessageDecryptor messageDecryptor;
    @Mock
    private Trilateration2DSolver trilateration2DSolver;
    @Captor
    private ArgumentCaptor<Satellite> satelliteCaptor;

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
        assertValidTopSecretResponse(response);
    }

    @Test
    void giverUnknownSatelliteForUpdate_Then_ThrowsException() {

        assertThatThrownBy(() -> {
            var request = new UpdateSatelliteRequest();
            request.setName("unknown");
            topSecretService.updateSatellite(request);
        })
                .isInstanceOf(TopSecretException.class)
                .hasMessage("Satellite \"unknown\" not recognized. Only Kenobi, Skywalker and Sato are supported.");
    }

    @Test
    void giverASatelliteWithNegativeDistanceForUpdate_Then_ThrowsException() {
        assertThatThrownBy(() -> {
            String satelliteName = "kenobi";
            var kenobi = Satellite.builder().name(satelliteName).build();
            when(satelliteRepository.findByName(satelliteName)).thenReturn(Optional.of(kenobi));

            var request = new UpdateSatelliteRequest();
            request.setName(satelliteName);
            request.setDistance(-1);
            topSecretService.updateSatellite(request);
        })
                .isInstanceOf(NegativePositionsException.class)
                .hasMessage("distances should be greater than zero");
    }

    @Test
    void giverValidSatelliteToUpdate_Then_InvokeRepository() throws TopSecretException {
        var satelliteName = "kenobi";
        var messages = Collections.singletonList("Hi");
        var position = new Position(42, 80);
        var kenobi = Satellite.builder().name(satelliteName).message(messages).position(position).build();
        when(satelliteRepository.findByName(satelliteName)).thenReturn(Optional.of(kenobi));

        var request = new UpdateSatelliteRequest();
        request.setName(satelliteName);
        request.setDistance(100);

        topSecretService.updateSatellite(request);
        verify(satelliteRepository).save(satelliteCaptor.capture());

        Satellite expected = Satellite.builder().name(satelliteName).distance(100).message(messages).position(position).build();
        Satellite actual = satelliteCaptor.getValue();

        assertThat(expected).isEqualTo(actual);

    }

    @Test
    void tryProcessDataTest() throws TopSecretException {
        var kenobi = Satellite.builder().name("kenobi").message(new ArrayList<>()).position(new Position(-500, -200)).build();
        var skywalker = Satellite.builder().name("skywalker").message(new ArrayList<>()).position(new Position(-400, -100)).build();
        var sato = Satellite.builder().name("sato").message(new ArrayList<>()).position(new Position(-300, -50)).build();
        var satellites = Arrays.asList(kenobi,skywalker,sato);

        mockServices(kenobi, skywalker, sato, new double[]{1, 2}, satellites);

        var response = topSecretService.tryProcessData();
        assertValidTopSecretResponse(response);

    }

    private void assertValidTopSecretResponse(TopSecretResponse response) {
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("message");
        assertThat(response.getPosition()).isEqualTo(new Position(1, 2));
    }

    private void mockServices(Satellite kenobi, Satellite skywalker, Satellite sato, double[] point, List<Satellite> satellites) {
        mockServices(kenobi, skywalker, sato, point);
        when(satelliteRepository.getAll()).thenReturn(satellites);
    }


    private void mockServices(Satellite kenobi, Satellite skywalker, Satellite sato, double[] point) {
        when(satelliteRepository.findByName("kenobi")).thenReturn(Optional.of(kenobi));
        when(satelliteRepository.findByName("skywalker")).thenReturn(Optional.of(skywalker));
        when(satelliteRepository.findByName("sato")).thenReturn(Optional.of(sato));
        when(messageDecryptor.decript(anyList())).thenReturn("message");
        when(trilateration2DSolver.solve(any(), any())).thenReturn(point);
    }


}