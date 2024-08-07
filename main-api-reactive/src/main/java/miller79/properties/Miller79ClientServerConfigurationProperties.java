package miller79.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties("miller79.client")
public class Miller79ClientServerConfigurationProperties {
    private String microserviceTokenPassthroughBaseUrl;
    private String microserviceOAuth2ClientBaseUrl;
}
