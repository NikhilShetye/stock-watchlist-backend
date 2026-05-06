package com.nvs.watchlist.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nvs.watchlist.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}