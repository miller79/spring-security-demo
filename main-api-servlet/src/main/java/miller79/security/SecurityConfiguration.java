package miller79.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
 * Central security configuration for the servlet-based main API.
 *
 * <p>This class defines HOW the application protects its endpoints. In Spring Security, a "filter
 * chain" is a series of checks that every incoming HTTP request passes through. This
 * configuration builds that chain with the following rules:
 * <ul>
 *   <li>CSRF protection is disabled (safe for stateless token-based APIs that don't use cookies)</li>
 *   <li>{@code /actuator/**} endpoints (health checks, metrics) are open to everyone</li>
 *   <li>{@code /rest-client-token-passthrough-no-auth} and {@code /rest-client-oauth2-client-no-auth}
 *       are public (they handle auth differently — by forwarding tokens or using client credentials)</li>
 *   <li>{@code /security-config-role-read} requires the {@code permission:read} authority</li>
 *   <li>All other endpoints require authentication (a valid JWT token)</li>
 * </ul>
 *
 * <p>It also:
 * <ul>
 *   <li>Registers a custom {@link KeycloakJwtAuthenticationConverter} to extract user
 *       permissions from Keycloak-formatted JWT tokens</li>
 *   <li>Adds a {@link CustomSecurityAuthorizationFilter} that enriches the user's authorities
 *       with their username</li>
 *   <li>Configures an {@link org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager}
 *       for machine-to-machine token acquisition (client credentials grant)</li>
 * </ul>
 *
 * <p>Method-level security is also enabled via {@code @EnableMethodSecurity}, which allows
 * individual controller methods to use {@code @PreAuthorize} annotations.
 *
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

    /**
     * Creates an OAuth2 client manager for obtaining machine-to-machine tokens.
     *
     * @param clientRegistrationRepository holds the OAuth2 client registrations (e.g., Keycloak client ID/secret)
     * @param oAuth2AuthorizedClientService stores previously obtained OAuth2 tokens for reuse
     * @return an OAuth2 client manager configured for the client credentials grant type
     */
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

    /**
     * Builds the HTTP security filter chain that protects all servlet API endpoints.
     *
     * @param http the security builder provided by Spring, used to define URL access rules, JWT validation, and custom filters
     * @return a fully configured security filter chain
     * @throws Exception if the security configuration cannot be built
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
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

    /**
     * Prevents the custom security filter from being registered as a generic servlet filter.
     *
     * <p>Spring Boot auto-detects any bean that extends {@code Filter} and registers it as a servlet
     * filter. Since our custom filter is already added to the Spring Security filter chain,
     * this registration bean disables the duplicate auto-registration.
     *
     * @param filter the custom filter to exclude from auto-registration
     * @return a filter registration bean with {@code enabled} set to false
     */
    @Bean
    FilterRegistrationBean<CustomSecurityAuthorizationFilter> tenantFilterRegistration(
            CustomSecurityAuthorizationFilter filter) {
        FilterRegistrationBean<CustomSecurityAuthorizationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
