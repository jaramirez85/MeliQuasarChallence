package com.mercadolibre.challenge.quasar.service;

import com.mercadolibre.challenge.quasar.domain.*;
import com.mercadolibre.challenge.quasar.repository.SatelliteRepository;
import com.mercadolibre.challenge.quasar.service.exception.TopSecretException;
import com.mercadolibre.challenge.quasar.service.exception.TopSecretRuntimeException;
import com.mercadolibre.challenge.quasar.service.exception.UnknownSatelliteException;
import com.mercadolibre.challenge.quasar.service.location.Trilateration2DSolver;
import com.mercadolibre.challenge.quasar.service.location.exception.NegativePositionsException;
import com.mercadolibre.challenge.quasar.service.message.MessageDecryptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class TopSecretServiceImpl implements TopSecretService {

    @Autowired
    private final SatelliteRepository satelliteRepository;
    @Autowired
    private final MessageDecryptor messageDecryptor;
    @Autowired
    private final Trilateration2DSolver trilateration2DSolver;

    @Override
    public TopSecretResponse processData(TopSecretRequest topSecretRequest) throws TopSecretException {

        try{

            if(topSecretRequest.getSatellites().size() < 3)
                throw new TopSecretRuntimeException("The number of satellites must be 3");

            List<List<String>> allMessages = new ArrayList<>();
            double[] distance = new double[3];
            double[][] positions = new double[3][2];

            List<Satellite> satellites = topSecretRequest.getSatellites();
            for (int i = 0; i < satellites.size(); i++) {
                Satellite satellite = satellites.get(i);

                Optional<Satellite> satelliteInBD = satelliteRepository.findByName(satellite.getName());
                if (satelliteInBD.isEmpty())
                    throw new UnknownSatelliteException(String.format("Satellite [%s] is Unknown", satellite.getName()));

                allMessages.add(satellite.getMessage());
                distance[i] = satellite.getDistance();
                positions[i] = satelliteInBD.get().getPosition().toArray();
            }

            String messageDecrypted = messageDecryptor.decript(allMessages);
            double[] pointResolved = trilateration2DSolver.solve(positions, distance);

            if(existsIndeterminatePosition(pointResolved))
                throw new TopSecretRuntimeException("Indeterminate position");

            return TopSecretResponse.builder()
                    .position(new Position(pointResolved[0],pointResolved[1]))
                    .message(messageDecrypted)
                    .build();

        }catch (TopSecretRuntimeException ex){
            log.error(ex.getMessage());
            throw new TopSecretException(ex.getMessage(), ex);
        }

    }

    @Override
    public void updateSatellite(UpdateSatelliteRequest updateSatelliteRequest) throws TopSecretException {

        Optional<Satellite> satellite = satelliteRepository.findByName(updateSatelliteRequest.getName());
        if(satellite.isEmpty())
            throw new TopSecretException(String.format("Satellite \"%s\" not recognized. Only Kenobi, Skywalker and Sato are supported.", updateSatelliteRequest.getName()));

        if(updateSatelliteRequest.getDistance() < 0)
            throw new NegativePositionsException("distances should be greater than zero");

        satelliteRepository.save(Satellite.builder()
                .name(updateSatelliteRequest.getName())
                .message(updateSatelliteRequest.getMessage() == null ? satellite.get().getMessage() : updateSatelliteRequest.getMessage())
                .distance(updateSatelliteRequest.getDistance())
                .position(satellite.get().getPosition())
                .build());

    }

    @Override
    public TopSecretResponse tryProcessData() throws TopSecretException {
        List<Satellite> satellites = satelliteRepository.getAll();
        return processData(new TopSecretRequest(satellites));
    }


    private boolean existsIndeterminatePosition(double[] pointResolved) {
        return Double.isNaN(pointResolved[0]) || Double.isNaN(pointResolved[1]);
    }
}
