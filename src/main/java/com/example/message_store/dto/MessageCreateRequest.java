package com.example.message_store.dto;

import jakarta.validation.constraints.NotBlank;

public record MessageCreateRequest(
        @NotBlank(message = "Content must not be empty")
        String content
){}