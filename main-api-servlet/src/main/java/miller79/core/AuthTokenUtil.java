package miller79.core;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AuthTokenUtil {
    public String getAuthTokenFromSecurityContext() {
        return Optional
                .ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(Jwt.class::isInstance)
                .map(Jwt.class::cast)
                .map(Jwt::getTokenValue)
                .orElse(null);
    }
}
