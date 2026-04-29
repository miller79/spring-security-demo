package miller79;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.headerDoesNotExist;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.restclient.test.MockServerRestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import miller79.security.WithMockCustomUser;
import miller79.security.WithMockCustomUserSecurityContextFactory;

/**
 * Integration tests for the servlet-based main API that verify all security scenarios end-to-end.
 *
 * <p>These tests boot up the full Spring application and exercise every endpoint through
 * {@code MockMvc} (a test utility that simulates HTTP requests without starting a real server).
 * Downstream microservice calls are intercepted using {@code MockRestServiceServer} and
 * {@code MockServerRestClientCustomizer} so no real network calls are made.
 *
 * <p>Scenarios tested include:
 * <ul>
 *   <li>Token passthrough — verifying the user's JWT is forwarded to the downstream service
 *       via both RestClient and RestTemplate</li>
 *   <li>OAuth2 client credentials — verifying a machine token is used when no user is authenticated</li>
 *   <li>PreAuthorize role checks — verifying access is granted or denied based on permissions</li>
 *   <li>Security config role checks — verifying URL-based authorization rules</li>
 *   <li>Field-level security — verifying sensitive fields are masked, partially masked, or fully
 *       visible depending on the caller's authorities</li>
 * </ul>
 *
 * <p>The inner class {@code MainControllerTestConfiguration} sets up the mock HTTP servers used
 * to simulate downstream microservice responses.
 *
 * @see <a href=
 *      "https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html">Testing
 *      Spring Boot Applications</a>
 * @see <a href=
 *      "https://docs.spring.io/spring-security/reference/servlet/test/method.html#test-method-withuserdetails">Testing
 *      Method Security</a>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
@DisplayName("Main API Servlet Integration Tests")
class ApplicationIT {
    @Autowired
    private MockServerRestClientCustomizer customizer;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private RestClient.Builder microserviceTokenPassthroughRestClientBuilder;

    @Autowired
    private MockRestServiceServer microserviceTokenPassthroughRestTemplateServer;

    @Autowired
    private RestClient.Builder microserviceOAuth2ClientRestClientBuilder;

    @MockitoBean
    private OAuth2AuthorizedClientManager authorizedClientManager;

    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @MockitoBean
    private OAuth2AuthorizedClientService authorizedClientService;

    /**
     * Test-only configuration that sets up mock HTTP servers to simulate downstream
     * microservice responses during integration tests.
     */
    @TestConfiguration
    public static class MainControllerTestConfiguration {
        /** Creates a mock HTTP server for intercepting RestTemplate token passthrough calls. */
        @Bean
        MockRestServiceServer microserviceTokenPassthroughRestTemplateServer(
                RestTemplate microserviceTokenPassthroughRestTemplate) {
            return MockRestServiceServer.createServer(microserviceTokenPassthroughRestTemplate);
        }

        /** Creates a mock server customizer for intercepting RestClient calls during tests. */
        @Bean
        MockServerRestClientCustomizer mockServerRestClientCustomizer() {
            return new MockServerRestClientCustomizer();
        }
    }

    /** Resets the mock HTTP servers before each test to ensure a clean state. */
    @BeforeEach
    void init() {
        doReturn(new OAuth2AuthorizedClient(
                ClientRegistration
                        .withRegistrationId("test")
                        .authorizationGrantType(AuthorizationGrantType.JWT_BEARER)
                        .build(),
                "me", new OAuth2AccessToken(TokenType.BEARER, "myInternalToken", Instant.now(),
                        Instant.now().plusSeconds(300000L)))).when(authorizedClientManager).authorize(Mockito.any());
    }

    /** Verifies all expected mock HTTP calls were made after each test. */
    @AfterEach
    void cleanUpEach() {
        customizer.getServers().values().forEach(MockRestServiceServer::reset);
    }

    /** Verifies that RestClient token passthrough forwards the user's JWT and returns the microservice response. */
    @Test
    @WithMockCustomUser
    void shouldPassthroughToken_whenRestClientWithAuthenticatedUser() throws Exception {
        customizer
                .getServer(microserviceTokenPassthroughRestClientBuilder)
                .expect(requestTo("http://localhost/"))
                .andExpect(header(HttpHeaders.AUTHORIZATION,
                        "Bearer " + WithMockCustomUserSecurityContextFactory.getJwt().getTokenValue()))
                .andRespond(withSuccess("Hello world", MediaType.TEXT_PLAIN));

        mvc
                .perform(get("/rest-client-token-passthrough"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("Hello world"));
    }

    /** Verifies that RestTemplate token passthrough forwards the user's JWT and returns the microservice response. */
    @Test
    @WithMockCustomUser
    void shouldPassthroughToken_whenRestTemplateWithAuthenticatedUser() throws Exception {
        microserviceTokenPassthroughRestTemplateServer
                .expect(requestTo("http://localhost/"))
                .andExpect(header(HttpHeaders.AUTHORIZATION,
                        "Bearer " + WithMockCustomUserSecurityContextFactory.getJwt().getTokenValue()))
                .andRespond(withSuccess("Hello world", MediaType.TEXT_PLAIN));

        mvc
                .perform(get("/rest-template-token-passthrough"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("Hello world"));
    }

    /** Verifies that the no-auth token passthrough endpoint works without requiring caller authentication. */
    @Test
    void shouldReturnUnauthorized_whenRestClientPassthroughWithNoAuth() throws Exception {
        customizer
                .getServer(microserviceTokenPassthroughRestClientBuilder)
                .expect(requestTo("http://localhost/"))
                .andExpect(headerDoesNotExist(HttpHeaders.AUTHORIZATION))
                .andRespond(withUnauthorizedRequest());

        mvc.perform(get("/rest-client-token-passthrough-no-auth")).andExpect(status().isUnauthorized());
    }

    /** Verifies that the OAuth2 client credentials endpoint obtains its own token for the downstream call. */
    @Test
    void shouldUseOAuth2ClientToken_whenRestClientWithNoUserAuth() throws Exception {
        customizer
                .getServer(microserviceOAuth2ClientRestClientBuilder)
                .expect(requestTo("http://localhost/"))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer myInternalToken"))
                .andRespond(withSuccess("Hello world", MediaType.TEXT_PLAIN));

        mvc
                .perform(get("/rest-client-oauth2-client-no-auth"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("Hello world"));
    }

    /** Verifies that a user with the {@code permission:read} authority can access the pre-auth endpoint. */
    @Test
    @WithMockCustomUser(authorities = { "permission:read" })
    void shouldAllowRead_whenPreAuthorizedWithReadPermission() throws Exception {
        mvc
                .perform(get("/preauth-read"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("Hello world"));
    }

    /** Verifies that a user with the {@code permission:read} authority can access the URL-restricted endpoint. */
    @Test
    @WithMockCustomUser(authorities = { "permission:read" })
    void shouldAllowRead_whenSecurityConfigRoleWithReadPermission() throws Exception {
        mvc
                .perform(get("/security-config-role-read"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("Hello world"));
    }

    /** Verifies that a user without the {@code permission:read} authority is denied access (403). */
    @Test
    @WithMockCustomUser
    void shouldReturnForbidden_whenPreAuthWithoutReadPermission() throws Exception {
        mvc.perform(get("/preauth-read")).andExpect(status().isForbidden());
    }

    /** Verifies that a user without the {@code permission:read} authority is denied access via URL-based rules. */
    @Test
    @WithMockCustomUser
    void shouldReturnForbidden_whenSecurityConfigRoleWithoutReadPermission() throws Exception {
        mvc.perform(get("/security-config-role-read")).andExpect(status().isForbidden());
    }

    /** Verifies that an unauthenticated request to the semi-secure endpoint returns a 401/403 error. */
    @Test
    void shouldReturnClientError_whenFetchingSemiSecureObjectWithNoAuth() throws Exception {
        mvc.perform(get("/fetch-semi-secure-object")).andExpect(status().is4xxClientError());
    }

    /** Verifies that sensitive fields are masked when the caller lacks the {@code name:miller79} authority. */
    @Test
    @WithMockCustomUser
    void shouldMaskSensitiveFields_whenFetchingSemiSecureObjectWithPartialAuth() throws Exception {
        mvc.perform(get("/fetch-semi-secure-object")).andExpect(status().is2xxSuccessful()).andExpect(content().json("""
                    {
                        "name": "Secret Person",
                        "ssn": "***",
                        "phoneNumber": "***-***-7890"
                    }
                """));
    }

    /** Verifies that all fields are visible when the caller has the {@code name:miller79} authority. */
    @Test
    @WithMockCustomUser(authorities = { "name:miller79" })
    void shouldReturnUnmaskedFields_whenFetchingSemiSecureObjectWithFullAuth() throws Exception {
        mvc.perform(get("/fetch-semi-secure-object")).andExpect(status().is2xxSuccessful()).andExpect(content().json("""
                    {
                        "name": "Secret Person",
                        "ssn": "123-45-6789",
                        "phoneNumber": "123-456-7890"
                    }
                """));
    }
}
