package miller79.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

/**
 * Factory that creates a simulated Spring Security context with a JWT token for test methods
 * annotated with {@link WithMockCustomUser}.
 *
 * <p>When Spring Security's test infrastructure sees {@code @WithMockCustomUser} on a test method,
 * it calls this factory to build a {@code SecurityContext} containing a fake
 * {@code JwtAuthenticationToken}. The fake JWT includes realistic claims (name, email,
 * username) but is never validated against an actual identity provider — it's purely for testing.
 *
 * <p>The static {@link #getJwt()} method can also be called directly in test assertions when you
 * need to verify that the correct token value was forwarded to a downstream service.
 */
public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
    /**
     * Creates a security context with a fake JWT-authenticated user.
     *
     * @param withMockCustomUser the annotation instance containing the configured authorities
     * @return a SecurityContext with a JwtAuthenticationToken populated with the specified authorities
     */
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser withMockCustomUser) {
        List<GrantedAuthority> grantedAuthorities = Arrays.stream(withMockCustomUser.authorities())
                .map(authority -> (GrantedAuthority) new SimpleGrantedAuthority(authority))
                .toList();
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(createJwtAuthenticationToken(grantedAuthorities));
        return context;
    }

    private JwtAuthenticationToken createJwtAuthenticationToken(List<GrantedAuthority> grantedAuthorities) {
        return new JwtAuthenticationToken(getJwt(), grantedAuthorities);
    }

    /**
     * Returns a fake JWT token with predefined claims for use in test assertions.
     *
     * @return a JWT with predefined subject, name, email, and username claims
     */
    public static Jwt getJwt() {
        return Jwt
                .withTokenValue("token")
                .header("alg", "none")
                .subject("subject")
                .claim("given_name", "first")
                .claim("family_name", "last")
                .claim("phone_number", "1234567890")
                .claim("email", "test@test.com")
                .claim("preferred_username", "tst_user")
                .build();
    }
}
