package com.ejada.accounts.dto;

import com.ejada.accounts.Models.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {
    @NotNull
    private UUID userId;
    @NotNull
    private AccountType accountType;
    private Double initialBalance = 0.00;
}
