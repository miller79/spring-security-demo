package miller79.main;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

/**
 * Calls the downstream microservice using different HTTP client strategies.
 *
 * <p>A "service" in Spring sits between the controller (which receives web requests) and the
 * external systems (other APIs, databases, etc.). This service demonstrates three ways to
 * call another microservice with security tokens:
 * <ul>
 *   <li><b>RestClient with token passthrough</b> — uses Spring's modern {@code RestClient}
 *       and forwards the current user's JWT token to the downstream service</li>
 *   <li><b>RestTemplate with token passthrough</b> — uses the older {@code RestTemplate}
 *       (still widely used) with the same token-forwarding approach</li>
 *   <li><b>RestClient with OAuth2 client credentials</b> — the service obtains its own
 *       machine-to-machine token from the identity provider (Keycloak) instead of forwarding
 *       the user's token</li>
 * </ul>
 *
 * <p>All three methods call the same downstream microservice endpoint but differ in how
 * authentication is handled.
 *
 * @see <a href=
 *      "https://docs.spring.io/spring-framework/reference/integration/rest-clients.html">REST
 *      Clients</a>
 */
@Service
class MainService {
    private final RestClient microserviceTokenPassthroughRestClient;
    private final RestTemplate microserviceTokenPassthroughRestTemplate;
    private final RestClient microserviceOAuth2ClientRestClient;

    /**
     * Creates a new MainService with the configured HTTP clients.
     *
     * @param microserviceTokenPassthroughRestClientBuilder RestClient builder for token passthrough calls
     * @param microserviceTokenPassthroughRestTemplate RestTemplate for token passthrough calls
     * @param microserviceOAuth2ClientRestClientBuilder RestClient builder for OAuth2 client credentials calls
     */
    public MainService(RestClient.Builder microserviceTokenPassthroughRestClientBuilder,
            RestTemplate microserviceTokenPassthroughRestTemplate,
            RestClient.Builder microserviceOAuth2ClientRestClientBuilder) {
        this.microserviceTokenPassthroughRestClient = microserviceTokenPassthroughRestClientBuilder.build();
        this.microserviceTokenPassthroughRestTemplate = microserviceTokenPassthroughRestTemplate;
        this.microserviceOAuth2ClientRestClient = microserviceOAuth2ClientRestClientBuilder.build();
    }

    /**
     * Calls the downstream microservice using RestClient with the user's forwarded JWT token.
     *
     * @return the response body from the downstream microservice
     */
    public String microserviceCallWithTokenPassthroughRestClient() {
        return microserviceTokenPassthroughRestClient
                .get()
                .uri("/")
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new ResponseStatusException(response.getStatusCode());
                })
                .body(String.class);
    }

    /**
     * Calls the downstream microservice using RestTemplate with the user's forwarded JWT token.
     *
     * @return the response body from the downstream microservice
     */
    public String microserviceCallWithTokenPassthroughRestTemplate() {
        try {
            return microserviceTokenPassthroughRestTemplate.getForObject("/", String.class);
        } catch (RestClientResponseException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getMessage());
        }
    }

    /**
     * Calls the downstream microservice using RestClient with an OAuth2 client credentials token.
     *
     * @return the response body from the downstream microservice
     */
    public String microserviceCallWithOAuth2ClientRestClient() {
        return microserviceOAuth2ClientRestClient
                .get()
                .uri("/")
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new ResponseStatusException(response.getStatusCode());
                })
                .body(String.class);
    }
}
