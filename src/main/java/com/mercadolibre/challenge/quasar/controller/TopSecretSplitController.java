package com.mercadolibre.challenge.quasar.controller;

import com.mercadolibre.challenge.quasar.domain.UpdateSatelliteRequest;
import com.mercadolibre.challenge.quasar.service.TopSecretService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/topsecret_split")
public class TopSecretSplitController {

    @Autowired
    private final TopSecretService topSecretService;

    @PostMapping("/{satellite_name}")
    public ResponseEntity<String> updateSatellite(@PathVariable("satellite_name") String satelliteName, @RequestBody UpdateSatelliteRequest updateSatelliteRequest) {
        try {
            updateSatelliteRequest.setName(satelliteName);
            topSecretService.updateSatellite(updateSatelliteRequest);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex){
            return logAndCreateInternalErrorResponse(ex);
        }
    }


    @GetMapping
    public ResponseEntity<? extends Object> tryProcessData() {
        try {
            return new ResponseEntity<>(topSecretService.tryProcessData(), HttpStatus.OK);
        } catch (Exception ex){
            return logAndCreateInternalErrorResponse(ex);
        }
    }

    private ResponseEntity<String> logAndCreateInternalErrorResponse(Exception ex) {
        log.error("Internal Error", ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
