package com.sivalabs.ft.features.integration;

import com.sivalabs.ft.features.domain.Feature;
import com.sivalabs.ft.features.domain.FeatureStatus;
import java.time.Instant;

record FeatureUpdatedEvent(
        Long id,
        String code,
        String title,
        String description,
        FeatureStatus status,
        String assignedTo,
        String createdBy,
        Instant createdAt,
        String updatedBy,
        Instant updatedAt) {

    public FeatureUpdatedEvent(Feature feature) {
        this(
                feature.getId(),
                feature.getCode(),
                feature.getTitle(),
                feature.getDescription(),
                feature.getStatus(),
                feature.getAssignedTo(),
                feature.getCreatedBy(),
                feature.getCreatedAt(),
                feature.getUpdatedBy(),
                feature.getUpdatedAt());
    }
}
