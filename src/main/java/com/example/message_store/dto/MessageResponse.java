package com.example.message_store.dto;

import com.example.message_store.model.Message;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;


public record MessageResponse(
        @NotBlank(message = "Content must not be empty")
        String content
) {
    public static MessageResponse from(Message message) {
        Objects.requireNonNull(message, "Message must not be null");
        return new MessageResponse(message.getContent());
    }
}