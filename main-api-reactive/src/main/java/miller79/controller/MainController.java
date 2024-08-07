package miller79.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import miller79.service.MainService;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
public class MainController {
    private static final String HELLO_WORLD = "Hello world";
    private final MainService mainService;

    @GetMapping("/web-client-token-passthrough")
    public Mono<String> helloWebClientTokenPassthrough() {
        return mainService.microserviceCallWithTokenPassthrough();
    }

    @GetMapping("/web-client-token-passthrough-no-auth")
    public Mono<String> helloRestClientTokenPassthroughNoAuth() {
        return mainService.microserviceCallWithTokenPassthrough();
    }

    @GetMapping("/web-client-oauth2-client-no-auth")
    public Mono<String> helloRestClientOAuth2ClientNoAuth() {
        return mainService.microserviceCallWithOAuth2ClientRestClient();
    }

    @GetMapping("/preauth-read")
    @PreAuthorize("hasAuthority('permission:read')")
    public Mono<String> helloWebClientRoleRead() {
        return mainService.microserviceCallWithTokenPassthrough();
    }

    @GetMapping("/security-config-role-read")
    public String helloRestClientSecurityConfigRoleRead() {
        return HELLO_WORLD;
    }

// TODO: Can't get this working
//    @GetMapping("/fetch-semi-secure-object")
//    @AuthorizeReturnObject
//    public Mono<SemiSecretObject> fetchSemiSecureObject() {
//        return Mono
//                .just(SemiSecretObject
//                        .builder()
//                        .name("Secret Person")
//                        .ssn("123-45-6789")
//                        .phoneNumber("123-456-7890")
//                        .build());
//    }
}
