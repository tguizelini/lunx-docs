package com.example.demo.service;

import com.example.demo.entity.Account;
import com.example.demo.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    public String getAccountName(Long id) {
        return repository.findById(id)
                .map(Account::getName)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    public Account createAccount(String name) {
        return repository.save(new Account(name));
    }
}
