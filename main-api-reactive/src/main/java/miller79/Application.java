package miller79;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * @see <a href=
 *      "https://docs.spring.io/spring-boot/reference/using/using-the-springbootapplication-annotation.html">Using the @SpringBootApplication Annotation</a>
 * @see <a href="https://docs.spring.io/spring-boot/reference/features/external-config.html#features.external-config.typesafe-configuration-properties.enabling-annotated-types">Enabling @ConfigurationProperties-annotated Types</a>
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
