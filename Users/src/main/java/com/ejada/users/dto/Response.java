package com.ejada.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private UUID userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
}
