package com.ejada.users.controller;

import com.ejada.users.dto.LoginRequest;
import com.ejada.users.dto.RegisterRequest;
import com.ejada.users.dto.UserResponse;
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
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest registerRequest) {
        kafkaProducerService.sendMessage(Map.of("request", registerRequest), "Request");
        UserResponse user = userService.register(registerRequest);
        Map<String, Object> response = Map.of(
                "userId", user.getUserId(),
                "username", user.getUsername(),
                "message", "User registered successfully.");
        kafkaProducerService.sendMessage(response, "Response");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        kafkaProducerService.sendMessage(Map.of("request", loginRequest), "Request");
        UserResponse user = userService.login(loginRequest);
        Map<String, Object> response = Map.of(
                "userId", user.getUserId(),
                "username", user.getUsername(),
                "message", "Login successful.");
        kafkaProducerService.sendMessage(response, "Response");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@PathVariable String userId) {
        kafkaProducerService.sendMessage(null, "Request");
        UserResponse user = userService.getProfile(userId);
        Map<String, Object> response = Map.of(
                "userId", user.getUserId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName());
        kafkaProducerService.sendMessage(response, "Response");
        return ResponseEntity.ok(response);
    }
}
