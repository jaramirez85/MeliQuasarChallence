package com.mercadolibre.challenge.quasar.controller;

import com.mercadolibre.challenge.quasar.domain.TopSecretResponse;
import com.mercadolibre.challenge.quasar.domain.UpdateSatelliteRequest;
import com.mercadolibre.challenge.quasar.service.TopSecretService;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully response"),
            @ApiResponse(code = 500, message = "Internal error", response = String.class)})
    @PostMapping("/{satellite_name}")
    public ResponseEntity<String> updateSatellite(@ApiParam(value = "Satellite name", required = true) @PathVariable("satellite_name") String satelliteName,
                                                  @ApiParam(value = "UpdateSatelliteRequest", required = true) @RequestBody UpdateSatelliteRequest updateSatelliteRequest) {
        try {
            updateSatelliteRequest.setName(satelliteName);
            topSecretService.updateSatellite(updateSatelliteRequest);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex){
            return logAndCreateInternalErrorResponse(ex);
        }
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully response", response = TopSecretResponse.class),
            @ApiResponse(code = 500, message = "Internal error", response = String.class)})
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
