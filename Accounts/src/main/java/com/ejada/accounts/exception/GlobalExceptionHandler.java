package com.ejada.accounts.exception;

import com.ejada.accounts.Services.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @Autowired
    private KafkaProducerService kafkaProducerService;

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Error> handleAccountNotFound(AccountNotFoundException ex) {
        Error error = new Error(
            LocalDateTime.now(),
            String.valueOf(HttpStatus.NOT_FOUND.value()),
            "Account Not Found",
            ex.getMessage()
        );
        kafkaProducerService.sendMessage(error, "Response");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Error> handleInsufficientFunds(InsufficientFundsException ex) {
        Error error = new Error(
            LocalDateTime.now(),
            String.valueOf(HttpStatus.BAD_REQUEST.value()),
            "Insufficient Funds",
            ex.getMessage()
        );
        kafkaProducerService.sendMessage(error, "Response");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidAccountDataException.class)
    public ResponseEntity<Error> handleInvalidAccountData(InvalidAccountDataException ex) {
        Error error = new Error(
            LocalDateTime.now(),
            String.valueOf(HttpStatus.BAD_REQUEST.value()),
            "Invalid Account Data",
            ex.getMessage()
        );
        kafkaProducerService.sendMessage(error, "Response");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidTransferException.class)
    public ResponseEntity<Error> handleInvalidTransfer(InvalidTransferException ex) {
        Error error = new Error(
            LocalDateTime.now(),
            String.valueOf(HttpStatus.BAD_REQUEST.value()),
            "Invalid Transfer",
            ex.getMessage()
        );
        kafkaProducerService.sendMessage(error, "Response");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                    FieldError::getField,
                    FieldError::getDefaultMessage,
                    (existing, replacement) -> existing
                ));
        
        Error error = new Error(
            LocalDateTime.now(),
            String.valueOf(HttpStatus.BAD_REQUEST.value()),
            "Validation Failed",
            "Input validation failed",
            errors
        );
        kafkaProducerService.sendMessage(error, "Response");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleOtherException(Exception ex) {
        Error error = new Error(
            LocalDateTime.now(),
            String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
            "Internal Server Error",
            "An unexpected error occurred: " + ex.getMessage()
        );
        kafkaProducerService.sendMessage(error, "Response");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
