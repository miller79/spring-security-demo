package miller79;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * The starting point of the reactive main API application.
 *
 * <p>This is the "reactive" variant of the main API, meaning it uses Spring WebFlux — a
 * non-blocking web framework that can handle many concurrent requests with fewer threads.
 * (The other variant, main-api-servlet, uses the traditional blocking Spring MVC framework.)
 *
 * <p>{@code @ConfigurationPropertiesScan} tells Spring Boot to automatically discover and bind
 * configuration property classes from the application's YAML/properties files.
 *
 * @see <a href=
 *      "https://docs.spring.io/spring-boot/reference/using/using-the-springbootapplication-annotation.html">Using the @SpringBootApplication Annotation</a>
 * @see <a href="https://docs.spring.io/spring-boot/reference/features/external-config.html#features.external-config.typesafe-configuration-properties.enabling-annotated-types">Enabling @ConfigurationProperties-annotated Types</a>
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class Application {
    /**
     * Launches the reactive main API application.
     *
     * @param args command-line arguments passed when starting the application
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
