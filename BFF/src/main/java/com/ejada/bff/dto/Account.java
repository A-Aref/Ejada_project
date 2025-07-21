package com.ejada.bff.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private UUID accountId;
    private String accountNumber;
    private String accountType;
    private double balance;
    private String status;
}
