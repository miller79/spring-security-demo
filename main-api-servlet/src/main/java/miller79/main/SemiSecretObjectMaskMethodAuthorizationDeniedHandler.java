package miller79.main;

import java.util.Optional;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.authorization.method.MethodAuthorizationDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * @see <a href="https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html#_using_the_denied_result_from_the_method_invocation">Using the Denied Result From the Method Invocation</a>
 */
@Component
class SemiSecretObjectMaskMethodAuthorizationDeniedHandler implements MethodAuthorizationDeniedHandler {
    @Override
    public Object handleDeniedInvocation(MethodInvocation methodInvocation, AuthorizationResult authorizationResult) {
        return Optional
                .of(methodInvocation.getThis())
                .filter(SemiSecretObject.class::isInstance)
                .filter(obj -> methodInvocation.getMethod().getName().equals("getPhoneNumber"))
                .map(SemiSecretObject.class::cast)
                .map(SemiSecretObject::getPhoneNumber)
                .map(phone -> "***-***-" + StringUtils.right(phone, 4))
                .orElse("***");
    }
}
