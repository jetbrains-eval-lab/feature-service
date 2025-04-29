package com.sivalabs.ft.features.integration;

import com.sivalabs.ft.features.domain.feature.Feature;
import java.time.Instant;

public interface EventPublisher {
    void publishFeatureCreatedEvent(Feature feature);

    void publishFeatureUpdatedEvent(Feature feature);

    void publishFeatureDeletedEvent(Feature feature, String deletedBy, Instant deletedAt);
}
