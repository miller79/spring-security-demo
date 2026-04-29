package miller79;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import java.io.IOException;
import java.time.Instant;
import java.net.InetAddress;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.TestSocketUtils;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;

import miller79.security.WithMockCustomUser;
import miller79.security.WithMockCustomUserSecurityContextFactory;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;

/**
 * Integration tests for the reactive main API that verify all security scenarios end-to-end.
 *
 * <p>These tests boot up the full reactive Spring application and exercise every endpoint through
 * {@link org.springframework.test.web.reactive.server.WebTestClient WebTestClient}. Unlike the
 * servlet module (which uses {@code MockRestServiceServer}), the reactive module uses
 * {@code MockWebServer} from the OkHttp/MockWebServer3 library — a lightweight HTTP server
 * that runs locally and returns pre-configured responses.
 *
 * <p>Scenarios tested include:
 * <ul>
 *   <li>Token passthrough — verifying the user's JWT is forwarded via WebClient</li>
 *   <li>OAuth2 client credentials — verifying a machine token is used</li>
 *   <li>PreAuthorize role checks — verifying access is granted or denied</li>
 *   <li>URL-based authorization — verifying security configuration rules</li>
 *   <li>Field-level security — verifying sensitive fields are masked or visible</li>
 * </ul>
 *
 * <p>{@code @DynamicPropertySource} overrides the downstream service URLs at test time to point
 * at the local MockWebServer instead of a real microservice.
 *
 * @see <a href=
 *      "https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html">Testing
 *      Spring Boot Applications</a>
 * @see <a href=
 *      "https://docs.spring.io/spring-security/reference/reactive/test/method.html">Testing
 *      Method Security</a>
 */
@SpringBootTest
@AutoConfigureWebTestClient
@DisplayName("Main API Reactive Integration Tests")
class ApplicationIT {
    private static final int MOCK_BACK_END_PORT = TestSocketUtils.findAvailableTcpPort();

    private MockWebServer mockBackEnd;

    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    private ReactiveOAuth2AuthorizedClientManager authorizedClientManager;

    @MockitoBean
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @MockitoBean
    private ServerOAuth2AuthorizedClientRepository authorizedClientRepository;

    /** Starts the MockWebServer before each test. */
    @BeforeEach
    void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start(InetAddress.getByName("localhost"), MOCK_BACK_END_PORT);

