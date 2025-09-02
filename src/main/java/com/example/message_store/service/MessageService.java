package com.example.message_store.service;

import com.example.message_store.dto.MessageCreateRequest;
import com.example.message_store.exceptions.MessageNotFoundException;
import com.example.message_store.model.Client;
import com.example.message_store.model.Message;
import com.example.message_store.repository.MessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public Page<Message> getAll(Pageable pageable) {
        return messageRepository.findAll(pageable);
    }

    public Message getById(UUID id) {
        return messageRepository.findById(id).orElseThrow(() -> new MessageNotFoundException("Message not found with id: " + id));
    }

    public Message save(MessageCreateRequest messageCreate, Client client) {
        return messageRepository.save(new Message(
                messageCreate.content(),
                client
        ));
    }

    public Message update(UUID id, MessageCreateRequest newMessage) {
        Message existingMessage = messageRepository.findById(id).orElse(null);
        if (existingMessage == null) {
            throw new MessageNotFoundException("Message not found with id: " + id);
        }
        existingMessage.setContent(newMessage.content());
        return messageRepository.save(existingMessage);
    }

    public void deleteById(UUID id) {
        messageRepository.deleteById(id);
    }
}
