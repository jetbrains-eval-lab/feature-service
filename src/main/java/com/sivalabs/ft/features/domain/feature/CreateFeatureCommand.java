package com.sivalabs.ft.features.domain.feature;

public record CreateFeatureCommand(
        String productCode,
        String releaseCode,
        String code,
        String title,
        String description,
        String assignedTo,
        String createdBy) {}
