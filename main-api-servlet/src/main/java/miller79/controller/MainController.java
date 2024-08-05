package miller79.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import miller79.model.SemiSecretObject;
import miller79.service.MainService;

@RequiredArgsConstructor
@RestController
public class MainController {
    private static final String HELLO_WORLD = "Hello world";
    private final MainService mainService;

    @GetMapping("/rest-client-token-passthrough")
    public String helloRestClientTokenPassthrough() {
        return mainService.microserviceCallWithTokenPassthroughRestClient();
    }

    @GetMapping("/rest-template-token-passthrough")
    public String helloRestTemplateTokenPassthrough() {
        return mainService.microserviceCallWithTokenPassthroughRestTemplate();
    }

    @GetMapping("/rest-client-token-passthrough-no-auth")
    public String helloRestClientTokenPassthroughNoAuth() {
        return mainService.microserviceCallWithTokenPassthroughRestClient();
    }

    @GetMapping("/rest-client-oauth2-client-no-auth")
    public String helloRestClientOAuth2ClientNoAuth() {
        return mainService.microserviceCallWithOAuth2ClientRestClient();
    }

    @GetMapping("/preauth-read")
    @PreAuthorize("hasAuthority('permission:read')")
    public String helloRestClientPreauthRoleRead() {
        return HELLO_WORLD;
    }

    @GetMapping("/security-config-role-read")
    public String helloRestClientSecurityConfigRoleRead() {
        return HELLO_WORLD;
    }

    @GetMapping("/fetch-semi-secure-object")
    @AuthorizeReturnObject
    public SemiSecretObject fetchSemiSecureObject() {
        return SemiSecretObject.builder().name("Secret Person").ssn("123-45-6789").phoneNumber("123-456-7890").build();
    }

}