package com.example.message_store.service;

import com.example.message_store.model.Client;
import com.example.message_store.repository.ClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    public Client getClientByUsername(String username) {
        return clientRepository.findByUsername(username);
    }
}
