package com.github.matek2305.betting;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.github.matek2305.betting", importOptions = ImportOption.DoNotIncludeTests.class)
class BettingArchitectureTest {

    @ArchTest
    static final ArchRule core_should_not_depend_on_configuration =
            noClasses()
                    .that()
                    .resideInAPackage("..betting.core..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("..betting.configuration..");

    @ArchTest
    static final ArchRule commons_should_not_depend_on_core =
            noClasses()
                    .that()
                    .resideInAPackage("..betting.commons..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage("..betting.core..");

    @ArchTest
    static final ArchRule domains_should_not_depend_on_framework =
            noClasses()
                    .that()
                    .resideInAPackage("..betting.core..domain..")
                    .should()
                    .dependOnClassesThat()
                    .resideOutsideOfPackages(
                            "..betting.core..domain..",
                            "..betting.commons..",
                            "com.google.common..",
                            "io.vavr..",
                            "java..",
                            "lombok..",
                            "org.slf4j.."
                    );
}
