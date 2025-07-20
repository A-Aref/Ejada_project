package com.ejada.users.controller;

import com.ejada.users.dto.LoginRequest;
import com.ejada.users.dto.RegisterRequest;
import com.ejada.users.dto.Response;
import com.ejada.users.exception.DuplicateUserException;
import com.ejada.users.exception.UserNotFoundException;
import com.ejada.users.model.UserModel;
import com.ejada.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest){
        try{
            UserModel user=userService.register(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("userId",user.getUserId(),"username",user.getUsername(),"message","User registered successfully."));
        }
        catch(DuplicateUserException e){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("status", 409, "error", "Conflict", "message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        try{
            UserModel user=userService.login(loginRequest);
            return ResponseEntity.ok(Map.of("userId",user.getUserId(),"username",user.getUsername()));
        }
        catch(UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status",401,"error","Unauthorized","message",e.getMessage()));
        }
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<?> getProfile(@PathVariable String userId){
        try{
            UserModel user=userService.getProfile(userId);
            return ResponseEntity.ok(new Response(user.getUserId(),user.getUsername(),user.getEmail(),user.getFirstName(),user.getLastName()));
        }
        catch(UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status",404,"error","Not Found","message",e.getMessage()));
        }
    }
}
