package miller79;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * The starting point of the servlet-based main API application.
 *
 * <p>This is the "servlet" variant of the main API, meaning it uses Spring MVC — the traditional,
 * one-request-per-thread web framework. (The other variant, main-api-reactive, uses WebFlux
 * for non-blocking I/O.)
 *
 * <p>The {@code @ConfigurationPropertiesScan} annotation tells Spring Boot to automatically
 * discover any classes annotated with {@code @ConfigurationProperties} in this package
 * and its sub-packages, so their values get loaded from {@code application.yml} at startup.
 *
 * @see <a href=
 *      "https://docs.spring.io/spring-boot/reference/using/using-the-springbootapplication-annotation.html">Using the @SpringBootApplication Annotation</a>
 * @see <a href="https://docs.spring.io/spring-boot/reference/features/external-config.html#features.external-config.typesafe-configuration-properties.enabling-annotated-types">Enabling @ConfigurationProperties-annotated Types</a>
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class Application {
    /**
     * Launches the servlet-based main API application.
     *
     * @param args command-line arguments passed when starting the application
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
