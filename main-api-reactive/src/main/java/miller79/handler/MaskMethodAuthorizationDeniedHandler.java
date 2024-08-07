// TODO Can't seem to get this working
//package miller79.handler;
//
//import java.util.Optional;
//
//import org.aopalliance.intercept.MethodInvocation;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.security.authorization.AuthorizationResult;
//import org.springframework.security.authorization.method.MethodAuthorizationDeniedHandler;
//import org.springframework.stereotype.Component;
//
//import miller79.model.SemiSecretObject;
//
//import reactor.core.publisher.Mono;
//
//@Component
//public class MaskMethodAuthorizationDeniedHandler implements MethodAuthorizationDeniedHandler {
//    @Override
//    public Object handleDeniedInvocation(MethodInvocation methodInvocation, AuthorizationResult authorizationResult) {
//        return Optional
//                .of(methodInvocation.getThis())
//                .filter(SemiSecretObject.class::isInstance)
//                .filter(obj -> methodInvocation.getMethod().getName().equals("getPhoneNumber"))
//                .map(SemiSecretObject.class::cast)
//                .map(SemiSecretObject::getPhoneNumber)
//                .map(Mono::block)
//                .map(phone -> "***-***-" + StringUtils.right(phone, 4))
//                .orElse("***");
//    }
//}