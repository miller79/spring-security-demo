package miller79.security;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
 * @see <a href=
 *      "https://docs.spring.io/spring-security/reference/reactive/oauth2/resource-server/jwt.html">OAuth
 *      2.0 Resource Server JWT</a>
 */
@Component
@RequiredArgsConstructor
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {
    private static final String RESOURCE_ACCESS = "resource_access";
    private static final String ROLES = "roles";

    private final Miller79SecurityResourceServerConfigurationProperties miller79SecurityConfigurationProperties;

    @Override
    public Mono<AbstractAuthenticationToken> convert(final Jwt source) {
        Collection<GrantedAuthority> authorities = extractResourceRoles(source,
                miller79SecurityConfigurationProperties.getClientId());
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
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}