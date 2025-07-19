package com.ejada.users.service;

import com.ejada.users.dto.LoginRequest;
import com.ejada.users.dto.RegisterRequest;
import com.ejada.users.model.UserModel;

public interface UserService {
    public UserModel register(RegisterRequest registerRequest);
    public UserModel login(LoginRequest loginRequest);
    public UserModel getProfile(String userId);
}
