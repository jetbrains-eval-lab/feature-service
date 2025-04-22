package com.sivalabs.ft.features;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.sivalabs.ft.features.integration.EventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@Import(DatabaseConfiguration.class)
@Sql(scripts = {"/test-data.sql"})
@TestPropertySource("classpath:application-test.properties")
public abstract class AbstractIT {

    @MockitoBean
    EventPublisher eventPublisher;

    @Autowired
    protected MockMvcTester mvc;
}
