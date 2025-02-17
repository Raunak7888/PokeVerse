package com.pokeverse.auth.authentication.authe.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    public String refreshToken() {
        return UUID.randomUUID().toString();
    }
}
