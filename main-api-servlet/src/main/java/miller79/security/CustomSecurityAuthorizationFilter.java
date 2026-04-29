package miller79.security;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A custom servlet filter that enriches the authenticated user's permissions by adding their
 * username as a granted authority.
 *
 * <p>After the standard JWT authentication runs, this filter checks if the current user is
 * authenticated with a JWT token. If so, it adds a new authority in the format
 * {@code name:<username>} (for example, {@code name:miller79}). This allows other parts of the
 * application to write security rules based on the specific user's identity — for example,
 * only showing sensitive data to a specific user.
 *
 * <p>This filter extends {@link org.springframework.web.filter.OncePerRequestFilter} to guarantee
 * it runs exactly once per HTTP request, even if the request is forwarded internally.
 *
 * @see <a href=
 *      "https://docs.spring.io/spring-security/reference/servlet/architecture.html#adding-custom-filter">Adding
 *      a Custom Filter to the Filter Chain</a>
 */
@Component
class CustomSecurityAuthorizationFilter extends OncePerRequestFilter {
    /**
     * Checks for a JWT-authenticated user and adds their username as a granted authority.
     *
     * @param request the incoming HTTP request
     * @param response the outgoing HTTP response
     * @param filterChain the remaining filters to execute after this one
     * @throws ServletException if a servlet error occurs during filtering
     * @throws IOException if an I/O error occurs during filtering
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Optional
                .ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(JwtAuthenticationToken.class::isInstance)
                .map(JwtAuthenticationToken.class::cast)
                .map(jwt -> new JwtAuthenticationToken(jwt.getToken(),
                        Stream
                                .concat(jwt.getAuthorities().stream(),
                                        Stream.of(new SimpleGrantedAuthority("name:" + jwt.getName())))
                                .toList()))
                .ifPresent(SecurityContextHolder.getContext()::setAuthentication);

        filterChain.doFilter(request, response);
    }
}
