package miller79.main;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Handles all incoming HTTP requests for the reactive main API.
 *
 * <p>This controller is the reactive (non-blocking) counterpart to the servlet module's
 * {@code MainController}. Instead of returning plain Java objects, each method returns a
 * {@link reactor.core.publisher.Mono Mono} — a reactive wrapper that represents a value
 * that will be available in the future. This allows the server to handle thousands of
 * requests concurrently without blocking threads.
 *
 * <p>This controller demonstrates:
 * <ul>
 *   <li><b>Token passthrough</b> — forwarding the caller's JWT to the downstream microservice</li>
 *   <li><b>OAuth2 client credentials</b> — using a machine-to-machine token for service calls</li>
 *   <li><b>Pre-authorization</b> — restricting endpoints by permission using {@code @PreAuthorize}</li>
 *   <li><b>URL-based authorization</b> — restricting endpoints via security configuration</li>
 *   <li><b>Inline field masking</b> — manually masking sensitive data based on the user's
 *       authorities (the reactive version handles this differently from servlet because
 *       {@code @AuthorizeReturnObject} is not supported in the reactive stack)</li>
 * </ul>
 *
 * @see <a href=
 *      "https://docs.spring.io/spring-framework/reference/web/webflux/controller.html">Annotated
 *      Controllers</a>
 * @see <a href=
 *      "https://docs.spring.io/spring-security/reference/reactive/authorization/method.html#page-title">Method
 *      Security</a>
 */
@RequiredArgsConstructor
@RestController
class MainController {
    private static final String HELLO_WORLD = "Hello world";
    private final MainService mainService;

    /**
     * Calls the downstream microservice using WebClient, forwarding the current user's JWT token.
     *
     * @return a Mono emitting the response from the downstream microservice
     */
    @GetMapping("/web-client-token-passthrough")
    public Mono<String> helloWebClientTokenPassthrough() {
        return mainService.microserviceCallWithTokenPassthrough();
    }

    /**
     * Calls the downstream microservice without requiring caller authentication.
     *
     * @return a Mono emitting the response from the downstream microservice
     */
    @GetMapping("/web-client-token-passthrough-no-auth")
    public Mono<String> helloRestClientTokenPassthroughNoAuth() {
        return mainService.microserviceCallWithTokenPassthrough();
    }

    /**
     * Calls the downstream microservice using a machine-to-machine OAuth2 client credentials token.
     *
     * @return a Mono emitting the response from the downstream microservice
     */
    @GetMapping("/web-client-oauth2-client-no-auth")
    public Mono<String> helloRestClientOAuth2ClientNoAuth() {
        return mainService.microserviceCallWithOAuth2ClientRestClient();
    }

    /**
     * Returns a greeting, but only if the caller has the {@code permission:read} authority.
     *
     * @return a Mono emitting "Hello world" if authorized; otherwise a 403 Forbidden error
     */
    @GetMapping("/preauth-read")
    @PreAuthorize("hasAuthority('permission:read')")
    public Mono<String> helloPreAuthRoleRead() {
        return Mono.just(HELLO_WORLD);
    }

    /**
     * Returns a greeting, restricted to {@code permission:read} via the security configuration.
     *
     * @return a Mono emitting "Hello world" — access is controlled by URL-based authorization rules
     */
    @GetMapping("/security-config-role-read")
    public Mono<String> helloSecurityConfigRoleRead() {
        return Mono.just(HELLO_WORLD);
    }

    /**
     * Returns a {@link SemiSecretObject} with sensitive fields masked or visible depending
     * on the caller's authorities.
     *
     * <p>Unlike the servlet module (which uses {@code @AuthorizeReturnObject} for automatic
     * masking), the reactive module performs manual masking by checking the user's authorities
     * via {@code ReactiveSecurityContextHolder} and building the response accordingly.
     *
     * @return a Mono emitting a {@link SemiSecretObject} with field-level masking applied
     */
    @GetMapping("/fetch-semi-secure-object")
    public Mono<SemiSecretObject> fetchSemiSecureObject() {
        return ReactiveSecurityContextHolder.getContext()
                .map(context -> {
                    var authentication = context.getAuthentication();
                    boolean hasFullAccess = authentication.getAuthorities().stream()
                            .anyMatch(auth -> auth.getAuthority().equals("name:miller79"));

                    String phone = "123-456-7890";
                    if (hasFullAccess) {
                        return SemiSecretObject.builder()
                                .name("Secret Person")
                                .ssn("123-45-6789")
                                .phoneNumber(phone)
                                .build();
                    } else {
                        return SemiSecretObject.builder()
                                .name("Secret Person")
                                .ssn("***")
                                .phoneNumber("***-***-" + phone.substring(Math.max(0, phone.length() - 4)))
                                .build();
                    }
                });
    }
}
