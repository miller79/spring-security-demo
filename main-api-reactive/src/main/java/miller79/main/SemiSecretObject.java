// TODO: Can't get this working
//package miller79.model;
//
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.authorization.method.HandleAuthorizationDenied;
//
//import com.fasterxml.jackson.databind.annotation.JsonSerialize;
//import miller79.handler.MaskMethodAuthorizationDeniedHandler;
//
//import lombok.Builder;
//import lombok.RequiredArgsConstructor;
//
//@Builder
//@RequiredArgsConstructor
//@JsonSerialize(as = SemiSecretObject.class)
//public class SemiSecretObject {
//    private final String name;
//    private final String ssn;
//    private final String phoneNumber;
//
//    public String getName() {
//        return name;
//    }
//
//    @PreAuthorize("hasAuthority('security360-attribute:EL - Spring Security:read:test-attribute:2345')")
//    @HandleAuthorizationDenied(handlerClass = MaskMethodAuthorizationDeniedHandler.class)
//    public String getSsn() {
//        return ssn;
//    }
//
//    @PreAuthorize("hasAuthority('security360-attribute:EL - Spring Security:read:test-attribute:2345')")
//    @HandleAuthorizationDenied(handlerClass = MaskMethodAuthorizationDeniedHandler.class)
//    public String getPhoneNumber() {
//        return phoneNumber;
//    }
//}