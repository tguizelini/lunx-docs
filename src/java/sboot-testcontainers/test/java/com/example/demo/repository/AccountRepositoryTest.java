package com.example.demo.repository;

import com.example.demo.entity.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class AccountRepositoryTest {

    @Autowired
    AccountRepository repository;

    @Test
    void shouldSaveAndFind() {
        Account acc = repository.save(new Account("Joana"));
        assertTrue(repository.findById(acc.getId()).isPresent());
    }
}
