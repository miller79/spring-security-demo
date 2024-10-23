package miller79.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

import lombok.RequiredArgsConstructor;

/**
 * @see <a href=
 *      "https://docs.spring.io/spring-security/reference/servlet/oauth2/client/index.html">OAuth
 *      2.0 Client</a>
 * @see <a href=
 *      "https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html">OAuth
 *      2.0 Resource Server JWT</a>
 * @see <a href=
 *      "https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html#meta-annotations">Method
 *      Security </a>
 * @see <a href=
 *      "https://docs.spring.io/spring-framework/reference/core/beans/java/configuration-annotation.html">Using
 *      the @Configuration annotation</a>
 * @see <a href="https://projectlombok.org/features/constructor">Lombok
 *      Constructor</a>
 * @see <a href=
 *      "https://docs.spring.io/spring-security/reference/servlet/architecture.html#adding-custom-filter">Adding
 *      a Custom Filter to the Filter Chain</a>
 */
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
class SecurityConfiguration {
    private final KeycloakJwtAuthenticationConverter keycloakJwtAuthenticationConverter;
    private final CustomSecurityAuthorizationFilter customSecurityAuthorizationFilter;

    @Bean
    OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService) {

        OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder
                .builder()
                .clientCredentials()
                .build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientService);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/**")
                        .permitAll()
                        .requestMatchers("/rest-client-token-passthrough-no-auth")
                        .permitAll()
                        .requestMatchers("/rest-client-oauth2-client-no-auth")
                        .permitAll()
                        .requestMatchers("/security-config-role-read")
                        .hasAuthority("permission:read")
                        .anyRequest()
                        .authenticated())
                .oauth2ResourceServer(
                        oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtAuthenticationConverter)))
                .addFilterBefore(customSecurityAuthorizationFilter, AuthorizationFilter.class)
                .build();
    }

    @Bean
    FilterRegistrationBean<CustomSecurityAuthorizationFilter> tenantFilterRegistration(
            CustomSecurityAuthorizationFilter filter) {
        FilterRegistrationBean<CustomSecurityAuthorizationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
