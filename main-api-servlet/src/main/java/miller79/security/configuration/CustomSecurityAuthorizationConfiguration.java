package miller79.security.configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import miller79.security.filter.CustomSecurityAuthorizationFilter;

/**
 * @see <a href=
 *      "https://docs.spring.io/spring-security/reference/servlet/architecture.html#adding-custom-filter">Adding
 *      a Custom Filter to the Filter Chain</a>
 */
@Configuration
public class CustomSecurityAuthorizationConfiguration {
    @Bean
    FilterRegistrationBean<CustomSecurityAuthorizationFilter> tenantFilterRegistration(
            CustomSecurityAuthorizationFilter filter) {
        FilterRegistrationBean<CustomSecurityAuthorizationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
