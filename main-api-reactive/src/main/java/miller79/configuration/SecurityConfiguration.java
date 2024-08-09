package miller79.configuration;

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
import miller79.converter.KeycloakJwtAuthenticationConverter;
import miller79.security.filter.CustomSecurityAuthorizationFilter;

/**
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
public class SecurityConfiguration {
    private final KeycloakJwtAuthenticationConverter keycloakJwtAuthenticationConverter;

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

    @Bean
    SecurityWebFilterChain filterChain(ServerHttpSecurity http) throws Exception {
        return http
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
