package com.mercadolibre.challenge.quasar.repository;

import com.mercadolibre.challenge.quasar.domain.Position;
import com.mercadolibre.challenge.quasar.domain.Satellite;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Repository
public class SatelliteRepositoryInMemoryImpl implements SatelliteRepository {

    private static final Map<String, Satellite> SATELLITE_MAP = new ConcurrentHashMap<>();
    public static final String SATELLITE_KENOBI_NAME = "kenobi";
    public static final String SATELLITE_SKYWALKER_NAME = "skywalker";
    public static final String SATELLITE_SATO_NAME = "sato";

    static {
        initSatelliteInfo();
    }

    @Override
    public Optional<Satellite> findByName(String name) {
        return Optional.ofNullable(SATELLITE_MAP.get(name.toLowerCase()))
                .map(cloneSatellite());
    }

    @Override
    public List<Satellite> getAll() {
        return new ArrayList<>(SATELLITE_MAP.values());
    }

    @Override
    public void save(Satellite satellite) {
        SATELLITE_MAP.computeIfPresent(satellite.getName().toLowerCase(), (key, obj) -> satellite);
    }

    private Function<Satellite, Satellite> cloneSatellite() {
        return s -> Satellite.builder()
                .name(s.getName())
                .message(s.getMessage())
                .position(s.getPosition())
                .distance(s.getDistance())
                .build();
    }

    private static void initSatelliteInfo() {
        SATELLITE_MAP.put(SATELLITE_KENOBI_NAME, Satellite.builder()
                .name(SATELLITE_KENOBI_NAME)
                .message(new ArrayList<>())
                .position(new Position(-500, -200))
                .build());

        SATELLITE_MAP.put(SATELLITE_SKYWALKER_NAME, Satellite.builder()
                .name(SATELLITE_SKYWALKER_NAME)
                .message(new ArrayList<>())
                .position(new Position(100, -100))
                .build());

        SATELLITE_MAP.put(SATELLITE_SATO_NAME, Satellite.builder()
                .name(SATELLITE_SATO_NAME)
                .message(new ArrayList<>())
                .position(new Position(500, 100))
                .build());
    }
}
