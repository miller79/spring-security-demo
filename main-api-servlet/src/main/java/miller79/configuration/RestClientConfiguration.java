package miller79.configuration;

import org.springframework.boot.autoconfigure.web.client.RestClientBuilderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;
import miller79.interceptor.OAuth2ClientHttpRequestInterceptor;
import miller79.interceptor.TokenPassthroughInterceptor;
import miller79.properties.Miller79ClientServerConfigurationProperties;
import miller79.util.RestClientUtil;

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
public class RestClientConfiguration {
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
            OAuth2ClientHttpRequestInterceptor oauth2ClientHttpRequestInterceptor) {
        return RestClientUtil
                .createDefaultRestClientBuilder(restClientBuilderConfigurer)
                .baseUrl(miller79ClientServerConfigurationProperties.getMicroserviceOAuth2ClientBaseUrl())
                .requestInterceptor(oauth2ClientHttpRequestInterceptor);
    }
}
