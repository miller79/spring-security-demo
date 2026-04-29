package miller79.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;

import lombok.RequiredArgsConstructor;

/**
 * Central security configuration for the reactive main API.
 *
 * <p>This is the reactive counterpart to the servlet module's {@code SecurityConfiguration}.
 * It uses WebFlux's {@link org.springframework.security.web.server.SecurityWebFilterChain}
 * instead of the servlet-based {@code SecurityFilterChain}. The security rules are equivalent:
 * <ul>
 *   <li>CSRF disabled (stateless token-based API)</li>
 *   <li>{@code /actuator/**} open to all</li>
 *   <li>{@code /web-client-token-passthrough-no-auth} and {@code /web-client-oauth2-client-no-auth}
 *       are public</li>
 *   <li>{@code /security-config-role-read} requires {@code permission:read}</li>
 *   <li>All other endpoints require authentication</li>
 * </ul>
 *
 * <p>The reactive OAuth2 client manager uses
 * {@code ReactiveOAuth2AuthorizedClientProviderBuilder} for machine-to-machine token
 * acquisition. Method-level security is enabled via {@code @EnableReactiveMethodSecurity}.
 *
 * @see <a href=
 *      "https://docs.spring.io/spring-framework/reference/core/beans/java/configuration-annotation.html">Using
 *      the @Configuration annotation</a>
 * @see <a href="https://projectlombok.org/features/constructor">Lombok
 *      Constructor</a>
 * @see <a href=
 *      "https://docs.spring.io/spring-security/reference/reactive/authorization/method.html#jc-enable-reactive-method-security-authorization-manager">EnableReactiveMethodSecurity
 *      with Authorization Manager</a>
 */
@Configuration
@RequiredArgsConstructor
@EnableReactiveMethodSecurity
class SecurityConfiguration {
    private final KeycloakJwtAuthenticationConverter keycloakJwtAuthenticationConverter;

    /**
     * Creates a reactive OAuth2 client manager for obtaining machine-to-machine tokens.
     *
     * @param clientRegistrationRepository holds the OAuth2 client registrations (e.g., Keycloak client ID/secret)
     * @param authorizedClientRepository stores previously obtained OAuth2 tokens for reuse
     * @return a reactive OAuth2 client manager configured for the client credentials grant type
     */
    @Bean
    ReactiveOAuth2AuthorizedClientManager authorizedClientManager(
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {
        ReactiveOAuth2AuthorizedClientProvider authorizedClientProvider = ReactiveOAuth2AuthorizedClientProviderBuilder
                .builder()
                .clientCredentials()
                .build();

        DefaultReactiveOAuth2AuthorizedClientManager authorizedClientManager = new DefaultReactiveOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientRepository);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }

    /**
     * Builds the reactive security filter chain that protects all API endpoints.
     *
     * @param http the reactive security builder provided by Spring, used to define URL access rules and JWT validation
     * @return a fully configured security filter chain for the reactive stack
     * @throws Exception if the security configuration cannot be built
     */
    @Bean
    SecurityWebFilterChain filterChain(ServerHttpSecurity http) throws Exception {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/actuator/**")
                        .permitAll()
                        .pathMatchers("/web-client-token-passthrough-no-auth")
                        .permitAll()
                        .pathMatchers("/web-client-oauth2-client-no-auth")
                        .permitAll()
                        .pathMatchers("/security-config-role-read")
                        .hasAuthority("permission:read")
                        .anyExchange()
                        .authenticated())
                .oauth2ResourceServer(
                        oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtAuthenticationConverter)))
                .addFilterBefore(new CustomSecurityAuthorizationFilter(), SecurityWebFiltersOrder.AUTHORIZATION)
                .build();
    }
}
