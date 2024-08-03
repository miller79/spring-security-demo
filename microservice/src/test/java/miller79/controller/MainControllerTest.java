package miller79.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
class MainControllerTest {
    @Autowired
    private WebTestClient webClient;

    @WithMockUser
    @Test
    void testServiceAuthenticated() {
        webClient
                .get()
                .uri("/")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(String.class)
                .isEqualTo("Hello World");
    }

    @Test
    void testServiceNotAuthenticated() {
        webClient.get().uri("/").exchange().expectStatus().is4xxClientError();
    }

}