        Mockito.doReturn(Mono.just(new OAuth2AuthorizedClient(
                ClientRegistration
                        .withRegistrationId("test")
                        .authorizationGrantType(AuthorizationGrantType.JWT_BEARER)
                        .build(),
                "me", new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "myInternalToken", Instant.now(),
                        Instant.now().plusSeconds(300000L))))).when(authorizedClientManager).authorize(any());
    }

    /** Shuts down the MockWebServer after each test. */
    @AfterEach
    void tearDown() throws IOException {
        mockBackEnd.close();
    }

    /**
     * Overrides the downstream service URLs to point at the local MockWebServer.
     *
     * @param dynamicPropertyRegistry the registry for dynamic test properties
     */
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry dynamicPropertyRegistry) throws IOException {
        dynamicPropertyRegistry
                .add("miller79.client.microservice-token-passthrough-base-url",
                        () -> "http://localhost:" + MOCK_BACK_END_PORT);
        dynamicPropertyRegistry
                .add("miller79.client.microservice-o-auth2-client-base-url",
                        () -> "http://localhost:" + MOCK_BACK_END_PORT);
    }

    /** Verifies that WebClient token passthrough forwards the user's JWT and returns the microservice response. */
    @Test
    @WithMockCustomUser
    void shouldPassthroughToken_whenWebClientWithAuthenticatedUser() throws Exception {
        mockBackEnd
                .enqueue(new MockResponse.Builder()
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                        .code(200)
                        .body("Hello world")
                        .build());

        webClient
                .get()
                .uri("/web-client-token-passthrough")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(String.class)
                .isEqualTo("Hello world");

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("Bearer " + WithMockCustomUserSecurityContextFactory.getJwt().getTokenValue(),
                recordedRequest.getHeaders().get(HttpHeaders.AUTHORIZATION));
        assertEquals(HttpMethod.GET.toString(), recordedRequest.getMethod());
        assertEquals("/", recordedRequest.getUrl().encodedPath());
    }

    /** Verifies that the no-auth token passthrough endpoint works without requiring caller authentication. */
    @Test
    void shouldReturnUnauthorized_whenWebClientPassthroughWithNoAuth() throws Exception {
        mockBackEnd
                .enqueue(new MockResponse.Builder()
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                        .code(401)
                        .build());

        webClient.get().uri("/web-client-token-passthrough-no-auth").exchange().expectStatus().isUnauthorized();

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertFalse(recordedRequest.getHeaders().toMultimap().containsKey(HttpHeaders.AUTHORIZATION));
        assertEquals(HttpMethod.GET.toString(), recordedRequest.getMethod());
        assertEquals("/", recordedRequest.getUrl().encodedPath());
    }

    /** Verifies that the OAuth2 client credentials endpoint obtains its own token for the downstream call. */
    @Test
    void shouldUseOAuth2ClientToken_whenWebClientWithNoUserAuth() throws Exception {
        mockBackEnd
                .enqueue(new MockResponse.Builder()
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                        .code(200)
                        .body("Hello world")
                        .build());

        webClient
                .get()
                .uri("/web-client-oauth2-client-no-auth")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(String.class)
                .isEqualTo("Hello world");

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("Bearer myInternalToken",
                recordedRequest.getHeaders().get(HttpHeaders.AUTHORIZATION));
        assertEquals(HttpMethod.GET.toString(), recordedRequest.getMethod());
        assertEquals("/", recordedRequest.getUrl().encodedPath());
    }

    /** Verifies that a user with the {@code permission:read} authority can access the pre-auth endpoint. */
    @Test
    @WithMockCustomUser(authorities = { "permission:read" })
    void shouldAllowRead_whenPreAuthorizedWithReadPermission() throws Exception {
        webClient
                .get()
                .uri("/preauth-read")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(String.class)
                .isEqualTo("Hello world");
    }

    /** Verifies that a user with the {@code permission:read} authority can access the URL-restricted endpoint. */
    @Test
    @WithMockCustomUser(authorities = { "permission:read" })
    void shouldAllowRead_whenSecurityConfigRoleWithReadPermission() throws Exception {
        webClient
                .get()
                .uri("/security-config-role-read")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(String.class)
                .isEqualTo("Hello world");
    }

    /** Verifies that a user without the {@code permission:read} authority is denied access (403). */
    @Test
    @WithMockCustomUser
    void shouldReturnForbidden_whenPreAuthWithoutReadPermission() throws Exception {
        webClient
                .get()
                .uri("/preauth-read")
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    /** Verifies that a user without the {@code permission:read} authority is denied access via URL-based rules. */
    @Test
    @WithMockCustomUser
    void shouldReturnForbidden_whenSecurityConfigRoleWithoutReadPermission() throws Exception {
        webClient
                .get()
                .uri("/security-config-role-read")
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    /** Verifies that an unauthenticated request to the semi-secure endpoint returns a 401 error. */
    @Test
    void shouldReturnUnauthorized_whenFetchingSemiSecureObjectWithNoAuth() throws Exception {
        webClient
                .get()
                .uri("/fetch-semi-secure-object")
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    /** Verifies that sensitive fields are masked when the caller lacks the {@code name:miller79} authority. */
    @Test
    @WithMockCustomUser
    void shouldMaskSensitiveFields_whenFetchingSemiSecureObjectWithPartialAuth() throws Exception {
        webClient
                .get()
                .uri("/fetch-semi-secure-object")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Secret Person")
                .jsonPath("$.ssn").isEqualTo("***")
                .jsonPath("$.phoneNumber").isEqualTo("***-***-7890");
    }

    /** Verifies that all fields are visible when the caller has the {@code name:miller79} authority. */
    @Test
    @WithMockCustomUser(authorities = { "name:miller79" })
    void shouldReturnUnmaskedFields_whenFetchingSemiSecureObjectWithFullAuth() throws Exception {
        webClient
                .get()
                .uri("/fetch-semi-secure-object")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Secret Person")
                .jsonPath("$.ssn").isEqualTo("123-45-6789")
                .jsonPath("$.phoneNumber").isEqualTo("123-456-7890");
    }
}
