package com.example.message_store.service;

import com.example.message_store.dto.MessageCreateRequest;
import com.example.message_store.exceptions.MessageNotFoundException;
import com.example.message_store.model.Client;
import com.example.message_store.model.Message;
import com.example.message_store.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAll_Ok() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Message> page = new PageImpl<>(List.of(new Message("content", new Client())));
        when(messageRepository.findAll(pageable)).thenReturn(page);

        Page<Message> result = messageService.getAll(pageable);

        assertEquals(1, result.getTotalElements());
        verify(messageRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetById_oK() {
        UUID id = UUID.randomUUID();
        Message message = new Message("content", new Client());
        when(messageRepository.findById(id)).thenReturn(Optional.of(message));

        Message result = messageService.getById(id);

        assertNotNull(result);
        assertEquals("content", result.getContent());
        verify(messageRepository, times(1)).findById(id);
    }

    @Test
    void testGetById_MessageNotFoundException() {
        UUID id = UUID.randomUUID();
        when(messageRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(MessageNotFoundException.class, () -> messageService.getById(id));
        verify(messageRepository, times(1)).findById(id);
    }

    @Test
    void testSave_Ok() {
        MessageCreateRequest request = new MessageCreateRequest("content");
        Client client = new Client();
        Message message = new Message("content", client);
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        Message result = messageService.save(request, client);

        assertNotNull(result);
        assertEquals("content", result.getContent());
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void testUpdate_Ok() {
        UUID id = UUID.randomUUID();
        Message existingMessage = new Message("old content", new Client());
        MessageCreateRequest newMessage = new MessageCreateRequest("new content");
        when(messageRepository.findById(id)).thenReturn(Optional.of(existingMessage));
        when(messageRepository.save(existingMessage)).thenReturn(existingMessage);

        Message result = messageService.update(id, newMessage);

        assertNotNull(result);
        assertEquals("new content", result.getContent());

        verify(messageRepository, times(1)).findById(id);
        verify(messageRepository, times(1)).save(existingMessage);
    }

    @Test
    void testUpdate_MessageNotFoundException() {
        UUID id = UUID.randomUUID();
        MessageCreateRequest newMessage = new MessageCreateRequest("new content");
        when(messageRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(MessageNotFoundException.class, () -> messageService.update(id, newMessage));

        verify(messageRepository, times(1)).findById(id);
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void testDeleteById_Ok() {
        UUID id = UUID.randomUUID();

        messageService.deleteById(id);

        verify(messageRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteById_MessageNotFoundException() {
        UUID id = UUID.randomUUID();

        messageService.deleteById(id);

        verify(messageRepository, times(1)).deleteById(id);
    }
}
