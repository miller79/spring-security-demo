package miller79.main;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Calls the downstream microservice using Spring's reactive
 * {@link org.springframework.web.reactive.function.client.WebClient WebClient}.
 *
 * <p>This is the reactive counterpart to the servlet module's {@code MainService}. Instead of
 * {@code RestClient}/{@code RestTemplate}, it uses {@code WebClient} — a non-blocking HTTP
 * client designed for reactive applications. Two WebClient instances are injected:
 * <ul>
 *   <li><b>Token passthrough WebClient</b> — forwards the current user's JWT token to the
 *       downstream microservice</li>
 *   <li><b>OAuth2 client WebClient</b> — obtains its own machine-to-machine token from
 *       Keycloak for server-to-server calls</li>
 * </ul>
 *
 * <p>Each method returns a {@link reactor.core.publisher.Mono Mono&lt;String&gt;} that will emit
 * the microservice's response when it arrives, without blocking the calling thread.
 *
 * @see <a href=
 *      "https://docs.spring.io/spring-framework/reference/integration/rest-clients.html">REST
 *      Clients</a>
 */
@RequiredArgsConstructor
@Service
class MainService {
    private final WebClient microserviceTokenPassthroughWebClient;
    private final WebClient microserviceOAuth2ClientWebClient;

    /**
     * Calls the downstream microservice using WebClient with the user's forwarded JWT token.
     *
     * @return a Mono emitting the response body from the downstream microservice
     */
    public Mono<String> microserviceCallWithTokenPassthrough() {
        return microserviceTokenPassthroughWebClient
                .get()
                .uri("/")
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        response -> Mono.error(new ResponseStatusException(response.statusCode())))
                .bodyToMono(String.class);
    }

    /**
     * Calls the downstream microservice using WebClient with an OAuth2 client credentials token.
     *
     * @return a Mono emitting the response body from the downstream microservice
     */
    public Mono<String> microserviceCallWithOAuth2ClientRestClient() {
        return microserviceOAuth2ClientWebClient
                .get()
                .uri("/")
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        response -> Mono.error(new ResponseStatusException(response.statusCode())))
                .bodyToMono(String.class);
    }
}
