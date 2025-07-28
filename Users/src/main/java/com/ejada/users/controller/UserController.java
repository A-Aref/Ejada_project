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
        UserModel user = userService.register(registerRequest);
        Map<String, Object> response = Map.of(
                "userId", user.getUserId(),
                "username", user.getUsername(),
                "message", "User registered successfully.");
        kafkaProducerService.sendMessage(response, "Response");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        kafkaProducerService.sendMessage(Map.of("request", loginRequest), "Request");
        UserModel user = userService.login(loginRequest);
        Map<String, Object> response = Map.of(
                "userId", user.getUserId(),
                "username", user.getUsername(),
                "message", "Login successful.");
        kafkaProducerService.sendMessage(response, "Response");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<?> getProfile(@PathVariable String userId) {
        kafkaProducerService.sendMessage(null, "Request");
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
    }
}
