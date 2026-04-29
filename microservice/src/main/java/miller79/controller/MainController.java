package miller79.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles incoming HTTP requests and sends back responses for the microservice.
 *
 * <p>A "controller" in Spring is like a receptionist — it listens for incoming web requests
 * (e.g., someone visiting a URL in a browser or another service calling this API) and decides
 * what response to send back.
 *
 * <p>This controller has a single endpoint at the root URL ({@code /}) that returns "Hello world."
 * It's intentionally simple because this microservice exists mainly to be called by the
 * main API modules to demonstrate service-to-service communication with security tokens.
 *
 * @see <a href=
 *      "https://docs.spring.io/spring-framework/reference/web/webflux/controller.html">Annotated
 *      Controllers</a>
 */
@RestController
class MainController {
    /**
     * Returns a simple greeting message.
     *
     * @return the string "Hello world" as the HTTP response body
     */
    @GetMapping
    public String hello() {
        return "Hello world";
    }
}
