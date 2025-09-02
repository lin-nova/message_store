package com.example.message_store.controller;

import com.example.message_store.config.security.MessageSecurity;
import com.example.message_store.config.security.SecurityConfig;
import com.example.message_store.dto.MessageCreateRequest;
import com.example.message_store.model.Client;
import com.example.message_store.model.Message;
import com.example.message_store.service.ClientService;
import com.example.message_store.service.MessageService;
import com.example.message_store.util.JwtUtil;
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
import static org.mockito.Mockito.verify;
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

    @MockitoBean(name = "messageSecurity")
    private MessageSecurity messageSecurity;

    @MockitoBean
    private JwtUtil jwtUtil;

    private static final String CLIENT_JWT_TOKEN = "generate_token_for_client_1";

    @Test
    @WithMockUser(username = "client1")
    void getById_returnsMessage_whenMessageExists() throws Exception {
        UUID messageId = UUID.randomUUID();

        Message message = Message.builder().uuid(messageId).content("Message 1").build();

        Mockito.when(messageService.getById(messageId)).thenReturn(message);

        mockMvc.perform(get("/api/v1/messages/{id}", messageId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(("Message 1")));
    }

    @Test
    @WithMockUser(username = "client1")
    void getAll_returnsMessagesPage_whenMessagesExist() throws Exception {
        Message msg = Message.builder().uuid(UUID.randomUUID()).content("Message 1").build();

        Page<Message> page = new PageImpl<>(List.of(msg));
        Mockito.when(jwtUtil.extractUsername(CLIENT_JWT_TOKEN)).thenReturn("client1");
        Mockito.when(messageService.getAll(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/messages/").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("Message 1"));
    }

    @Test
    void createMessage_returnsCreated_whenValidRequest() throws Exception {
        Client client = Client.builder().uuid(UUID.randomUUID()).username("client1").build();
        Message msg = Message.builder().uuid(UUID.randomUUID()).content("Message for client1").build();

        Mockito.when(jwtUtil.extractUsername(CLIENT_JWT_TOKEN)).thenReturn("client1");
        Mockito.when(clientService.getClientByUsername("client1")).thenReturn(client);
        Mockito.when(messageService.save(any(MessageCreateRequest.class), eq(client))).thenReturn(msg);

        String json = "{\"content\": \"new message\"}";

        mockMvc.perform(post("/api/v1/messages/")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + CLIENT_JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.content").value("Message for client1"));
    }

    @Test
    void updateMessage_returnsOk_whenAuthorized() throws Exception {
        UUID message_id = UUID.randomUUID();

        Message updatedMessage = Message.builder().uuid(UUID.randomUUID()).content("Updated message").build();

        Mockito.when(jwtUtil.extractUsername(CLIENT_JWT_TOKEN)).thenReturn("client1");
        Mockito.when(messageSecurity.isAuthorizedToManageMessage(eq(message_id), any())).thenReturn(true);
        Mockito.when(messageService.update(eq(message_id), any(MessageCreateRequest.class))).thenReturn(updatedMessage);

        String json = "{\"content\": \"Updated message\"}";

        mockMvc.perform(put("/api/v1/messages/{id}", message_id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + CLIENT_JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated message"));
    }

    @Test
    void updateMessage_returnsForbidden_whenNotAuthorized() throws Exception {
        UUID message_id = UUID.randomUUID();

        Mockito.when(jwtUtil.extractUsername(CLIENT_JWT_TOKEN)).thenReturn("client2");
        Mockito.when(messageSecurity.isAuthorizedToManageMessage(eq(message_id), any())).thenReturn(false);

        String json = "{\"content\": \"Updated message\"}";

        mockMvc.perform(put("/api/v1/messages/{id}", message_id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + CLIENT_JWT_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteMessage_returnsNoContent_whenAuthorized() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(jwtUtil.extractUsername(CLIENT_JWT_TOKEN)).thenReturn("client1");
        Mockito.when(messageSecurity.isAuthorizedToManageMessage(eq(id), any())).thenReturn(true);

        mockMvc.perform(delete("/api/v1/messages/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + CLIENT_JWT_TOKEN))
                .andExpect(status().isNoContent());

        verify(messageService).deleteById(id);
    }

    @Test
    void deleteMessage_returnsForbidden_whenNotAuthorized() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(jwtUtil.extractUsername(CLIENT_JWT_TOKEN)).thenReturn("client1");
        Mockito.when(messageSecurity.isAuthorizedToManageMessage(eq(id), any())).thenReturn(false);

        mockMvc.perform(delete("/api/v1/messages/{id}", id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + CLIENT_JWT_TOKEN))
                .andExpect(status().isForbidden());
    }
}
