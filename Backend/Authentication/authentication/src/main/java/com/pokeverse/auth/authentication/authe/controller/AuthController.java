package com.pokeverse.auth.authentication.authe.controller;


import com.pokeverse.auth.authentication.authe.model.User;
import com.pokeverse.auth.authentication.authe.repository.UserRepository;
import com.pokeverse.auth.authentication.authe.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> login() {
        return ResponseEntity.ok(Map.of("message", "Redirect to /oauth2/authorization/google to login"));
    }

    @GetMapping("/success")
    public ResponseEntity<?> loginSuccess(@AuthenticationPrincipal OAuth2User principal) {
        System.out.println("Principle: " + principal);

        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
        }

        String email = principal.getAttribute("email");
        String name = principal.getAttribute("name");
        String profilePicUrl = principal.getAttribute("picture");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .name(name)
                            .profilePicUrl(profilePicUrl)
                            .build();
                    return userRepository.save(newUser);
                });
        String token = jwtUtil.generateToken(user.getEmail());
        return ResponseEntity.ok(Map.of(
                "user", user,
                "token", token
        ));
    }



    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        return ResponseEntity.ok(Map.of(
                "id", Objects.requireNonNull(principal.getAttribute("sub")),
                "email", Objects.requireNonNull(principal.getAttribute("email")),
                "name", Objects.requireNonNull(principal.getAttribute("name")),
                "profilePicUrl", Objects.requireNonNull(principal.getAttribute("picture"))
        ));
    }
}
