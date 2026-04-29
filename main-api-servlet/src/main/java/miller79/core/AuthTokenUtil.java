package miller79.core;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Utility class for extracting the current user's raw JWT (JSON Web Token) string from
 * Spring Security's {@code SecurityContext}.
 *
 * <p>Spring Security stores the authenticated user's information in a thread-local
 * "security context." When the user authenticated with a JWT, the token object is stored
 * as the "principal" in that context. This utility reaches into the context, pulls out
 * the JWT, and returns its raw string value (the encoded token that can be sent in HTTP
 * headers).
 *
 * <p>This is a final utility class with a private constructor — it cannot be instantiated
 * and only provides static methods.
 */
public final class AuthTokenUtil {
    private AuthTokenUtil() {}

    /**
     * Retrieves the raw JWT token value from the current security context.
     *
     * @return the raw JWT token value as a string, or {@code null} if no JWT-authenticated user
     *         is in the current security context
     */
    public static String getAuthTokenFromSecurityContext() {
        return Optional
                .ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(Jwt.class::isInstance)
                .map(Jwt.class::cast)
                .map(Jwt::getTokenValue)
                .orElse(null);
    }
}
