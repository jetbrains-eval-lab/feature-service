package com.sivalabs.ft.features.api.dtos;

import java.io.Serializable;
import java.time.Instant;

public record FeatureDto(
        Long id,
        String code,
        String title,
        ProductDto product,
        String description,
        String status,
        String assignedTo,
        String createdBy,
        Instant createdAt,
        String updatedBy,
        Instant updatedAt)
        implements Serializable {}
