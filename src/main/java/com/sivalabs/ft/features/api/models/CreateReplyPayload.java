package com.sivalabs.ft.features.api.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record CreateReplyPayload(
        @NotEmpty(message = "Text is required") 
        @Size(max = 5000, message = "Text cannot exceed 5000 characters") 
        String text) {
}