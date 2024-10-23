package miller79.main;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import miller79.core.Miller79ClientServerConfigurationProperties;
import miller79.core.TokenPassthroughInterceptor;

/**
 * @see <a href=
 *      "https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-resttemplate">RestTemplate</a>
 * @see <a href=
 *      "https://docs.spring.io/spring-boot/reference/io/rest-client.html#io.rest-client.resttemplate.customization">RestTemplate
 *      Customization</a>
 * @see <a href=
 *      "https://docs.spring.io/spring-framework/reference/core/beans/java/configuration-annotation.html">Using
 *      the @Configuration annotation</a>
 * @see <a href="https://projectlombok.org/features/constructor">Lombok
 *      Constructor</a>
 */
@RequiredArgsConstructor
@Configuration
class RestTemplateConfiguration {
    private final RestTemplateBuilder restTemplateBuilder;
    private final Miller79ClientServerConfigurationProperties miller79ClientServerConfigurationProperties;

    @Bean
    RestTemplate microserviceTokenPassthroughRestTemplate(TokenPassthroughInterceptor tokenPassthroughInterceptor) {
        return restTemplateBuilder
                .rootUri(miller79ClientServerConfigurationProperties.getMicroserviceTokenPassthroughBaseUrl())
                .additionalInterceptors(tokenPassthroughInterceptor)
                .build();
    }
}
