package miller79.core;

import org.springframework.boot.restclient.autoconfigure.RestClientBuilderConfigurer;
import org.springframework.web.client.RestClient;

/**
 * Utility class for creating {@link org.springframework.web.client.RestClient RestClient}
 * builder instances that match Spring Boot's default auto-configuration.
 *
 * <p>When Spring Boot auto-configures a {@code RestClient}, it applies default settings like
 * timeouts and error handlers via a {@code RestClientBuilderConfigurer}. If you create
 * additional {@code RestClient} beans manually (as this project does for multiple downstream
 * services), those defaults wouldn't apply automatically. This utility applies the same
 * configurer to ensure all RestClient instances behave consistently.
 *
 * <p>This is a final utility class with a private constructor — it cannot be instantiated
 * and only provides static methods.
 */
public final class RestClientUtil {
    private RestClientUtil() {}

    /**
     * Creates a new {@code RestClient.Builder} pre-configured with Spring Boot's default settings.
     *
     * @param restClientBuilderConfigurer the configurer from Spring Boot's auto-configuration that applies default settings
     * @return a RestClient.Builder pre-configured with Spring Boot's default settings
     */
    public static RestClient.Builder createDefaultRestClientBuilder(RestClientBuilderConfigurer restClientBuilderConfigurer) {
        return restClientBuilderConfigurer.configure(RestClient.builder());
    }
}
