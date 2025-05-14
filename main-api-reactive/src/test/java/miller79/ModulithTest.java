package miller79;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;
import org.springframework.modulith.docs.Documenter.DiagramOptions;
import org.springframework.modulith.docs.Documenter.DiagramOptions.ElementsWithoutRelationships;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class ModulithTest {
    ApplicationModules modules = ApplicationModules.of(Application.class);

    @Test
    void verifyModules() {
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
