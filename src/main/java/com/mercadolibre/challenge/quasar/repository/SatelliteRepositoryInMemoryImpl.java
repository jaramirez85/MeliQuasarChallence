package com.mercadolibre.challenge.quasar.repository;

import com.mercadolibre.challenge.quasar.domain.Position;
import com.mercadolibre.challenge.quasar.domain.Satellite;
import lombok.Getter;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SatelliteRepositoryInMemoryImpl implements SatelliteRepository {

    private static final Map<String, Satellite> SATELLITE_MAP = new ConcurrentHashMap<>();

    static {
        initSatelliteInfo();
    }

    @Override
    public Optional<Satellite> findByName(String name) {
        return Optional.ofNullable(SATELLITE_MAP.get(name.toLowerCase()));
    }

    private static void initSatelliteInfo() {
        SATELLITE_MAP.put("kenobi", Satellite.builder()
                .name("kenobi")
                .position(new Position(-500, -200))
                .build());

        SATELLITE_MAP.put("skywalker", Satellite.builder()
                .name("skywalker")
                .position(new Position(100, -100))
                .build());

        SATELLITE_MAP.put("sato", Satellite.builder()
                .name("sato")
                .position(new Position(500, 100))
                .build());
    }
}
