package com.ejada.users.service;

import com.ejada.users.config.SecurityConfig;
import com.ejada.users.dto.LoginRequest;
import com.ejada.users.dto.RegisterRequest;
import com.ejada.users.exception.DuplicateUserException;
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
    public UserModel register(RegisterRequest registerRequest) {
        if(userRepository.existsByUsernameOrEmail(registerRequest.getUsername(),registerRequest.getEmail())){
            throw new DuplicateUserException("Username or email already exists.");
        }
        UserModel user=new UserModel();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setUsername(registerRequest.getUsername());
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        return userRepository.save(user);
    }

    @Override
    public UserModel login(LoginRequest loginRequest) {
        if(userRepository.existsByUsernameOrEmail(loginRequest.getUsername(),"")){
            UserModel user=userRepository.findByUsername(loginRequest.getUsername());
            if(!passwordEncoder.matches(loginRequest.getPassword(),user.getPassword()))
            {
                throw new UserNotFoundException("Invalid username or password.");
            }
            return user;
        }
        throw new UserNotFoundException("Invalid username or password.");
    }

    @Override
    public UserModel getProfile(String userId) {
        if(userRepository.findByUserId(UUID.fromString(userId))==null)
            throw new UserNotFoundException("User with ID "+userId+" not found.");
        return userRepository.findByUserId(UUID.fromString(userId));
    }
}
