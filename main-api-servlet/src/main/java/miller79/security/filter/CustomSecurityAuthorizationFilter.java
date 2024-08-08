package miller79.security.filter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * @see <a href=
 *      "https://docs.spring.io/spring-security/reference/servlet/architecture.html#adding-custom-filter">Adding
 *      a Custom Filter to the Filter Chain</a>
 */
@Component
@RequiredArgsConstructor
public class CustomSecurityAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            JwtAuthenticationToken newAuthentication = Optional
                    .ofNullable(authentication)
                    .filter(JwtAuthenticationToken.class::isInstance)
                    .map(JwtAuthenticationToken.class::cast)
                    .map(jwt -> new JwtAuthenticationToken(jwt.getToken(), Stream
                            .concat(jwt.getAuthorities().stream(),
                                    List.of(new SimpleGrantedAuthority("name:" + authentication.getName())).stream())
                            .toList()))
                    .orElse(null);

            if (authentication != null && newAuthentication != null) {
                SecurityContextHolder.getContext().setAuthentication(newAuthentication);
            }

            filterChain.doFilter(request, response);
        } catch (RuntimeException e) {
            throw new AccessDeniedException(e.getMessage(), e);
        }
    }
}
