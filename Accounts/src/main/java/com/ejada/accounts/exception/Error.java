package com.ejada.accounts.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Error {
    private LocalDateTime timestamp;
    private String status;
    private String error;
    private String message;
    private Map<String, String> fieldErrors;
    
    // Constructor without fieldErrors for simple errors
    public Error(LocalDateTime timestamp, String status, String error, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
    }
}
