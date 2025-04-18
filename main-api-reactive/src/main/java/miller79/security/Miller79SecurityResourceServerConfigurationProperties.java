package miller79.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties("miller79.security.resource-server")
public class Miller79SecurityResourceServerConfigurationProperties {
    private String clientId;
}
