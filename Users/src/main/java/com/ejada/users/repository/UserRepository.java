package com.ejada.users.repository;

import com.ejada.users.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserModel, UUID> {
    UserModel findByUserId(UUID userId);
    UserModel findByUsername(String username);
    boolean existsByUsernameOrEmail(String username, String email);
}
