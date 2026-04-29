package miller79.main;

import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import miller79.core.Miller79ClientServerConfigurationProperties;
import miller79.core.TokenPassthroughInterceptor;

/**
 * Configures a {@link org.springframework.web.client.RestTemplate RestTemplate} bean for
 * calling the downstream microservice with the current user's security token attached.
 *
 * <p>{@code RestTemplate} is Spring's original HTTP client for making REST API calls from
 * server-side code. Although Spring now recommends the newer {@code RestClient}, many
 * projects still use {@code RestTemplate}.
 *
 * <p>This configuration:
 * <ul>
 *   <li>Sets the base URL for the downstream microservice (read from application properties)</li>
 *   <li>Adds a {@link miller79.core.TokenPassthroughInterceptor} that grabs the current user's
 *       JWT token from the security context and attaches it to every outgoing request as
 *       a "Bearer" authorization header</li>
 * </ul>
 *
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

    /**
     * Creates a RestTemplate pre-configured for token passthrough calls to the downstream microservice.
     *
     * @param tokenPassthroughInterceptor the interceptor that attaches the user's token to outgoing requests
     * @return a RestTemplate with the microservice's base URL and token passthrough configured
     */
    @Bean
    RestTemplate microserviceTokenPassthroughRestTemplate(TokenPassthroughInterceptor tokenPassthroughInterceptor) {
        return restTemplateBuilder
                .rootUri(miller79ClientServerConfigurationProperties.microserviceTokenPassthroughBaseUrl())
                .additionalInterceptors(tokenPassthroughInterceptor)
                .build();
    }
}
