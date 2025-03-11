package com.poke.matrix.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/random")
public class RandomMatrix {
    @GetMapping("/matrix")
    public ResponseEntity<?> getRandomMatrix(){
        return null;
    }
}
