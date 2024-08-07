package miller79.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import miller79.security.WithMockCustomUser;
import miller79.security.WithMockCustomUserSecurityContextFactory;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

@SpringBootTest
@AutoConfigureWebTestClient
class MainControllerTest {
    public static MockWebServer mockBackEnd;

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private ReactiveOAuth2AuthorizedClientManager authorizedClientManager;

    @MockBean
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @MockBean
    private ServerOAuth2AuthorizedClientRepository authorizedClientRepository;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry dynamicPropertyRegistry) throws IOException {
        dynamicPropertyRegistry
                .add("miller79.client.microservice-token-passthrough-base-url",
                        () -> "http://localhost:" + mockBackEnd.getPort());
        dynamicPropertyRegistry
                .add("miller79.client.microservice-o-auth2-client-base-url",
                        () -> "http://localhost:" + mockBackEnd.getPort());
    }

    @Test
    @WithMockCustomUser
    void testWithWebClientTokenPassthrough() throws Exception {
        mockBackEnd
                .enqueue(new MockResponse()
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                        .setResponseCode(200)
                        .setBody("Hello world"));

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
                recordedRequest.getHeader(HttpHeaders.AUTHORIZATION));
        assertEquals(HttpMethod.GET.toString(), recordedRequest.getMethod());
        assertEquals("/", recordedRequest.getPath());
    }

    @Test
    void testWithWebClientTokenPassthroughNoAuth() throws Exception {
        mockBackEnd
                .enqueue(new MockResponse()
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                        .setResponseCode(401));

        webClient.get().uri("/web-client-token-passthrough-no-auth").exchange().expectStatus().isUnauthorized();

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertFalse(recordedRequest.getHeaders().toMultimap().containsKey(HttpHeaders.AUTHORIZATION));
        assertEquals(HttpMethod.GET.toString(), recordedRequest.getMethod());
        assertEquals("/", recordedRequest.getPath());
    }

// TODO: Is this testable?
//    @Test
//    void testWithWebClientOAuth2ClientNoAuth() throws Exception {
//        mockBackEnd
//                .enqueue(new MockResponse()
//                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
//                        .setResponseCode(200)
//                        .setBody("Hello world"));
//
//        webClient
//                .get()
//                .uri("/web-client-oauth2-client-no-auth")
//                .exchange()
//                .expectStatus()
//                .is2xxSuccessful()
//                .expectBody(String.class)
//                .isEqualTo("Hello world");
//
//        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
//        assertEquals("Bearer myInternalToken", recordedRequest.getHeader(HttpHeaders.AUTHORIZATION));
//        assertEquals(HttpMethod.GET.toString(), recordedRequest.getMethod());
//        assertEquals("/", recordedRequest.getPath());
//    }

    // TODO: This test does not work for some reason
//    @Test
//    @WithMockCustomUser(authorities = { "permission:read" })
//    void testPreAuthReadWithSecurity() throws Exception {
//        webClient
//                .get()
//                .uri("/preauth-read")
//                .exchange()
//                .expectStatus()
//                .is2xxSuccessful()
//                .expectBody(String.class)
//                .isEqualTo("Hello world");
//    }

    @Test
    @WithMockCustomUser(authorities = { "security360-permission:EL - Spring Security:read" })
    void testSecurityConfigReadWithSecurity() throws Exception {
        webClient
                .get()
                .uri("/security-config-role-read")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(String.class)
                .isEqualTo("Hello world");
    }
}
