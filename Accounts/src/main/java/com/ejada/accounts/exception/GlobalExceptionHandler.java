package com.ejada.accounts.exception;

import com.ejada.accounts.Services.KafkaProducerService;
import com.ejada.accounts.dto.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @Autowired
    private KafkaProducerService kafkaProducerService;

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFound(AccountNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            404,
            "Not Found",
            ex.getMessage()
        );
        kafkaProducerService.sendMessage(error, "Response");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFunds(InsufficientFundsException ex) {
        ErrorResponse error = new ErrorResponse(
            400,
            "Bad Request",
            ex.getMessage()
        );
        kafkaProducerService.sendMessage(error, "Response");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidAccountDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAccountData(InvalidAccountDataException ex) {
        ErrorResponse error = new ErrorResponse(
            400,
            "Bad Request",
            "Invalid account type or initial balance."
        );
        kafkaProducerService.sendMessage(error, "Response");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidTransferException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTransfer(InvalidTransferException ex) {
        ErrorResponse error = new ErrorResponse(
            400,
            "Bad Request",
            ex.getMessage()
        );
        kafkaProducerService.sendMessage(error, "Response");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        ErrorResponse error = new ErrorResponse(
            400,
            "Bad Request",
            "Invalid account type or initial balance. " + errors
        );
        kafkaProducerService.sendMessage(error, "Response");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(
            400,
            "Bad Request",
            ex.getMessage()
        );
        kafkaProducerService.sendMessage(error, "Response");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                404,
                "Not Found",
                ex.getMessage()
        );
        kafkaProducerService.sendMessage(error, "Response");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOtherException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
            500,
            "Internal Server Error",
            "An unexpected error occurred: " + ex.getMessage()
        );
        kafkaProducerService.sendMessage(error, "Response");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
