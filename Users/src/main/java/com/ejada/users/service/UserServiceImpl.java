package com.ejada.users.service;

import com.ejada.users.dto.LoginRequest;
import com.ejada.users.dto.RegisterRequest;
import com.ejada.users.dto.UserResponse;
import com.ejada.users.exception.DuplicateUserException;
import com.ejada.users.exception.UnauthorizedException;
import com.ejada.users.exception.UserNotFoundException;
import com.ejada.users.model.UserModel;
import com.ejada.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public UserResponse register(RegisterRequest registerRequest) {
        if(userRepository.existsByUsernameOrEmail(registerRequest.getUsername(),registerRequest.getEmail())){
            throw new DuplicateUserException("Username or email already exists.");
        }
        UserModel user=new UserModel();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setUsername(registerRequest.getUsername());
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        userRepository.save(user);
        return new UserResponse(user.getUserId(),user.getUsername(),user.getEmail(), user.getFirstName(), user.getLastName());
    }

    @Override
    public UserResponse login(LoginRequest loginRequest) {
        if(userRepository.existsByUsernameOrEmail(loginRequest.getUsername(),"")){
            UserModel user=userRepository.findByUsername(loginRequest.getUsername());
            if(!passwordEncoder.matches(loginRequest.getPassword(),user.getPassword()))
            {
                throw new UnauthorizedException("Invalid username or password.");
            }
            return new UserResponse(user.getUserId(),user.getUsername(),user.getEmail(), user.getFirstName(), user.getLastName());
        }
        throw new UnauthorizedException("Invalid username or password.");
    }

    @Override
    public UserResponse getProfile(String userId) {
        if(userRepository.findByUserId(UUID.fromString(userId))==null)
            throw new UserNotFoundException("User with ID "+userId+" not found.");
        UserModel user= userRepository.findByUserId(UUID.fromString(userId));
        return new UserResponse(user.getUserId(),user.getUsername(),user.getEmail(), user.getFirstName(), user.getLastName());
    }
}
