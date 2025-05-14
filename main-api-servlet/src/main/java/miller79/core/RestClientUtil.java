package miller79.core;

import org.springframework.boot.autoconfigure.web.client.RestClientBuilderConfigurer;
import org.springframework.web.client.RestClient;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RestClientUtil {
    /**
     * Copied from
     * {@link org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration
     * RestClientAutoConfiguration}
     * 
     * This code is intended to perform what the RestClientAutoConfiguration
     * currently does with RestClients and can be used as a single place to manage
     * if its logic does happen to change in the future.
     * 
     * @return
     */
    public RestClient.Builder createDefaultRestClientBuilder(RestClientBuilderConfigurer restClientBuilderConfigurer) {
        return restClientBuilderConfigurer.configure(RestClient.builder());
    }
}
