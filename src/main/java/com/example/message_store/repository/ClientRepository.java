package com.example.message_store.repository;

import com.example.message_store.model.Client;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClientRepository extends CrudRepository<Client, UUID> {
    Client findByUsername(String username);
}
