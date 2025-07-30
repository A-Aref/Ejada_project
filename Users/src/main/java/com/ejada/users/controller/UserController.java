package com.ejada.users.controller;

import com.ejada.users.dto.CredentialsResponse;
import com.ejada.users.dto.LoginRequest;
import com.ejada.users.dto.RegisterRequest;
import com.ejada.users.dto.UserResponse;
import com.ejada.users.service.KafkaProducerService;
import com.ejada.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.Map;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "User management API endpoints")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @PostMapping("/register")
    public ResponseEntity<CredentialsResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        kafkaProducerService.sendMessage(Map.of("request", registerRequest), "Request");
        UserResponse user = userService.register(registerRequest);
        CredentialsResponse cr=new CredentialsResponse(user.getUserId(),user.getUsername(),"User registered successfully.");
        kafkaProducerService.sendMessage(cr, "Response");
        return ResponseEntity.status(HttpStatus.CREATED).body(cr);
    }

    @PostMapping("/login")
    public ResponseEntity<CredentialsResponse> login(@RequestBody LoginRequest loginRequest) {
        kafkaProducerService.sendMessage(Map.of("request", loginRequest), "Request");
        UserResponse user = userService.login(loginRequest);
        CredentialsResponse cr=new CredentialsResponse(user.getUserId(),user.getUsername(),"Login successful.");
        kafkaProducerService.sendMessage(cr, "Response");
        return ResponseEntity.ok(cr);
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserResponse> getProfile(@PathVariable String userId) {
        kafkaProducerService.sendMessage(Map.of("userId", userId), "Request");
        UserResponse user = userService.getProfile(userId);
        kafkaProducerService.sendMessage(user, "Response");
        return ResponseEntity.ok(user);
    }
}
