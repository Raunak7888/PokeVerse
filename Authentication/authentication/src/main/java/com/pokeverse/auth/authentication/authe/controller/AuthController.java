package com.pokeverse.auth.authentication.authe.controller;


import com.pokeverse.auth.authentication.authe.model.RToken;
import com.pokeverse.auth.authentication.authe.model.User;
import com.pokeverse.auth.authentication.authe.repository.RTokenRepository;
import com.pokeverse.auth.authentication.authe.repository.UserRepository;
import com.pokeverse.auth.authentication.authe.service.UserService;
import com.pokeverse.auth.authentication.authe.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    private final UserService userService;

    @Autowired
    private final RTokenRepository rTokenRepository;

    @GetMapping("/test")
    public String test() {
        return "Hello from Authentication Service!";
    }

    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> login() {
        return ResponseEntity.ok(Map.of("message", "Redirect to /oauth2/authorization/google to login"));
    }

    @GetMapping("/success")
    public ResponseEntity<?> loginSuccess(@AuthenticationPrincipal OAuth2User principal) {

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
        String accesstoken = jwtUtil.generateToken(user.getEmail(), user.getId());
        String refreshToken = userService.refreshToken();
        RToken rToken = RToken.builder()
                .token(refreshToken)
                .expirationTime(LocalDateTime.now().plusDays(2))
                .userId(user)
                .build();
        rTokenRepository.save(rToken);
        return ResponseEntity.ok(Map.of(
                "user", user,
                "access token", accesstoken,
                "refresh token", refreshToken
        ));
    }



    @GetMapping("/me")
    public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
        // Extract JWT from Authorization Header
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("error", "Missing or invalid Authorization header"));
        }
        // Split & extract token
        String token = authorizationHeader.substring(7); // Remove "Bearer "
        try {
            // Decode JWT
            String email = jwtUtil.extractEmail(token);
            if (email == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Email not found in token"));
            }
            // Find user in database
            Optional<User> userOpt = userRepository.findByEmail(email);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }
            User user = userOpt.get();
            return ResponseEntity.ok(Map.of(
                    "userId", user.getId(),
                    "email", user.getEmail(),
                    "name", user.getName(),
                    "profile",user.getProfilePicUrl()// If roles exist
            ));

        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(401).body(Map.of("error", "JWT token expired"));
        } catch (JwtException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid JWT token"));
        }
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        Optional<RToken> rToken = rTokenRepository.findByToken(refreshToken);
        if (rToken.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Refresh token not found"));
        }
        RToken rToken1 = rToken.get();
        if (rToken1.getExpirationTime().isBefore(LocalDateTime.now())) {
            rTokenRepository.delete(rToken1);  // Delete expired token from database  (optional)
            return ResponseEntity.status(401).body(Map.of("error", "Refresh token expired"));
        }
        String accesstoken = jwtUtil.generateToken(rToken1.getUserId().getEmail(), rToken1.getUserId().getId());
        String newRefreshToken = userService.refreshToken();
        LocalDateTime newRefreshTokenExpirationTime = LocalDateTime.now().plusDays(2);
        RToken newRToken = rToken.get();
        newRToken.setToken(newRefreshToken);
        newRToken.setExpirationTime(newRefreshTokenExpirationTime);
        rTokenRepository.save(newRToken);
        return ResponseEntity.ok(Map.of(
                "access token", accesstoken,
                "refresh token", newRefreshToken
        ));
    }

}
