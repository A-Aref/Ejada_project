package com.ejada.users.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "userId")
    @NotNull
    private UUID userId;
    @Column(name="username",unique = true)
    @NotNull
    private String username;
    @Column(name="email",unique = true)
    @NotNull
    private String email;
    @Column(name="password")
    @NotNull
    private String password;
    @Column(name="firstName")
    @NotNull
    private String firstName;
    @Column(name="lastName")
    @NotNull
    private String lastName;
}
