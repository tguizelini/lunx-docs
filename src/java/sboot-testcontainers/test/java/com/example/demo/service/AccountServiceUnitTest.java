package com.example.demo.service;

import com.example.demo.entity.Account;
import com.example.demo.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceUnitTest {

    @Mock
    AccountRepository repository;

    @InjectMocks
    AccountService service;

    @Test
    void shouldReturnNameIfFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(new Account("Tiago")));
        String result = service.getAccountName(1L);
        assertEquals("Tiago", result);
    }

    @Test
    void shouldThrowIfNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.getAccountName(99L));
    }
}
