package com.example.demo.controller;

import com.example.demo.entity.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AccountControllerIntegrationTest {

    @Autowired
    TestRestTemplate rest;

    @Test
    void shouldCreateAndRetrieveAccount() {
        ResponseEntity<Account> createResp = rest.postForEntity("/accounts", "Bruna", Account.class);
        assertEquals(200, createResp.getStatusCodeValue());

        Long id = createResp.getBody().getId();
        String name = rest.getForObject("/accounts/" + id, String.class);
        assertEquals("Bruna", name);
    }
}
