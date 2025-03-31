package com.pokeverse.auth.authentication.authe.repository;

import com.pokeverse.auth.authentication.authe.model.RToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RTokenRepository extends JpaRepository<RToken,Long> {
    Optional<RToken> findByToken(String token);
}
