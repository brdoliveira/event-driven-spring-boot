package com.appsdeveloperblog.core;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BuildQualityConfigurationTest {

    @Test
    @DisplayName("@spec:AC-015 verify generates and publishes JaCoCo reports")
    void BuildAndCIConfigureJaCoCoReports() throws Exception {
        String pom = Files.readString(Path.of("..", "pom.xml"));
        String ci = Files.readString(Path.of("..", ".github", "workflows", "ci.yml"));

        assertTrue(pom.contains("jacoco-maven-plugin"));
        assertTrue(pom.contains("prepare-agent"));
        assertTrue(pom.contains("<phase>verify</phase>"));
        assertTrue(pom.contains("<goal>report</goal>"));
        assertTrue(ci.contains("./mvnw -B verify"));
        assertTrue(ci.contains("actions/upload-artifact@v4"));
        assertTrue(ci.contains("**/target/site/jacoco/**"));
    }
}
