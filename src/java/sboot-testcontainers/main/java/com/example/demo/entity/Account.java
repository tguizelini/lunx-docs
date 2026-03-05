package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public Account() {}
    public Account(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
}
