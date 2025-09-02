package com.example.message_store.repository;

import com.example.message_store.model.Message;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    @Override
    @EntityGraph(attributePaths = {"client"})
    List<Message> findAll();
}
