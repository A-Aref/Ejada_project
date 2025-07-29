package com.ejada.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountResponse {
    private UUID accountId;
    private String accountNumber;
    private String message;
}
