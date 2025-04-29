package com.sivalabs.ft.features.domain.release;

public record CreateReleaseCommand(String productCode, String code, String description, String createdBy) {}
