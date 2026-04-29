package miller79.main;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServerBearerExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

/**
 * Configures {@link org.springframework.web.reactive.function.client.WebClient WebClient} beans
 * for calling the downstream microservice with two different authentication strategies.
 *
 * <p>{@code WebClient} is Spring's non-blocking HTTP client for reactive applications (the
 * reactive counterpart to {@code RestClient}/{@code RestTemplate}). This configuration creates:
 * <ul>
 *   <li><b>Token passthrough WebClient</b> — uses a
 *       {@code ServerBearerExchangeFilterFunction}
 *       that automatically extracts the current user's JWT from the reactive security context
 *       and attaches it to outgoing requests</li>
 *   <li><b>OAuth2 client WebClient</b> — uses a
 *       {@code ServerOAuth2AuthorizedClientExchangeFilterFunction}
 *       that obtains a machine-to-machine token from Keycloak via client credentials</li>
 * </ul>
 *
 * @see <a href=
 *      "https://docs.spring.io/spring-framework/reference/core/beans/java/configuration-annotation.html">Using
 *      the @Configuration annotation</a>
 * @see <a href="https://projectlombok.org/features/constructor">Lombok
 *      Constructor</a>
 * @see <a href=
 *      "https://docs.spring.io/spring-framework/reference/web/webflux-webclient.html">WebClient</a>
 */
@RequiredArgsConstructor
@Configuration
class WebClientConfiguration {
    private final Miller79ClientServerConfigurationProperties miller79ClientServerConfigurationProperties;

    /**
     * Creates a WebClient configured for token passthrough calls to the downstream microservice.
     *
     * @param webClientBuilder the auto-configured WebClient builder provided by Spring Boot
     * @param serverBearerExchangeFilterFunction the filter that attaches the user's JWT to outgoing requests
     * @return a WebClient with the microservice's base URL and token passthrough configured
     */
    @Bean
    WebClient microserviceTokenPassthroughWebClient(
            WebClient.Builder webClientBuilder,
            ServerBearerExchangeFilterFunction serverBearerExchangeFilterFunction) {
        return webClientBuilder
                .baseUrl(miller79ClientServerConfigurationProperties.microserviceTokenPassthroughBaseUrl())
                .filter(serverBearerExchangeFilterFunction)
                .build();
    }

    /**
     * Creates a WebClient configured for OAuth2 client credentials calls to the downstream microservice.
     *
     * @param webClientBuilder the auto-configured WebClient builder provided by Spring Boot
     * @param serverOAuth2AuthorizedClientExchangeFilterFunction the filter that obtains and attaches machine-to-machine tokens
     * @return a WebClient with the microservice's base URL and OAuth2 client credentials configured
     */
    @Bean
    WebClient microserviceOAuth2ClientWebClient(
            WebClient.Builder webClientBuilder,
            ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunction) {
        return webClientBuilder
                .baseUrl(miller79ClientServerConfigurationProperties.microserviceOAuth2ClientBaseUrl())
                .filter(serverOAuth2AuthorizedClientExchangeFilterFunction)
                .build();
    }
}
