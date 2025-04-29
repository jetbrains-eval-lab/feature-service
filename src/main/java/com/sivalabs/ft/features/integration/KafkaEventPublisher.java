package com.sivalabs.ft.features.integration;

import com.sivalabs.ft.features.ApplicationProperties;
import com.sivalabs.ft.features.domain.feature.Feature;
import java.time.Instant;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "ft.events.publisher", havingValue = "KAFKA")
public class KafkaEventPublisher implements EventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ApplicationProperties properties;

    public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate, ApplicationProperties properties) {
        this.kafkaTemplate = kafkaTemplate;
        this.properties = properties;
    }

    @Override
    public void publishFeatureCreatedEvent(Feature feature) {
        FeatureCreatedEvent event = new FeatureCreatedEvent(feature);
        kafkaTemplate.send(properties.events().newFeatures(), event);
    }

    @Override
    public void publishFeatureUpdatedEvent(Feature feature) {
        FeatureUpdatedEvent event = new FeatureUpdatedEvent(feature);
        kafkaTemplate.send(properties.events().updatedFeatures(), event);
    }

    @Override
    public void publishFeatureDeletedEvent(Feature feature, String deletedBy, Instant deletedAt) {
        FeatureDeletedEvent event = new FeatureDeletedEvent(feature, deletedBy, deletedAt);
        kafkaTemplate.send(properties.events().deletedFeatures(), event);
    }
}
