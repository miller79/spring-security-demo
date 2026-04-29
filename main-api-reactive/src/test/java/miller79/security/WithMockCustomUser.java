package miller79.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.test.context.support.WithSecurityContext;

/**
 * Custom test annotation that simulates an authenticated user with a JWT token for
 * integration tests.
 *
 * <p>Spring Security provides {@code @WithMockUser} for simple test scenarios, but it only
 * simulates a basic username/password user. In applications that use OAuth2/JWT (like this
 * one), you need a more realistic simulation that includes a JWT token with claims like
 * name, email, and username.
 *
 * <p>Annotate a test method with {@code @WithMockCustomUser} to run it as if a JWT-authenticated
 * user is logged in. You can optionally specify authorities (permissions) the simulated user
 * should have — for example, {@code @WithMockCustomUser(authorities = {"permission:read"})}.
 *
 * <p>The actual JWT and security context are created by
 * {@link WithMockCustomUserSecurityContextFactory}.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
    /**
     * The list of granted authority strings the mock user should have.
     *
     * @return the authority strings (default: empty — no special permissions)
     */
    String[] authorities() default {};
}
