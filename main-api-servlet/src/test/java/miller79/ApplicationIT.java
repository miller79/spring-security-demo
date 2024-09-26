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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.MockServerRestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import miller79.interceptor.OAuth2ClientHttpRequestInterceptor;
import miller79.security.WithMockCustomUser;
import miller79.security.WithMockCustomUserSecurityContextFactory;

/**
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

    @MockBean
    private OAuth2AuthorizedClientManager authorizedClientManager;

    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @MockBean
    private OAuth2AuthorizedClientService authorizedClientService;

    @SpyBean
    private OAuth2ClientHttpRequestInterceptor oauth2ClientHttpRequestInterceptor;

    @TestConfiguration
    public static class MainControllerTestConfiguration {
        @Bean
        MockRestServiceServer microserviceTokenPassthroughRestTemplateServer(
                RestTemplate microserviceTokenPassthroughRestTemplate) {
            return MockRestServiceServer.createServer(microserviceTokenPassthroughRestTemplate);
        }

        @Bean
        MockServerRestClientCustomizer mockServerRestClientCustomizer() {
            return new MockServerRestClientCustomizer();
        }
    }

    @BeforeEach
    void init() {
        doReturn("myInternalToken").when(oauth2ClientHttpRequestInterceptor).getToken();
    }

    @AfterEach
    void cleanUpEach() {
        customizer.getServers().values().forEach(MockRestServiceServer::reset);
    }

    @Test
    @WithMockCustomUser
    void testRestClientTokenPassthroughWithSecurity() throws Exception {
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

    @Test
    @WithMockCustomUser
    void testRestTemplateTokenPassthroughWithSecurity() throws Exception {
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

    @Test
    void testRestClientTokenPassthroughNoAuthWithNoSecurity() throws Exception {
        customizer
                .getServer(microserviceTokenPassthroughRestClientBuilder)
                .expect(requestTo("http://localhost/"))
                .andExpect(headerDoesNotExist(HttpHeaders.AUTHORIZATION))
                .andRespond(withUnauthorizedRequest());

        mvc.perform(get("/rest-client-token-passthrough-no-auth")).andExpect(status().isUnauthorized());
    }

    @Test
    void testRestClientOAuth2ClientNoAuthWithNoSecurity() throws Exception {
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

    @Test
    @WithMockCustomUser(authorities = { "permission:read" })
    void testPreAuthReadWithSecurity() throws Exception {
        mvc
                .perform(get("/preauth-read"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("Hello world"));
    }

    @Test
    @WithMockCustomUser(authorities = { "permission:read" })
    void testSecurityConfigReadWithSecurity() throws Exception {
        mvc
                .perform(get("/security-config-role-read"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("Hello world"));
    }

    @Test
    void testFetchSemiSecureObjectNoSecurity() throws Exception {
        mvc.perform(get("/fetch-semi-secure-object")).andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockCustomUser
    void testFetchSemiSecureObjectWithSecurityButNotFull() throws Exception {
        mvc.perform(get("/fetch-semi-secure-object")).andExpect(status().is2xxSuccessful()).andExpect(content().json("""
                    {
                        "name": "Secret Person",
                        "ssn": "***",
                        "phoneNumber": "***-***-7890"
                    }
                """));
    }

    @Test
    @WithMockCustomUser(authorities = { "name:miller79" })
    void testFetchSemiSecureObjectWithSecurityFull() throws Exception {
        mvc.perform(get("/fetch-semi-secure-object")).andExpect(status().is2xxSuccessful()).andExpect(content().json("""
                    {
                        "name": "Secret Person",
                        "ssn": "123-45-6789",
                        "phoneNumber": "123-456-7890"
                    }
                """));
    }
}
