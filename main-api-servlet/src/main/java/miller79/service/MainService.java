package miller79.service;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

/**
 * @see <a href=
 *      "https://docs.spring.io/spring-framework/reference/integration/rest-clients.html">REST
 *      Clients</a>
 */
@Service
public class MainService {
    private final RestClient microserviceTokenPassthroughRestClient;
    private final RestTemplate microserviceTokenPassthroughRestTemplate;
    private final RestClient microserviceOAuth2ClientRestClient;

    public MainService(RestClient.Builder microserviceTokenPassthroughRestClientBuilder,
            RestTemplate microserviceTokenPassthroughRestTemplate,
            RestClient.Builder microserviceOAuth2ClientRestClientBuilder) {
        super();
        this.microserviceTokenPassthroughRestClient = microserviceTokenPassthroughRestClientBuilder.build();
        this.microserviceTokenPassthroughRestTemplate = microserviceTokenPassthroughRestTemplate;
        this.microserviceOAuth2ClientRestClient = microserviceOAuth2ClientRestClientBuilder.build();
    }

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

    public String microserviceCallWithTokenPassthroughRestTemplate() {
        try {
            return microserviceTokenPassthroughRestTemplate.getForObject("/", String.class);
        } catch (RestClientResponseException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getMessage());
        }
    }

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
