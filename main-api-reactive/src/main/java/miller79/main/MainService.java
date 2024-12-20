package miller79.main;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * @see <a href=
 *      "https://docs.spring.io/spring-framework/reference/integration/rest-clients.html">REST
 *      Clients</a>
 */
@RequiredArgsConstructor
@Service
public class MainService {
    private final WebClient microserviceTokenPassthroughWebClient;
    private final WebClient microserviceOAuth2ClientWebClient;

    public Mono<String> microserviceCallWithTokenPassthrough() {
        return microserviceTokenPassthroughWebClient
                .get()
                .uri("/")
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        response -> Mono.error(new ResponseStatusException(response.statusCode())))
                .bodyToMono(String.class);
    }

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
