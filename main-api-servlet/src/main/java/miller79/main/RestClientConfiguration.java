package miller79.main;

import org.springframework.boot.restclient.autoconfigure.RestClientBuilderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;
import miller79.core.Miller79ClientServerConfigurationProperties;
import miller79.core.RestClientUtil;
import miller79.core.TokenPassthroughInterceptor;

/**
 * Configures {@link org.springframework.web.client.RestClient RestClient} builder beans for
 * calling the downstream microservice with two different authentication strategies.
 *
 * <p>{@code RestClient} is Spring's modern replacement for {@code RestTemplate} — it provides
 * a fluent, functional API for making HTTP calls. This configuration creates two builders:
 * <ul>
 *   <li><b>Token passthrough builder</b> — attaches the current user's JWT token to every
 *       outgoing request using a {@link miller79.core.TokenPassthroughInterceptor}. Use this
 *       when the downstream service needs to know WHO the original user is.</li>
 *   <li><b>OAuth2 client credentials builder</b> — obtains a separate machine-to-machine token
 *       from Keycloak (an identity provider) using the "client credentials" flow. Use this for
 *       server-to-server calls where no user is involved.</li>
 * </ul>
 *
 * @see <a href=
 *      "https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-restclient">Rest
 *      Client</a>
 * @see <a href=
 *      "https://docs.spring.io/spring-boot/reference/io/rest-client.html#io.rest-client.restclient.customization">RestClient
 *      Customization</a>
 * @see <a href=
 *      "https://docs.spring.io/spring-framework/reference/core/beans/java/configuration-annotation.html">Using
 *      the @Configuration annotation</a>
 * @see <a href="https://projectlombok.org/features/constructor">Lombok
 *      Constructor</a>
 */
@RequiredArgsConstructor
@Configuration
class RestClientConfiguration {
    private final RestClientBuilderConfigurer restClientBuilderConfigurer;
    private final Miller79ClientServerConfigurationProperties miller79ClientServerConfigurationProperties;

    /**
     * Creates a RestClient builder configured for token passthrough to the downstream microservice.
     *
     * @param tokenPassthroughInterceptor the interceptor that forwards the user's JWT token
     * @return a RestClient.Builder with the microservice's base URL and token passthrough configured
     */
    @Bean
    RestClient.Builder microserviceTokenPassthroughRestClientBuilder(
            TokenPassthroughInterceptor tokenPassthroughInterceptor) {
        return RestClientUtil
                .createDefaultRestClientBuilder(restClientBuilderConfigurer)
                .baseUrl(miller79ClientServerConfigurationProperties.microserviceTokenPassthroughBaseUrl())
                .requestInterceptor(tokenPassthroughInterceptor);
    }

    /**
     * Creates a RestClient builder configured for OAuth2 client credentials calls to the downstream microservice.
     *
     * @param authorizedClientManager manages the OAuth2 client credentials token lifecycle (obtaining, refreshing)
     * @return a RestClient.Builder with the microservice's base URL and OAuth2 client credentials configured
     */
    @Bean
    RestClient.Builder microserviceOAuth2ClientRestClientBuilder(
            OAuth2AuthorizedClientManager authorizedClientManager) {
        OAuth2ClientHttpRequestInterceptor oauth2ClientHttpRequestInterceptor = new OAuth2ClientHttpRequestInterceptor(
                authorizedClientManager);
        oauth2ClientHttpRequestInterceptor.setClientRegistrationIdResolver(request -> "keycloak");

        return RestClientUtil
                .createDefaultRestClientBuilder(restClientBuilderConfigurer)
                .baseUrl(miller79ClientServerConfigurationProperties.microserviceOAuth2ClientBaseUrl())
                .requestInterceptor(oauth2ClientHttpRequestInterceptor);
    }
}
