package com.example.message_store.dto;

import com.example.message_store.model.Message;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record MessageResponseWithId(
        @NotBlank(message = "Content must not be empty")
        String content,
        @NotBlank(message = "UUID must not be empty")
        UUID uuid
) {
    public static MessageResponseWithId from(Message message) {
        return new MessageResponseWithId(message.getContent(), message.getUuid());
    }
}

