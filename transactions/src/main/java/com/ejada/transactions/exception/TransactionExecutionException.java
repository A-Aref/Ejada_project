package com.ejada.transactions.exception;

public class TransactionExecutionException extends RuntimeException {
    
    public TransactionExecutionException(String message) {
        super(message);
    }
    
    public TransactionExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
