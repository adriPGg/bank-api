package com.adrian.bankapi.controller;

import com.adrian.bankapi.dto.UserRequest;
import com.adrian.bankapi.dto.UserResponse;
import com.adrian.bankapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")

public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping
    public UserResponse createUser(@Valid @RequestBody UserRequest request) {
        return userService.createUser(request);
    }
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return userService.updateUser(id, request);
    }
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }
    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}

