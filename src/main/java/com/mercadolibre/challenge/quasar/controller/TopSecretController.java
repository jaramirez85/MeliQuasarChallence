package com.mercadolibre.challenge.quasar.controller;

import com.mercadolibre.challenge.quasar.domain.Position;
import com.mercadolibre.challenge.quasar.domain.TopSecretRequest;
import com.mercadolibre.challenge.quasar.domain.TopSecretResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/topsecret")
public class TopSecretController {

    @GetMapping("/status")
    public String status(){
        return "topsecret endpoint is ok";
    }

    @PostMapping
    public TopSecretResponse topsecret(@RequestBody TopSecretRequest topSecretRequest) {
        return TopSecretResponse.builder()
                .position(Position.builder().x(1).y(2).build())
                .message(topSecretRequest.toString())
                .build();
    }

}
