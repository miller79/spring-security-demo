package miller79;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;
import org.springframework.modulith.docs.Documenter.DiagramOptions;
import org.springframework.modulith.docs.Documenter.DiagramOptions.ElementsWithoutRelationships;

import lombok.extern.slf4j.Slf4j;

/**
 * Validates the module structure of the reactive main API using Spring Modulith.
 *
 * <p>Ensures packages within the reactive API don't have hidden or circular dependencies
 * and generates PlantUML architecture diagrams.
 */
@Slf4j
@DisplayName("Spring Modulith Verification")
class ModulithTest {
    ApplicationModules modules = ApplicationModules.of(Application.class);

    /**
     * Verifies that all application modules follow Spring Modulith's dependency rules and
     * generates architecture diagrams as PlantUML files.
     */
    @Test
    void shouldVerifyModuleStructure() {
        for (var module : modules) {
            log.info("module: {}:{}", module.getIdentifier(), module.getBasePackage());
        }

        modules.verify();

        new Documenter(modules)
                .writeIndividualModulesAsPlantUml()
                .writeModulesAsPlantUml(DiagramOptions
                        .defaults()
                        .withElementsWithoutRelationships(ElementsWithoutRelationships.VISIBLE));
    }
}
