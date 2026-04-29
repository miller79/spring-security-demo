package miller79.main;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/**
 * Handles all incoming HTTP requests for the servlet-based main API.
 *
 * <p>This controller demonstrates several Spring Security patterns:
 * <ul>
 *   <li><b>Token passthrough</b> — forwarding the caller's JWT token to a downstream
 *       microservice (so the microservice knows who the original caller is)</li>
 *   <li><b>OAuth2 client credentials</b> — the API obtains its own token (separate from
 *       the user's) to call a downstream service, useful for machine-to-machine communication</li>
 *   <li><b>Pre-authorization</b> — using {@code @PreAuthorize} to restrict specific endpoints
 *       to users who have certain permissions (like "read")</li>
 *   <li><b>URL-based authorization</b> — restricting endpoints via the security configuration
 *       instead of annotations</li>
 *   <li><b>Return-object security</b> — using {@code @AuthorizeReturnObject} to mask sensitive
 *       fields in the response based on the caller's permissions</li>
 * </ul>
 *
 * @see <a href=
 *      "https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller.html">Annotated
 *      Controllers</a>
 * @see <a href="https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html#use-secured">Method Security</a>
 */
@RequiredArgsConstructor
@RestController
class MainController {
    private static final String HELLO_WORLD = "Hello world";
    private final MainService mainService;

    /**
     * Calls the downstream microservice using RestClient, forwarding the current user's JWT token.
     *
     * @return the response from the downstream microservice, called with the user's forwarded token via RestClient
     */
    @GetMapping("/rest-client-token-passthrough")
    public String helloRestClientTokenPassthrough() {
        return mainService.microserviceCallWithTokenPassthroughRestClient();
    }

    /**
     * Calls the downstream microservice using RestTemplate, forwarding the current user's JWT token.
     *
     * @return the response from the downstream microservice, called with the user's forwarded token via RestTemplate
     */
    @GetMapping("/rest-template-token-passthrough")
    public String helloRestTemplateTokenPassthrough() {
        return mainService.microserviceCallWithTokenPassthroughRestTemplate();
    }

    /**
     * Calls the downstream microservice without requiring caller authentication.
     *
     * @return the response from the downstream microservice, accessible without authentication
     */
    @GetMapping("/rest-client-token-passthrough-no-auth")
    public String helloRestClientTokenPassthroughNoAuth() {
        return mainService.microserviceCallWithTokenPassthroughRestClient();
    }

    /**
     * Calls the downstream microservice using a machine-to-machine OAuth2 client credentials token.
     *
     * @return the response from the downstream microservice, called with an OAuth2 client credentials token
     */
    @GetMapping("/rest-client-oauth2-client-no-auth")
    public String helloRestClientOAuth2ClientNoAuth() {
        return mainService.microserviceCallWithOAuth2ClientRestClient();
    }

    /**
     * Returns a greeting, but only if the caller has the {@code permission:read} authority.
     *
     * @return "Hello world" if the caller has the required authority; otherwise a 403 Forbidden error
     */
    @GetMapping("/preauth-read")
    @PreAuthorize("hasAuthority('permission:read')")
    public String helloPreAuthRoleRead() {
        return HELLO_WORLD;
    }

    /**
     * Returns a greeting, restricted to {@code permission:read} via the security configuration.
     *
     * @return "Hello world" — access is controlled by URL-based authorization rules, not an annotation
     */
    @GetMapping("/security-config-role-read")
    public String helloSecurityConfigRoleRead() {
        return HELLO_WORLD;
    }

    /**
     * Returns a {@link SemiSecretObject} with sensitive fields masked or visible depending
     * on the caller's authorities.
     *
     * @return a {@link SemiSecretObject} with field-level security applied via {@code @AuthorizeReturnObject}
     */
    @GetMapping("/fetch-semi-secure-object")
    @AuthorizeReturnObject
    public SemiSecretObject fetchSemiSecureObject() {
        return SemiSecretObject.builder().name("Secret Person").ssn("123-45-6789").phoneNumber("123-456-7890").build();
    }

}