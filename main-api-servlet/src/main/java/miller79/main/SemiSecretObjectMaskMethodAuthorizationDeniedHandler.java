package miller79.main;

import java.util.Optional;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.authorization.method.MethodAuthorizationDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Handles authorization denials on {@link SemiSecretObject} fields by returning masked values
 * instead of throwing an access-denied error.
 *
 * <p>Normally, when Spring Security blocks access to a method (because the user lacks the required
 * permission), it throws a 403 Forbidden exception. This handler overrides that behavior —
 * instead of an error, it returns a "masked" version of the data:
 * <ul>
 *   <li>For the {@code phoneNumber} field: returns a partially masked value like "***-***-7890"
 *       (showing only the last 4 digits)</li>
 *   <li>For all other fields (like {@code ssn}): returns "***"</li>
 * </ul>
 *
 * <p>This pattern is useful when you want to return a complete JSON response but hide specific
 * sensitive fields from unauthorized users.
 *
 * @see <a href="https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html#_using_the_denied_result_from_the_method_invocation">Using the Denied Result From the Method Invocation</a>
 */
@Component
class SemiSecretObjectMaskMethodAuthorizationDeniedHandler implements MethodAuthorizationDeniedHandler {
    /**
     * Returns a masked value instead of throwing an access-denied exception.
     *
     * @param methodInvocation contains information about the method that was denied — which object, which method name, etc.
     * @param authorizationResult the authorization decision that caused the denial
     * @return a masked string: partially masked phone number, or "***" for other fields
     */
    @Override
    public Object handleDeniedInvocation(MethodInvocation methodInvocation, AuthorizationResult authorizationResult) {
        return Optional
                .of(methodInvocation.getThis())
                .filter(SemiSecretObject.class::isInstance)
                .filter(obj -> methodInvocation.getMethod().getName().equals("getPhoneNumber"))
                .map(SemiSecretObject.class::cast)
                .map(SemiSecretObject::getPhoneNumber)
                .map(phone -> "***-***-" + phone.substring(Math.max(0, phone.length() - 4)))
                .orElse("***");
    }
}
