package miller79;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the microservice that verify security enforcement end-to-end.
 *
 * <p>An "integration test" boots up the real Spring application (not just individual classes)
 * and tests it as a whole. These tests confirm that:
 * <ul>
 *   <li>An authenticated user (simulated via {@code @WithMockUser}) gets a 200 OK response
 *       with "Hello world"</li>
 *   <li>An unauthenticated request (no user/token) gets a 4xx error (access denied)</li>
 * </ul>
 *
 * <p>{@code @WithMockUser} is a Spring Security test helper that fakes a logged-in user
 * without needing a real OAuth2/JWT token. This lets us test security rules in isolation
 * from the actual identity provider (e.g., Keycloak).
 *
 * @see <a href=
 *      "https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html">Testing
 *      Spring Boot Applications</a>
 * @see <a href=
 *      "https://docs.spring.io/spring-security/reference/reactive/test/web/oauth2.html#page-title">Testing
 *      OAuth 2.0</a>
 */
@SpringBootTest
@AutoConfigureWebTestClient
@DisplayName("Microservice Integration Tests")
class ApplicationIT {
    @Autowired
    private WebTestClient webClient;

    /** Verifies that an authenticated user receives a 200 OK response with "Hello world." */
    @WithMockUser
    @Test
    void shouldReturnSuccess_whenAuthenticated() {
        webClient
                .get()
                .uri("/")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(String.class)
                .isEqualTo("Hello world");
    }

    /** Verifies that an unauthenticated request receives a 4xx client error (access denied). */
    @Test
    void shouldReturnClientError_whenNotAuthenticated() {
        webClient.get().uri("/").exchange().expectStatus().is4xxClientError();
    }

}
