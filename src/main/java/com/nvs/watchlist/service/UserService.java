package com.nvs.watchlist.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nvs.watchlist.dto.request.UserRequest;
import com.nvs.watchlist.dto.response.UserResponse;
import com.nvs.watchlist.entity.User;
import com.nvs.watchlist.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserService {

    private final UserRepository repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public User save(UserRequest request) {
        User user = new User();
        user.setUsername(request.username);
        user.setEmail(request.email);
        user.setPassword(encoder.encode(request.password));
        user.setRole("USER"); // Default role
        return repo.save(user);
    }

    public List<User> getAll() {
        return repo.findAll();
    }

    public User login(String email, String password) {
        User user = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        return user;
    }

    public UserResponse toResponse(User user) {
        UserResponse res = new UserResponse();
        res.id = user.getId();
        res.username = user.getUsername();
        res.email = user.getEmail();
        return res;
    }
}