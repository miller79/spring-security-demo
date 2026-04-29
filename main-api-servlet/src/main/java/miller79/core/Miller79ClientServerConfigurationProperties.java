package miller79.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties that hold the base URLs for downstream microservice endpoints,
 * loaded from {@code miller79.client} in the application's YAML/properties file.
 *
 * <p>Instead of hardcoding URLs in Java code, Spring Boot's {@code @ConfigurationProperties}
 * pattern reads values from configuration files and makes them available as type-safe Java
 * objects. This record stores two URLs:
 * <ul>
 *   <li>The token-passthrough URL — used when forwarding the user's own JWT token</li>
 *   <li>The OAuth2 client URL — used when the API obtains its own machine-to-machine token</li>
 * </ul>
 *
 * @param microserviceTokenPassthroughBaseUrl base URL of the microservice endpoint used for
 *        token passthrough calls (e.g., {@code http://localhost:8081})
 * @param microserviceOAuth2ClientBaseUrl base URL of the microservice endpoint used for
 *        OAuth2 client credentials calls
 */
@ConfigurationProperties("miller79.client")
public record Miller79ClientServerConfigurationProperties(
        String microserviceTokenPassthroughBaseUrl,
        String microserviceOAuth2ClientBaseUrl) {
}
