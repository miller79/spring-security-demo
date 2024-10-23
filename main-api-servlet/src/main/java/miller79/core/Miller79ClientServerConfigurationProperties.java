package miller79.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @see <a href=
 *      "https://docs.spring.io/spring-boot/reference/features/external-config.html#features.external-config.typesafe-configuration-properties.third-party-configuration">Externalized
 *      Configuration</a>
 */
@Data
@ConfigurationProperties("miller79.client")
public class Miller79ClientServerConfigurationProperties {
    private String microserviceTokenPassthroughBaseUrl;
    private String microserviceOAuth2ClientBaseUrl;
}
