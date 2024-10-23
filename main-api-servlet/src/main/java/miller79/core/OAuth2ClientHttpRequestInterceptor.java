package miller79.core;

import java.io.IOException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Component;

import com.nimbusds.oauth2.sdk.util.StringUtils;

import lombok.RequiredArgsConstructor;

/**
 * @see <a href=
 *      "https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#_creating_a_restclient">Rest
 *      Client</a>
 * @see <a href=
 *      "https://docs.spring.io/spring-security/reference/servlet/oauth2/client/authorized-clients.html">Authorized
 *      Client Features</a>
 */
@Component
@RequiredArgsConstructor
public class OAuth2ClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    private final OAuth2AuthorizedClientManager authorizedClientManager;

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        String token = getToken();

        if (StringUtils.isNotBlank(token)) {
            request.getHeaders().setBearerAuth(token);
        }

        return execution.execute(request, body);
    }

    public String getToken() {
        OAuth2AuthorizedClient authorizedClient = authorizedClientManager
                .authorize(
                        OAuth2AuthorizeRequest.withClientRegistrationId("keycloak").principal(applicationName).build());
        return Objects.requireNonNull(authorizedClient).getAccessToken().getTokenValue();
    }
}
