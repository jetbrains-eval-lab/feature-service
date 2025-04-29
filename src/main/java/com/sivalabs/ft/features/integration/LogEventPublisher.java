package com.sivalabs.ft.features.integration;

import com.sivalabs.ft.features.domain.feature.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@ConditionalOnProperty(name = "ft.events.publisher", havingValue = "DUMB")
public class LogEventPublisher implements EventPublisher {
    private static final Logger log = LoggerFactory.getLogger(LogEventPublisher.class);

    @Override
    public void publishFeatureCreatedEvent(Feature feature) {
        log.info("Created {}", feature.toString());
    }

    @Override
    public void publishFeatureUpdatedEvent(Feature feature) {
        log.info("Updated {}", feature.toString());
    }

    @Override
    public void publishFeatureDeletedEvent(Feature feature, String deletedBy, Instant deletedAt) {
        log.info("Deleted {} by {} at {}", feature.toString(), deletedBy, deletedAt);
    }
}
