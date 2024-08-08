package miller79.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import lombok.RequiredArgsConstructor;

/**
 * @see <a href=
 *      "https://docs.spring.io/spring-framework/reference/core/beans/java/configuration-annotation.html">Using
 *      the @Configuration annotation</a>
 * @see <a href="https://projectlombok.org/features/constructor">Lombok
 *      Constructor</a>
 * @see <a href=
 *      "https://docs.spring.io/spring-security/reference/reactive/oauth2/resource-server/jwt.html#webflux-oauth2resourceserver-jwt-sansboot">Overriding
 *      or Replacing Boot Auto Configuration</a>
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {
    @Bean
    SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(
                        exchanges -> exchanges.pathMatchers("/actuator/**").permitAll().anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
