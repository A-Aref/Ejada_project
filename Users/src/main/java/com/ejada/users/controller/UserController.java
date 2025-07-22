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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
    @Operation(summary = "Register a new user", 
               description = "Creates a new user account with provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", 
                    description = "User registered successfully",
                    content = @Content(mediaType = "application/json",
                               examples = @ExampleObject(value = """
                                   {
                                       "userId": "123e4567-e89b-12d3-a456-426614174000",
                                       "username": "john_doe",
                                       "message": "User registered successfully."
                                   }
                                   """))),
        @ApiResponse(responseCode = "409", 
                    description = "User already exists",
                    content = @Content(mediaType = "application/json",
                               examples = @ExampleObject(value = """
                                   {
                                       "status": 409,
                                       "error": "Conflict",
                                       "message": "Username or email already exists"
                                   }
                                   """)))
    })
    public ResponseEntity<?> register(
            @Parameter(description = "User registration data", required = true)
            @RequestBody RegisterRequest registerRequest) {
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
    @Operation(summary = "User login", 
               description = "Authenticates a user with username/email and password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Login successful",
                    content = @Content(mediaType = "application/json",
                               examples = @ExampleObject(value = """
                                   {
                                       "userId": "123e4567-e89b-12d3-a456-426614174000",
                                       "username": "john_doe",
                                       "message": "Login successful."
                                   }
                                   """))),
        @ApiResponse(responseCode = "401", 
                    description = "Invalid credentials",
                    content = @Content(mediaType = "application/json",
                               examples = @ExampleObject(value = """
                                   {
                                       "status": 401,
                                       "error": "Unauthorized",
                                       "message": "Invalid credentials"
                                   }
                                   """)))
    })
    public ResponseEntity<?> login(
            @Parameter(description = "User login credentials", required = true)
            @RequestBody LoginRequest loginRequest) {
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
    @Operation(summary = "Get user profile", 
               description = "Retrieves user profile information by user ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Profile retrieved successfully",
                    content = @Content(mediaType = "application/json",
                               examples = @ExampleObject(value = """
                                   {
                                       "userId": "123e4567-e89b-12d3-a456-426614174000",
                                       "username": "john_doe",
                                       "email": "john@example.com",
                                       "firstName": "John",
                                       "lastName": "Doe"
                                   }
                                   """))),
        @ApiResponse(responseCode = "404", 
                    description = "User not found",
                    content = @Content(mediaType = "application/json",
                               examples = @ExampleObject(value = """
                                   {
                                       "status": 404,
                                       "error": "Not Found",
                                       "message": "User not found"
                                   }
                                   """)))
    })
    public ResponseEntity<?> getProfile(
            @Parameter(description = "User ID to retrieve profile for", required = true)
            @PathVariable String userId) {
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
