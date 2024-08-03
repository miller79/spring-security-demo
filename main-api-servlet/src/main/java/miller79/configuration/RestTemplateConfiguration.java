package miller79.configuration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import miller79.interceptor.TokenPassthroughInterceptor;
import miller79.properties.Miller79ClientServerConfigurationProperties;

@RequiredArgsConstructor
@Configuration
public class RestTemplateConfiguration {
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
