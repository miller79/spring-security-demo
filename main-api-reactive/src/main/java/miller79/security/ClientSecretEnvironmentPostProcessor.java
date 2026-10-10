package miller79.security;

import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Fails application startup, before any bean is created, when the OAuth2 client secret is missing.
 *
 * <p>Spring Boot's property binder leaves an unresolved {@code ${OAUTH2_CLIENT_SECRET}} placeholder
 * as literal text, so without this check the literal text would be sent as the client secret.
 */
public class ClientSecretEnvironmentPostProcessor implements EnvironmentPostProcessor {
    static final String PROPERTY = "spring.security.oauth2.client.registration.keycloak.client-secret";
    static final String MESSAGE = "OAuth2 client secret is not configured: set the OAUTH2_CLIENT_SECRET environment variable";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String secret = Binder.get(environment).bind(PROPERTY, String.class).orElse(null);
        if (secret == null || secret.isBlank() || secret.contains("${")) {
            throw new IllegalStateException(MESSAGE);
        }
    }
}
