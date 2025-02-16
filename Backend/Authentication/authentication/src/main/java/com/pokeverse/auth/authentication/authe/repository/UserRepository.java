package com.pokeverse.auth.authentication.authe.repository;

import com.pokeverse.auth.authentication.authe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
}
