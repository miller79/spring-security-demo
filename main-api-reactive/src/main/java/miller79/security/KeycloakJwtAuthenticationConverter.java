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
import reactor.core.publisher.Mono;

/**
 * Reactive version of the Keycloak JWT authentication converter.
 *
 * <p>Performs the same role-extraction logic as the servlet module's converter, but returns
 * a {@link reactor.core.publisher.Mono Mono} wrapping the authentication token, as required
 * by Spring Security's reactive stack.
 *
 * <p>See the servlet module's {@code KeycloakJwtAuthenticationConverter} for a detailed
 * explanation of how Keycloak roles are extracted from JWT tokens.
 *
 * @see <a href=
 *      "https://docs.spring.io/spring-security/reference/reactive/oauth2/resource-server/jwt.html">OAuth
 *      2.0 Resource Server JWT</a>
 */
@Component
@RequiredArgsConstructor
class KeycloakJwtAuthenticationConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {
    private static final String RESOURCE_ACCESS = "resource_access";
    private static final String ROLES = "roles";

    private final Miller79SecurityResourceServerConfigurationProperties miller79SecurityConfigurationProperties;

    /**
     * Converts a decoded JWT token into a reactive Spring Security authentication token with Keycloak-extracted permissions.
     *
     * @param source the decoded JWT token received from the caller
     * @return a Mono emitting a Spring Security authentication token with extracted Keycloak permissions
     */
    @Override
    public Mono<AbstractAuthenticationToken> convert(final Jwt source) {
        Collection<GrantedAuthority> authorities = extractResourceRoles(source,
                miller79SecurityConfigurationProperties.clientId());
        return Mono.just(new JwtAuthenticationToken(source, authorities));
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
                .map(role -> "permission:" + role)
                .<GrantedAuthority>map(SimpleGrantedAuthority::new)
                .toList();
    }
}