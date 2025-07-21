package com.ejada.users.controller;

import com.ejada.users.dto.LoginRequest;
import com.ejada.users.dto.RegisterRequest;
import com.ejada.users.dto.Response;
import com.ejada.users.exception.DuplicateUserException;
import com.ejada.users.exception.UserNotFoundException;
import com.ejada.users.model.UserModel;
import com.ejada.users.service.KafkaProducerService;
import com.ejada.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        kafkaProducerService.sendMessage(Map.of("request", registerRequest), "Request");
        try {
            UserModel user = userService.register(registerRequest);
            Map<String, Object> response = Map.of(
                    "userId", user.getUserId(),
                    "username", user.getUsername(),
                    "message", "User registered successfully.");
            kafkaProducerService.sendMessage(response, "Response");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (DuplicateUserException e) {
            Map<String, Object> errorResponse = Map.of(
                    "status", 409,
                    "error", "Conflict",
                    "message", e.getMessage());
            kafkaProducerService.sendMessage(errorResponse, "Response");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        kafkaProducerService.sendMessage(Map.of("request", loginRequest), "Request");
        try {
            UserModel user = userService.login(loginRequest);
            Map<String, Object> response = Map.of(
                    "userId", user.getUserId(),
                    "username", user.getUsername(),
                    "message", "Login successful.");
            kafkaProducerService.sendMessage(response, "Response");
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            Map<String, Object> errorResponse = Map.of(
                    "status", 401,
                    "error", "Unauthorized",
                    "message", e.getMessage());
            kafkaProducerService.sendMessage(errorResponse, "Response");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<?> getProfile(@PathVariable String userId) {
        // TODO: Add body to the request
        kafkaProducerService.sendMessage(null, "Request");
        try {
            UserModel user = userService.getProfile(userId);
            Map<String, Object> response = Map.of(
                    "userId", user.getUserId(),
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "firstName", user.getFirstName(),
                    "lastName", user.getLastName());
            kafkaProducerService.sendMessage(response, "Response");
            return ResponseEntity.ok(new Response(user.getUserId(), user.getUsername(), user.getEmail(),
                    user.getFirstName(), user.getLastName()));
        } catch (UserNotFoundException e) {
            Map<String, Object> errorResponse = Map.of(
                    "status", 404,
                    "error", "Not Found",
                    "message", e.getMessage());
            kafkaProducerService.sendMessage(errorResponse, "Response");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
}
