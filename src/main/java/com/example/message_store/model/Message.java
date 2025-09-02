package com.example.message_store.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Messages")

public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;
    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    public Message(String content, Client client) {
        this.content = content;
        this.client = client;
    }
}
