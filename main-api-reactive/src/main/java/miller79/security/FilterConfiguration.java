package miller79.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServerBearerExchangeFilterFunction;

/**
 * @see <a href=
 *      "https://docs.spring.io/spring-framework/reference/core/beans/java/configuration-annotation.html">Using
 *      the @Configuration annotation</a>
 */
@Configuration
public class FilterConfiguration {
    @Bean
    ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunction(
            ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunction = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
                authorizedClientManager);
        serverOAuth2AuthorizedClientExchangeFilterFunction.setDefaultClientRegistrationId("keycloak");
        return serverOAuth2AuthorizedClientExchangeFilterFunction;
    }

    @Bean
    ServerBearerExchangeFilterFunction serverBearerExchangeFilterFunction() {
        return new ServerBearerExchangeFilterFunction();
    }
}
