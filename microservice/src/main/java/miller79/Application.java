package miller79;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The starting point of the microservice application.
 *
 * <p>In a Spring Boot application, you need one class annotated with {@code @SpringBootApplication}
 * to tell the framework: "this is where my app begins." When you run this class, Spring Boot
 * automatically sets up everything the app needs — connects to databases, configures security,
 * starts a web server, etc.
 *
 * <p>Think of this class like the ignition key for a car — it doesn't do much on its own,
 * but nothing runs without it.
 *
 * @see <a href=
 *      "https://docs.spring.io/spring-boot/reference/using/using-the-springbootapplication-annotation.html">Using the @SpringBootApplication Annotation</a>
 */
@SpringBootApplication
public class Application {
    /**
     * Launches the microservice application.
     *
     * @param args command-line arguments passed when starting the application (typically none for this service)
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
