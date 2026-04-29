package miller79.security;

import java.util.stream.Stream;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

/**
 * A reactive web filter that enriches the authenticated user's permissions by adding their
 * username as a granted authority.
 *
 * <p>This is the reactive counterpart to the servlet module's {@code CustomSecurityAuthorizationFilter}.
 * It performs the same function — adding a {@code name:<username>} authority — but uses the
 * reactive security context ({@code ReactiveSecurityContextHolder}) and reactive operators
 * instead of the thread-local {@code SecurityContextHolder}.
 *
 * <p>In the reactive stack, the security context is propagated through the
 * {@link reactor.util.context.Context Reactor Context} (a key-value store attached to the
 * reactive stream) rather than through thread-local variables.
 */
class CustomSecurityAuthorizationFilter implements WebFilter {
    /**
     * Checks for a JWT-authenticated user and adds their username as a granted authority.
     *
     * @param exchange the current server exchange (contains request and response)
     * @param chain the filter chain to delegate to
     * @return a Mono that completes when the filter chain finishes
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication)
                .filter(JwtAuthenticationToken.class::isInstance)
                .cast(JwtAuthenticationToken.class)
                .map(jwt -> new JwtAuthenticationToken(jwt.getToken(),
                        Stream
                                .concat(jwt.getAuthorities().stream(),
                                        Stream.of(new SimpleGrantedAuthority("name:" + jwt.getName())))
                                .toList()))
                .switchIfEmpty(chain.filter(exchange).then(Mono.empty()))
                .flatMap(jwt -> chain
                        .filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(jwt)));
    }
}
