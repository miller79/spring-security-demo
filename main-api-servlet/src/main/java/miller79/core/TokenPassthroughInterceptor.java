package miller79.core;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

/**
 * @see <a href=
 *      "https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#_creating_a_restclient">Rest
 *      Client</a>
 */
@Component
public class TokenPassthroughInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(
            HttpRequest httpRequest,
            byte[] bytes,
            ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        String token = AuthTokenUtil.getAuthTokenFromSecurityContext();

        if (StringUtils.isNotBlank(token)) {
            httpRequest.getHeaders().setBearerAuth(token);
        }

        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }
}
