package com.sivalabs.ft.features.domain.product;

public record UpdateProductCommand(String code, String name, String description, String imageUrl, String updatedBy) {}
