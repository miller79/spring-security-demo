package miller79.main;

import org.springframework.boot.autoconfigure.web.client.RestClientBuilderConfigurer;
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

    @Bean
    RestClient.Builder microserviceTokenPassthroughRestClientBuilder(
            TokenPassthroughInterceptor tokenPassthroughInterceptor) {
        return RestClientUtil
                .createDefaultRestClientBuilder(restClientBuilderConfigurer)
                .baseUrl(miller79ClientServerConfigurationProperties.getMicroserviceTokenPassthroughBaseUrl())
                .requestInterceptor(tokenPassthroughInterceptor);
    }

    @Bean
    RestClient.Builder microserviceOAuth2ClientRestClientBuilder(
            OAuth2AuthorizedClientManager authorizedClientManager) {
        OAuth2ClientHttpRequestInterceptor oauth2ClientHttpRequestInterceptor = new OAuth2ClientHttpRequestInterceptor(
                authorizedClientManager);
        oauth2ClientHttpRequestInterceptor.setClientRegistrationIdResolver(request -> "keycloak");

        return RestClientUtil
                .createDefaultRestClientBuilder(restClientBuilderConfigurer)
                .baseUrl(miller79ClientServerConfigurationProperties.getMicroserviceOAuth2ClientBaseUrl())
                .requestInterceptor(oauth2ClientHttpRequestInterceptor);
    }
}
