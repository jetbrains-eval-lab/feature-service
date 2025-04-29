package com.sivalabs.ft.features.domain.feature;

public record UpdateFeatureCommand(
        String code, String title, String description, FeatureStatus status, String assignedTo, String updatedBy) {}
