package com.lunar.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SimpleTest {

    @Test
    void contextLoads() {
        // This test will pass if the Spring context loads successfully
        // It's a basic smoke test to ensure the application can start
    }
}
