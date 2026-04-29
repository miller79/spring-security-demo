package miller79.security;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * Converts a Keycloak-issued JWT (JSON Web Token) into a Spring Security authentication object
 * with the correct user permissions.
 *
 * <p>By default, Spring Security reads roles from a standard JWT claim called "scope." But
 * Keycloak (an open-source identity and access management server) stores roles differently —
 * inside a nested structure called {@code resource_access.<client-id>.roles}.
 *
 * <p>This converter bridges that gap:
 * <ol>
 *   <li>Reads the {@code resource_access} claim from the JWT</li>
 *   <li>Finds the roles array for this application's client ID</li>
 *   <li>Converts each role into a Spring Security {@code GrantedAuthority} with a
 *       "permission:" prefix (e.g., the Keycloak role "read" becomes the authority
 *       "permission:read")</li>
 *   <li>Wraps everything into a {@code JwtAuthenticationToken} that Spring Security understands</li>
 * </ol>
 *
 * @see <a href=
 *      "https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html">OAuth
 *      2.0 Resource Server JWT</a>
 */
@Component
@RequiredArgsConstructor
class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private static final String RESOURCE_ACCESS = "resource_access";
    private static final String ROLES = "roles";

    private final Miller79SecurityResourceServerConfigurationProperties miller79SecurityConfigurationProperties;

    /**
     * Converts a decoded JWT token into a Spring Security authentication token with Keycloak-extracted permissions.
     *
     * @param source the decoded JWT token received from the caller
     * @return a Spring Security authentication token containing the user's identity and extracted permissions
     */
    @Override
    public AbstractAuthenticationToken convert(final Jwt source) {
        Collection<GrantedAuthority> authorities = extractResourceRoles(source,
                miller79SecurityConfigurationProperties.clientId());
        return new JwtAuthenticationToken(source, authorities);
    }

    @SuppressWarnings("unchecked")
    private static Collection<GrantedAuthority> extractResourceRoles(final Jwt jwt, final String resourceId) {
        return Optional
                .ofNullable(jwt.getClaimAsMap(RESOURCE_ACCESS))
                .map(claimsMap -> claimsMap.get(resourceId))
                .map(resourceMap -> (Map<String, List<String>>) resourceMap)
                .map(c -> c.get(ROLES))
                .stream()
                .flatMap(List::stream)
                .map(permission -> "permission:" + permission)
                .<GrantedAuthority>map(SimpleGrantedAuthority::new)
                .toList();
    }
}