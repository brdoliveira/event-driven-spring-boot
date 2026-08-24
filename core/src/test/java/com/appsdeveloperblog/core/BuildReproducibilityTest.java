package com.appsdeveloperblog.core;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BuildReproducibilityTest {

    @Test
    @DisplayName("@spec:AC-001 reactor includes core and all four services")
    void reactorIncludesEveryModuleRequiredByProductionReadiness_spec_AC_001() throws Exception {
        Path reactorPom = Path.of("..", "pom.xml").toAbsolutePath().normalize();
        String pom = Files.readString(reactorPom);

        assertTrue(pom.contains("<module>core</module>"));
        assertTrue(pom.contains("<module>orders-service</module>"));
        assertTrue(pom.contains("<module>products-service</module>"));
        assertTrue(pom.contains("<module>payments-service</module>"));
        assertTrue(pom.contains("<module>credit-card-processor-service</module>"));
    }
}
