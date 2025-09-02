package com.example.message_store.config.security;

import com.example.message_store.exceptions.MessageNotFoundException;
import com.example.message_store.model.Client;
import com.example.message_store.model.Message;
import com.example.message_store.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


class MessageSecurityTest {
    @Mock
    private MessageRepository messageRepository;
    @InjectMocks
    private MessageSecurity messageSecurity;
    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void isAuthorizedToManageMessage_returnsTrue_whenUserIsOwner() {
        UUID messageId = UUID.randomUUID();
        String username = "user1";
        Client client = Client.builder().username(username).build();
        Message message = Message.builder().uuid(messageId).client(client).build();
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(authentication.getName()).thenReturn(username);

        boolean result = messageSecurity.isAuthorizedToManageMessage(messageId, authentication);
        assertTrue(result);
    }

    @Test
    void isAuthorizedToManageMessage_returnsFalse_whenUserIsNotOwner() {
        UUID messageId = UUID.randomUUID();
        Client client = Client.builder().username("owner").build();
        Message message = Message.builder().uuid(messageId).client(client).build();
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(authentication.getName()).thenReturn("otherUser");

        boolean result = messageSecurity.isAuthorizedToManageMessage(messageId, authentication);
        assertFalse(result);
    }

    @Test
    void isAuthorizedToManageMessage_throwsException_whenMessageNotFound() {
        UUID messageId = UUID.randomUUID();
        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());
        when(authentication.getName()).thenReturn("user1");

        assertThrows(MessageNotFoundException.class, () ->
                messageSecurity.isAuthorizedToManageMessage(messageId, authentication));
    }
}