package com.sivalabs.ft.features.integration;

import com.sivalabs.ft.features.domain.feature.Feature;
import com.sivalabs.ft.features.domain.feature.FeatureStatus;
import java.time.Instant;

record FeatureCreatedEvent(
        Long id,
        String code,
        String title,
        String description,
        FeatureStatus status,
        String assignedTo,
        String createdBy,
        Instant createdAt) {

    public FeatureCreatedEvent(Feature feature) {
        this(
                feature.getId(),
                feature.getCode(),
                feature.getTitle(),
                feature.getDescription(),
                feature.getStatus(),
                feature.getAssignedTo(),
                feature.getCreatedBy(),
                feature.getCreatedAt());
    }
}
