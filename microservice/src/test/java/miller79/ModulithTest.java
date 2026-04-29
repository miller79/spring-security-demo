package miller79;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;
import org.springframework.modulith.docs.Documenter.DiagramOptions;
import org.springframework.modulith.docs.Documenter.DiagramOptions.ElementsWithoutRelationships;

import lombok.extern.slf4j.Slf4j;

/**
 * Validates the module structure of the microservice using Spring Modulith.
 *
 * <p>Spring Modulith is a tool that enforces clean architecture by checking that packages
 * (called "modules") within the application don't have hidden or circular dependencies
 * on each other. Think of it like a building inspector who makes sure the wiring in your
 * house follows code.
 *
 * <p>This test also generates PlantUML diagrams (visual architecture maps) showing how the
 * application's modules relate to each other. These diagrams are written to the build output
 * and can be used for documentation.
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
