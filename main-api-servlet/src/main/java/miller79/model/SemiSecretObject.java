package miller79.model;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.method.HandleAuthorizationDenied;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import miller79.handler.MaskMethodAuthorizationDeniedHandler;

@Builder
@RequiredArgsConstructor
@JsonSerialize(as = SemiSecretObject.class)
public class SemiSecretObject {
    private final String name;
    private final String ssn;
    private final String phoneNumber;

    public String getName() {
        return name;
    }

    @PreAuthorize("hasAuthority('name:miller79')")
    @HandleAuthorizationDenied(handlerClass = MaskMethodAuthorizationDeniedHandler.class)
    public String getSsn() {
        return ssn;
    }

    @PreAuthorize("hasAuthority('name:miller79')")
    @HandleAuthorizationDenied(handlerClass = MaskMethodAuthorizationDeniedHandler.class)
    public String getPhoneNumber() {
        return phoneNumber;
    }
}
