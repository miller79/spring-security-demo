package miller79.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser withMockCustomUser) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (String authority : withMockCustomUser.authorities()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority));
        }
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(createJwtAuthenticationToken(grantedAuthorities));
        return context;
    }

    private JwtAuthenticationToken createJwtAuthenticationToken(List<GrantedAuthority> grantedAuthorities) {
        return new JwtAuthenticationToken(getJwt(), grantedAuthorities);
    }

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
