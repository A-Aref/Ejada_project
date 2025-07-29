package com.ejada.transactions.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    private String message;
    private String error;
    private int statusCode;
    private long timestamp;
    
    public ErrorResponse(String message) {
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
    
    public ErrorResponse(String message, String error, int statusCode) {
        this.message = message;
        this.error = error;
        this.statusCode = statusCode;
        this.timestamp = System.currentTimeMillis();
    }
}
