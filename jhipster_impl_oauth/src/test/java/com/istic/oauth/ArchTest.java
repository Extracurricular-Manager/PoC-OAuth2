package com.istic.oauth;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {
        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.istic.oauth");

        noClasses()
            .that()
            .resideInAnyPackage("com.istic.oauth.service..")
            .or()
            .resideInAnyPackage("com.istic.oauth.repository..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..com.istic.oauth.web..")
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses);
    }
}
