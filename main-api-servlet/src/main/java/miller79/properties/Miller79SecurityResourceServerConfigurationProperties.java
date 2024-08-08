package miller79.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @see <a href=
 *      "https://docs.spring.io/spring-boot/reference/features/external-config.html#features.external-config.typesafe-configuration-properties.third-party-configuration">Externalized
 *      Configuration</a>
 */
@Data
@ConfigurationProperties("miller79.security.resource-server")
public class Miller79SecurityResourceServerConfigurationProperties {
    private String clientId;
}
