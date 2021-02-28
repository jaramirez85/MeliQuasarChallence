package com.mercadolibre.challenge.quasar.controller;

import com.mercadolibre.challenge.quasar.domain.TopSecretRequest;
import com.mercadolibre.challenge.quasar.domain.TopSecretResponse;
import com.mercadolibre.challenge.quasar.service.TopSecretService;
import com.mercadolibre.challenge.quasar.service.exception.TopSecretException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/topsecret")
@Slf4j
public class TopSecretController {

    @Autowired
    private final TopSecretService topSecretService;

    @GetMapping("/status")
    public String status(){
        return "topsecret endpoint is ok";
    }

    @PostMapping
    public ResponseEntity<TopSecretResponse> topSecret(@RequestBody TopSecretRequest topSecretRequest) {
        try{
            return new ResponseEntity<>(topSecretService.processData(topSecretRequest), HttpStatus.OK);
        }catch (TopSecretException ex){
            return new ResponseEntity(ex.getMessage(), HttpStatus.NOT_FOUND);
        }catch (Exception ex){
            log.error("Internal Error", ex);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
