package com.mercadolibre.challenge.quasar.repository;

import com.mercadolibre.challenge.quasar.domain.Satellite;

import java.util.List;
import java.util.Optional;

public interface SatelliteRepository {

    Optional<Satellite> findByName(String name);

    List<Satellite> getAll();

    void save(Satellite satellite);

}
