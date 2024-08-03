package miller79.security.configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import miller79.security.filter.CustomSecurityAuthorizationFilter;

@Configuration
public class CustomSecurityAuthorizationConfiguration {
    @Bean
    FilterRegistrationBean<CustomSecurityAuthorizationFilter> tenantFilterRegistration(CustomSecurityAuthorizationFilter filter) {
        FilterRegistrationBean<CustomSecurityAuthorizationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
