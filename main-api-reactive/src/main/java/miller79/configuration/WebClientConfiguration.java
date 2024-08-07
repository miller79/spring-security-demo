package miller79.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServerBearerExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import miller79.properties.Miller79ClientServerConfigurationProperties;

@RequiredArgsConstructor
@Configuration
public class WebClientConfiguration {
    private final Miller79ClientServerConfigurationProperties miller79ClientServerConfigurationProperties;

    @Bean
    WebClient microserviceTokenPassthroughWebClient(
            WebClient.Builder webClientBuilder,
            ServerBearerExchangeFilterFunction serverBearerExchangeFilterFunction) {
        return webClientBuilder
                .baseUrl(miller79ClientServerConfigurationProperties.getMicroserviceTokenPassthroughBaseUrl())
                .filter(serverBearerExchangeFilterFunction)
                .build();
    }

    @Bean
    WebClient microserviceOAuth2ClientWebClient(
            WebClient.Builder webClientBuilder,
            ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunction) {
        return webClientBuilder
                .baseUrl(miller79ClientServerConfigurationProperties.getMicroserviceOAuth2ClientBaseUrl())
                .filter(serverOAuth2AuthorizedClientExchangeFilterFunction)
                .build();
    }
}
