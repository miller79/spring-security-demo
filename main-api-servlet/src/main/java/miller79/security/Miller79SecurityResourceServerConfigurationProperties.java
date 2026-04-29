package miller79.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the OAuth2 resource server, loaded from
 * {@code miller79.security.resource-server} in the application's YAML/properties file.
 *
 * <p>Keycloak (the identity provider / login server) stores user roles inside the JWT token
 * under a structure keyed by "client ID." This record holds that client ID so the application
 * knows where to find the user's roles within the token.
 *
 * <p>For example, if the client ID is "my-app", the JWT token might contain:
 * {@code resource_access.my-app.roles = ["read", "write"]}.
 *
 * @param clientId the Keycloak client identifier used to look up roles in JWT tokens
 */
@ConfigurationProperties("miller79.security.resource-server")
record Miller79SecurityResourceServerConfigurationProperties(String clientId) {
}
