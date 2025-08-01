package com.ejada.users.service;

import com.ejada.users.dto.LoginRequest;
import com.ejada.users.dto.RegisterRequest;
import com.ejada.users.dto.UserResponse;

public interface UserService {
    public UserResponse register(RegisterRequest registerRequest);
    public UserResponse login(LoginRequest loginRequest);
    public UserResponse getProfile(String userId);
}
