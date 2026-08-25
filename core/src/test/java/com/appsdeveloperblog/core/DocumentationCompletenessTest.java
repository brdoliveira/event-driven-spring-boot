package com.appsdeveloperblog.core;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DocumentationCompletenessTest {

    @Test
    @DisplayName("@spec:AC-014 README documents local execution and saga demonstration")
    void READMEContainsOperationalWorkflow() throws Exception {
        String readme = Files.readString(Path.of("..", "README.md"));

        assertTrue(readme.contains("Java 17"));
        assertTrue(readme.contains("docker compose up -d"));
        assertTrue(readme.contains("orders-commands"));
        assertTrue(readme.contains("payments-events"));
        assertTrue(readme.contains("PAYMENT_PROCESSOR_CONNECT_TIMEOUT"));
        assertTrue(readme.contains("http://localhost:8080/orders"));
        assertTrue(readme.contains("/history"));
        assertTrue(readme.contains("Troubleshooting"));
    }
}
