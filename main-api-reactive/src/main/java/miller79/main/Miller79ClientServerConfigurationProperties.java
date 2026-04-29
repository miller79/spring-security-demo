package miller79.main;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties that hold the base URLs for downstream microservice endpoints,
 * loaded from {@code miller79.client} in the application's YAML/properties file.
 *
 * <p>Instead of hardcoding URLs in Java code, Spring Boot's {@code @ConfigurationProperties}
 * pattern reads values from configuration files and makes them available as type-safe Java
 * objects.
 *
 * @param microserviceTokenPassthroughBaseUrl base URL for token-passthrough calls
 *        (e.g., {@code http://localhost:8081})
 * @param microserviceOAuth2ClientBaseUrl base URL for OAuth2 client credentials calls
 */
@ConfigurationProperties("miller79.client")
record Miller79ClientServerConfigurationProperties(
        String microserviceTokenPassthroughBaseUrl,
        String microserviceOAuth2ClientBaseUrl) {
}
