package com.sivalabs.ft.features;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@Import(DatabaseConfiguration.class)
@TestPropertySource("classpath:application-test.properties")
class FeatureServiceApplicationTests {

    @Test
    void contextLoads() {}
}
