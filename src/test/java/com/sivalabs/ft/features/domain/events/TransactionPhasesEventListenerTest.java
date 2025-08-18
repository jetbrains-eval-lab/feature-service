package com.sivalabs.ft.features.domain.events;

import com.sivalabs.ft.features.ApplicationProperties;
import com.sivalabs.ft.features.TestcontainersConfiguration;
import com.sivalabs.ft.features.domain.Commands;
import com.sivalabs.ft.features.domain.FeatureService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import({TestcontainersConfiguration.class, TransactionPhasesEventListenerTest.TestConfig.class})
@Sql(scripts = {"/test-data.sql"})
class TransactionPhasesEventListenerTest {

    private static final Logger logger = LoggerFactory.getLogger(TransactionPhasesEventListenerTest.class);

    @Autowired
    private FeatureService featureService;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ApplicationProperties applicationProperties;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public KafkaTemplate<String, Object> kafkaTemplate() {
            return Mockito.mock(KafkaTemplate.class);
        }
    }

    @Test
    void testEventHandlingTransactionCommit() {
        Mockito.reset(kafkaTemplate);

        // Given a valid create feature command
        Commands.CreateFeatureCommand command = new Commands.CreateFeatureCommand(
                "intellij", // product code from test data
                "R-2023-Q4", // release code from test data
                "Test Feature",
                "Test Description",
                "assignee",
                "creator"
        );

        // When creating a feature (which should succeed)
        featureService.createFeature(command);

        // Verify that KafkaTemplate was invoked with the correct event and topic
        ArgumentCaptor<FeatureCreatedEvent> eventCaptor = ArgumentCaptor.forClass(FeatureCreatedEvent.class);
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(kafkaTemplate).send(topicCaptor.capture(), eventCaptor.capture());

        // Verify the topic
        assertThat(topicCaptor.getValue()).isEqualTo(applicationProperties.events().newFeatures());

        // Verify the event
        FeatureCreatedEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent).isNotNull();
        assertThat(capturedEvent.code()).isNotNull();
        assertThat(capturedEvent.title()).isEqualTo("Test Feature");
        assertThat(capturedEvent.description()).isEqualTo("Test Description");
        assertThat(capturedEvent.assignedTo()).isEqualTo("assignee");
        assertThat(capturedEvent.createdBy()).isEqualTo("creator");
    }

    @Test
    void testEventHandlingTransactionRollback() {
        Mockito.reset(kafkaTemplate);

        Commands.CreateFeatureCommand command = new Commands.CreateFeatureCommand(
                "unknown",
                "unknown", // release code from test data
                "Test Feature",
                "Test Description",
                "assignee",
                "creator"
        );

        try {
            featureService.createFeature(command);
        } catch (Exception e) {
        }

        // Verify that KafkaTemplate was not invoked (since the transaction was rolled back)
        Mockito.verifyNoInteractions(kafkaTemplate);
    }
}