package com.example.message_store.config.security;

import com.example.message_store.exceptions.MessageNotFoundException;
import com.example.message_store.repository.MessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class MessageSecurity {
    private final MessageRepository messageRepository;

    public boolean isAuthorizedToManageMessage(UUID messageId, Authentication authentication) {
        var message = messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException(
                        "Message with id " + messageId + " not found"));

        return message.getClient().getUsername().equals(authentication.getName());
    }
}
