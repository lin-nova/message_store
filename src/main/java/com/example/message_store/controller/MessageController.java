package com.example.message_store.controller;


import com.example.message_store.dto.MessageCreateRequest;
import com.example.message_store.dto.MessageResponse;
import com.example.message_store.dto.MessageResponseWithId;
import com.example.message_store.model.Client;
import com.example.message_store.model.Message;
import com.example.message_store.service.ClientService;
import com.example.message_store.service.MessageService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/messages")
@AllArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final ClientService clientService;

    @GetMapping("/{id}")
    public ResponseEntity<MessageResponse> getById(@PathVariable UUID id, Authentication auth) {
        Message message = messageService.getById(id);
        return ResponseEntity.ok(MessageResponse.from(message));
    }

    @GetMapping
    public ResponseEntity<Page<MessageResponse>> getAll(Pageable pageable) {
        Page<Message> messages = messageService.getAll(pageable);
        Page<MessageResponse> response = messages.map(MessageResponse::from);
        return ResponseEntity.ok(response);
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<MessageResponseWithId> createMessage(@RequestBody @Valid MessageCreateRequest messageDTO, Authentication auth) {
        Client client = clientService.getClientByUsername(auth.getName());
        Message createdMessage = messageService.save(messageDTO, client);
        URI location = URI.create("/api/v1/messages/" + createdMessage.getUuid());
        return ResponseEntity.created(location).body(MessageResponseWithId.from(createdMessage));
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    @PreAuthorize("@messageSecurity.isAuthorizedToManageMessage(#id, authentication)")
    public ResponseEntity<MessageResponse> updateMessage(@PathVariable UUID id, @RequestBody @Valid MessageCreateRequest messageDTO, Authentication authentication) {
        Message updatedMessage = messageService.update(id, messageDTO);
        return ResponseEntity.ok(MessageResponse.from(updatedMessage));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@messageSecurity.isAuthorizedToManageMessage(#id, authentication)")
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID id, Authentication authentication) {
        messageService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
