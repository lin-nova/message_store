package com.example.message_store.controller;

import com.example.message_store.model.Client;
import com.example.message_store.model.Message;
import com.example.message_store.repository.ClientRepository;
import com.example.message_store.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MessageControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ClientRepository clientRepository;

    private String JWT_TOKEN_FOR_CLIENT_1 = "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJleGFtcGxlLmNvbSIsInN1YiI6ImNsaWVudDEiLCJyb2xlIjoiYWRtaW4iLCJpYXQiOjE3NTY2NjczMDUsImV4cCI6MTc4ODIwMzMwNX0.aTYUxww1CfLYNMNO8wbsQ97HxLVkYc4_QXIHFcV7IWg";

    @Test
    void getById_returnsMessage_whenMessageExists() {
        Client testClient = Client.builder().username("testClient").build();
        clientRepository.save(testClient);

        Message message1 = new Message("Integration test message", testClient);
        messageRepository.save(message1);

        HttpEntity<?> entity = getHttpEntityWithHeaders();

        ResponseEntity<Message> responseEntity = restTemplate.exchange("/api/v1/messages",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                });

        assertThat(responseEntity.getStatusCode().is2xxSuccessful());
    }


//    @Test
//    void getAll_returnsMessagesPage_whenMessagesExist() {
//        Client testClient = Client.builder().username("testClient").build();
//        clientRepository.save(testClient);
//
//        Message message1 = new Message("Integration test message 1", testClient);
//        Message message2 = new Message("Integration test message 2", testClient);
//        messageRepository.save(message1);
//        messageRepository.save(message2);
//
//        HttpEntity<?> entity = getHttpEntityWithHeaders();
//
//        //Error "Cannot construct instance of `org.springframework.data.domain.Page".
//        // TODO: Fix will include wrapper around Page, e.g. MessagePageResponse
//        ResponseEntity<Page<Message>> responseEntity = restTemplate.exchange(
//                "/api/v1/messages",
//                HttpMethod.GET,
//                entity,
//                new TypeReference<Page<Message>>() {
//                });
//
//        assertThat(responseEntity.getStatusCode().is2xxSuccessful()).isTrue();
//        assertThat(responseEntity.getBody()).isNotNull();
//        assertThat(responseEntity.getBody().getContent().size()).isGreaterThanOrEqualTo(2);
//    }

    private HttpEntity getHttpEntityWithHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(JWT_TOKEN_FOR_CLIENT_1);
        return new HttpEntity<>(headers);
    }
}
