package miller79.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configures the security rules for this microservice — which requests require authentication
 * and which don't.
 *
 * <p>By default, Spring Security would block ALL incoming requests. This class customizes that
 * behavior so that:
 * <ul>
 *   <li>Health/monitoring endpoints ({@code /actuator/**}) are open to everyone (no login needed)</li>
 *   <li>All other endpoints require the caller to present a valid JWT
 *       (JSON Web Token — a compact, digitally-signed token that proves who the caller is)</li>
 * </ul>
 *
 * <p>This microservice uses the "reactive" web stack (WebFlux), so it configures a
 * {@link org.springframework.security.web.server.SecurityWebFilterChain SecurityWebFilterChain}
 * rather than the servlet-based {@code SecurityFilterChain}.
 *
 * <p>CSRF (Cross-Site Request Forgery) protection is disabled because this microservice is a
 * stateless API that uses tokens, not browser cookies, for authentication.
 *
 * @see <a href=
 *      "https://docs.spring.io/spring-framework/reference/core/beans/java/configuration-annotation.html">Using
 *      the @Configuration annotation</a>
 * @see <a href=
 *      "https://docs.spring.io/spring-security/reference/reactive/oauth2/resource-server/jwt.html#webflux-oauth2resourceserver-jwt-sansboot">Overriding
 *      or Replacing Boot Auto Configuration</a>
 */
@Configuration
class SecurityConfiguration {
    /**
     * Builds the security filter chain that defines how every incoming request is authenticated.
     *
     * @param http the security builder provided by Spring, used to define security rules
     * @return a fully configured security filter chain that Spring will apply to every incoming request
     */
    @Bean
    SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(
                        exchanges -> exchanges.pathMatchers("/actuator/**").permitAll().anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
