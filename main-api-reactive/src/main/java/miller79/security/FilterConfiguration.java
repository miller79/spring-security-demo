package miller79.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServerBearerExchangeFilterFunction;

/**
 * Configures reactive exchange filter functions used by {@code WebClient} beans to handle
 * authentication on outgoing HTTP requests.
 *
 * <p>In the reactive world, "exchange filter functions" are the equivalent of servlet interceptors.
 * This configuration creates two:
 * <ul>
 *   <li>{@code ServerOAuth2AuthorizedClientExchangeFilterFunction} — automatically attaches
 *       a machine-to-machine OAuth2 token (obtained via client credentials from Keycloak)
 *       to outgoing requests. Configured with the default client registration ID "keycloak."</li>
 *   <li>{@code ServerBearerExchangeFilterFunction} — automatically extracts the current user's
 *       JWT token from the reactive security context and attaches it as a Bearer header to
 *       outgoing requests.</li>
 * </ul>
 *
 * @see <a href=
 *      "https://docs.spring.io/spring-framework/reference/core/beans/java/configuration-annotation.html">Using
 *      the @Configuration annotation</a>
 */
@Configuration
class FilterConfiguration {
    /**
     * Creates a filter function that attaches machine-to-machine OAuth2 tokens to outgoing requests.
     *
     * @param authorizedClientManager manages the OAuth2 client credentials token lifecycle
     * @return a configured exchange filter function for OAuth2 client credentials
     */
    @Bean
    ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunction(
            ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunction = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
                authorizedClientManager);
        serverOAuth2AuthorizedClientExchangeFilterFunction.setDefaultClientRegistrationId("keycloak");
        return serverOAuth2AuthorizedClientExchangeFilterFunction;
    }

    /**
     * Creates a filter function that forwards the current user's JWT token to outgoing requests.
     *
     * @return a configured exchange filter function for bearer token passthrough
     */
    @Bean
    ServerBearerExchangeFilterFunction serverBearerExchangeFilterFunction() {
        return new ServerBearerExchangeFilterFunction();
    }
}
