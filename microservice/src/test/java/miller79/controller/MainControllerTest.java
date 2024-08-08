package miller79.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @see <a href=
 *      "https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html">Testing
 *      Spring Boot Applications</a>
 * @see <a href=
 *      "https://docs.spring.io/spring-security/reference/reactive/test/web/oauth2.html#page-title">Testing
 *      OAuth 2.0</a>
 */
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
