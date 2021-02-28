package com.mercadolibre.challenge.quasar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.challenge.quasar.controller.TopSecretController;
import com.mercadolibre.challenge.quasar.domain.TopSecretRequest;
import com.mercadolibre.challenge.quasar.domain.TopSecretResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class QuasarApplicationTests {

	@Autowired
	private TopSecretController topSecretController;

	@Test
	void topSecret_Integration_Ok_Test() throws IOException {
		TopSecretRequest topSecretRequest = createTopSecretRequest("requests/topsecretRequestOk.json");

		ResponseEntity<TopSecretResponse> responseEntity = topSecretController.topSecret(topSecretRequest);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseEntity.getBody().getMessage()).isEqualTo("este es un mensaje secreto");
		assertThat(responseEntity.getBody().getPosition().getX()).isEqualTo(-487.2859125);
		assertThat(responseEntity.getBody().getPosition().getY()).isEqualTo(1557.014225);
	}

	@Test
	void topSecret_Integration_404_Test() throws IOException {
		TopSecretRequest topSecretRequest = createTopSecretRequest("requests/topsecretRequest404.json");

		ResponseEntity<TopSecretResponse> responseEntity = topSecretController.topSecret(topSecretRequest);
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(String.valueOf(responseEntity.getBody())).isEqualTo("The number of satellites must be 3");
	}

	private TopSecretRequest createTopSecretRequest(String fileName) throws IOException {
		String requestBody = readFile(fileName);
		return new ObjectMapper().readValue(requestBody, TopSecretRequest.class);
	}

	private String readFile(String fileName) throws IOException {
		File resource = new ClassPathResource(fileName).getFile();
		return new String(Files.readAllBytes(resource.toPath()));
	}

}
