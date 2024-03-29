package se.magnus.microservices.core.recommendation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.boot.test.context.SpringBootTest.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "10000")
class RecommendationServiceApplicationTests {

	@Autowired private WebTestClient client;
	@Test
	void getRecommendationsByProductId() {
		int productId = 1;

		client.get()
				.uri("/recommendation?productId=" + productId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
					.jsonPath("$.length()").isEqualTo(3)
					.jsonPath("$[0].productId").isEqualTo(productId);
	}

	@Test
	void getRecommendationsMissingParameter() {
		client.get()
				.uri("/recommendation")
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
					.jsonPath("$.path").isEqualTo("/recommendation")
					.jsonPath("$.message").isEqualTo("Required query parameter 'productId' is not present.");
	}

	@Test
	void getRecommendationsInvalidParameter() {
		client.get()
				.uri("/recommendation?productId=no-integer")
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isBadRequest()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
					.jsonPath("$.path").isEqualTo("/recommendation")
					.jsonPath("$.message").isEqualTo("Type mismatch.");

	}

	@Test
	void getRecommendationsNotFound() {
		int productIdNotFound = 113;

		client.get()
				.uri("/recommendation?productId="+ productIdNotFound)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.length()").isEqualTo(0);
	}

	@Test
	void getRecommendationsInvalidParameterNegativeValue(){
		int productIdInvalid = -1;

		client.get()
				.uri("/recommendation?productId="+productIdInvalid)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
					.jsonPath("$.path").isEqualTo("/recommendation")
				.jsonPath("$.message").isEqualTo("Invalid productId: "+productIdInvalid);
	}





}
