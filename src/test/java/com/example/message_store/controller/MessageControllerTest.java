package com.example.message_store.controller;

import com.example.message_store.config.security.MessageSecurity;
import com.example.message_store.config.security.SecurityConfig;
import com.example.message_store.dto.MessageCreateRequest;
import com.example.message_store.model.Client;
import com.example.message_store.model.Message;
import com.example.message_store.service.ClientService;
import com.example.message_store.service.MessageService;
import com.example.message_store.util.JwtUtil;
import org.apache.tomcat.util.http.parser.Authorization;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageController.class)
@ExtendWith(SpringExtension.class)
@Import(SecurityConfig.class)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    @MockitoBean
    private ClientService clientService;

    @MockitoBean
    private MessageSecurity messageSecurity;

    @MockitoBean
    private JwtUtil jwtUtil;

    private static final String CLIENT_JWT_TOKEN = "generate_token_for_client_1";

    @Test
    @WithMockUser(username = "client1")
    void testGetById_Ok() throws Exception {
        UUID id = UUID.randomUUID();
        Message message = new Message();
        message.setUuid(id);
        message.setContent("Hello World");

        Mockito.when(messageService.getById(id)).thenReturn(message);

        mockMvc.perform(get("/api/v1/messages/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Hello World"));
    }

    @Test
    @WithMockUser(username = "client1")
    void testGetAll_Ok() throws Exception {
        Message msg = new Message();
        msg.setUuid(UUID.randomUUID());
        msg.setContent("Message 1");

        Page<Message> page = new PageImpl<>(List.of(msg));
        Mockito.when(jwtUtil.extractUsername(CLIENT_JWT_TOKEN)).thenReturn("client1");
        Mockito.when(messageService.getAll(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/messages").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("Message 1"));
    }

    @Test
    @WithMockUser(username = "client1")
    void testCreateMessage_Created() throws Exception {
        UUID id = UUID.randomUUID();
        Client client = new Client(id, "client1");

        Message msg = new Message(id, "Message for client1", client);

        Mockito.when(jwtUtil.extractUsername(CLIENT_JWT_TOKEN)).thenReturn("client1");
        Mockito.when(clientService.getClientByUsername("client1")).thenReturn(client);
        Mockito.when(messageService.save(any(MessageCreateRequest.class), eq(client))).thenReturn(msg);

        String json = "{\"content\": \"new message\"}";

        mockMvc.perform(post("/api/v1/messages")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + CLIENT_JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.content").value("Message for client1"));
    }

    @Test
    @WithMockUser(username = "client1")
    void testUpdateMessage() throws Exception {
        UUID message_id = UUID.randomUUID();

        Client client = new Client(message_id, "client1");
        Message updated = new Message(message_id, "Updated message", client);

        Mockito.when(jwtUtil.extractUsername(CLIENT_JWT_TOKEN)).thenReturn("client1");
        Mockito.when(messageSecurity.isAuthorizedToManageMessage(eq(message_id), any())).thenReturn(true);
        Mockito.when(messageService.update(eq(message_id), any(MessageCreateRequest.class))).thenReturn(updated);

        String json = "{\"content\": \"Updated message\"}";

        mockMvc.perform(put("/api/v1/messages/{id}", message_id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + CLIENT_JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated message"));
    }

    @Test
    @WithMockUser(username = "client1")
    void testDeleteMessage() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(jwtUtil.extractUsername(CLIENT_JWT_TOKEN)).thenReturn("client1");
        Mockito.when(messageSecurity.isAuthorizedToManageMessage(eq(id), any())).thenReturn(true);

        mockMvc.perform(delete("/api/v1/messages/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + CLIENT_JWT_TOKEN)
                        .principal(Authorization))
                .andExpect(status().isNoContent());

        Mockito.verify(messageService).deleteById(id);
    }
}
