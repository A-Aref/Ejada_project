package com.ejada.bff.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @JsonProperty("transactionId")
    private UUID id;
    @JsonProperty("accountId")
    private UUID toAccountId;
    private Double amount;
    private String description;
    @JsonProperty("timestamp")
    private String createdAt;
}
