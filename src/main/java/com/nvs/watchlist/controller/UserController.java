package com.nvs.watchlist.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.nvs.watchlist.dto.request.UserRequest;
import com.nvs.watchlist.dto.response.UserResponse;
import com.nvs.watchlist.entity.User;
import com.nvs.watchlist.service.UserService;
import com.nvs.watchlist.utils.JwtUtil;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public UserResponse createUser(@RequestBody UserRequest request) {

        User user = service.save(request);
        return service.toResponse(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return service.getAll();
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        User authenticatedUser = service.login(user.getEmail(), user.getPassword());
        return JwtUtil.generateToken(authenticatedUser.getEmail(), authenticatedUser.getRole());
    }

}
