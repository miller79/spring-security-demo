package miller79.security;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.env.MockPropertySource;

@DisplayName("Client secret startup validation")
class ClientSecretEnvironmentPostProcessorTest {
    private final ClientSecretEnvironmentPostProcessor processor = new ClientSecretEnvironmentPostProcessor();

    private void process(String value) {
        var env = new StandardEnvironment();
        var source = new MockPropertySource();
        if (value != null) {
            source.setProperty(ClientSecretEnvironmentPostProcessor.PROPERTY, value);
        }
        env.getPropertySources().addFirst(source);
        processor.postProcessEnvironment(env, new SpringApplication());
    }

    @Test
    void shouldFailStartup_whenSecretIsMissing() {
        assertThatThrownBy(() -> process(null))
                .hasMessage(ClientSecretEnvironmentPostProcessor.MESSAGE);
    }

    @Test
    void shouldFailStartup_whenSecretIsBlank() {
        assertThatThrownBy(() -> process("  ")).hasMessage(ClientSecretEnvironmentPostProcessor.MESSAGE);
    }

    @Test
    void shouldFailStartup_whenPlaceholderIsUnresolved() {
        assertThatThrownBy(() -> process("${OAUTH2_CLIENT_SECRET}"))
                .hasMessage(ClientSecretEnvironmentPostProcessor.MESSAGE);
    }

    @Test
    void shouldFailStartup_whenApplicationYamlPlaceholderIsUnset() throws Exception {
        var env = new StandardEnvironment();
        new YamlPropertySourceLoader()
                .load("application", new ClassPathResource("application.yaml"))
                .forEach(env.getPropertySources()::addLast);
        assertThatThrownBy(() -> processor.postProcessEnvironment(env, new SpringApplication()))
                .hasMessage(ClientSecretEnvironmentPostProcessor.MESSAGE);
    }

    @Test
    void shouldStart_whenSecretIsProvided() {
        assertThatCode(() -> process("test-only-dummy")).doesNotThrowAnyException();
    }
}
