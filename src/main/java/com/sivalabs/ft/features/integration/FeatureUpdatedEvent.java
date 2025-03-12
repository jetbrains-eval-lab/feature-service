package com.sivalabs.ft.features.integration;

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
        Instant updatedAt) {}
