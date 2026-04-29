package miller79.core;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

/**
 * An HTTP request interceptor that forwards ("passes through") the current user's security
 * token to downstream service calls.
 *
 * <p>When the main API calls a downstream microservice, that microservice also needs to verify
 * who the original caller is. This interceptor solves that by:
 * <ol>
 *   <li>Retrieving the current user's JWT token from Spring Security's {@code SecurityContext}</li>
 *   <li>Attaching it as a "Bearer" authorization header on the outgoing HTTP request</li>
 * </ol>
 *
 * <p>If no token is present (e.g., the request is unauthenticated), the interceptor skips the
 * header and lets the request proceed without a token — the downstream service will then
 * reject or accept the request based on its own security rules.
 *
 * <p>This interceptor works with both {@code RestClient} and {@code RestTemplate}.
 *
 * @see <a href=
 *      "https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#_creating_a_restclient">Rest
 *      Client</a>
 */
@Component
public class TokenPassthroughInterceptor implements ClientHttpRequestInterceptor {
    /**
     * Intercepts an outgoing HTTP request and adds the current user's JWT as a Bearer token header.
     *
     * @param httpRequest the outgoing HTTP request being intercepted
     * @param bytes the request body as a byte array
     * @param clientHttpRequestExecution the execution chain to continue the request
     * @return the HTTP response from the downstream service
     * @throws IOException if an I/O error occurs during request execution
     */
    @Override
    public ClientHttpResponse intercept(
            HttpRequest httpRequest,
            byte[] bytes,
            ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        String token = AuthTokenUtil.getAuthTokenFromSecurityContext();

        if (token != null && !token.isBlank()) {
            httpRequest.getHeaders().setBearerAuth(token);
        }

        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }
}
