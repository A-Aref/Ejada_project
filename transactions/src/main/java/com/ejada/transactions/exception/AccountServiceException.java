package com.ejada.transactions.exception;

public class AccountServiceException extends RuntimeException {
    
    public AccountServiceException(String message) {
        super(message);
    }
    
    public AccountServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
