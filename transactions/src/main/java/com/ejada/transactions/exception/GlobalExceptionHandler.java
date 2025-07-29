package com.ejada.transactions.exception;

import com.ejada.transactions.dto.ErrorResponse;
import com.ejada.transactions.Services.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTransactionNotFoundException(TransactionNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage()
        );
        
        HashMap<String, Object> kafkaMessage = new HashMap<>();
        kafkaMessage.put("message", ex.getMessage());
        kafkaMessage.put("error", "TRANSACTION_NOT_FOUND");
        kafkaProducerService.sendMessage(kafkaMessage, "Response");
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTransactionException(InvalidTransactionException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            ex.getMessage()
        );
        
        HashMap<String, Object> kafkaMessage = new HashMap<>();
        kafkaMessage.put("message", ex.getMessage());
        kafkaMessage.put("error", "INVALID_TRANSACTION");
        kafkaProducerService.sendMessage(kafkaMessage, "Response");
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(AccountServiceException.class)
    public ResponseEntity<ErrorResponse> handleAccountServiceException(AccountServiceException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            ex.getMessage()
        );
        
        HashMap<String, Object> kafkaMessage = new HashMap<>();
        kafkaMessage.put("message", ex.getMessage());
        kafkaMessage.put("error", "ACCOUNT_SERVICE_ERROR");
        kafkaProducerService.sendMessage(kafkaMessage, "Response");
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(TransactionExecutionException.class)
    public ResponseEntity<ErrorResponse> handleTransactionExecutionException(TransactionExecutionException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            ex.getMessage()
        );
        
        HashMap<String, Object> kafkaMessage = new HashMap<>();
        kafkaMessage.put("message", ex.getMessage());
        kafkaMessage.put("error", "TRANSACTION_EXECUTION_ERROR");
        kafkaProducerService.sendMessage(kafkaMessage, "Response");
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            "Validation failed: " + errors.toString()
        );
        
        HashMap<String, Object> kafkaMessage = new HashMap<>();
        kafkaMessage.put("message", "Validation failed");
        kafkaMessage.put("errors", errors);
        kafkaProducerService.sendMessage(kafkaMessage, "Response");
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        Class<?> requiredType = ex.getRequiredType();
        String typeName = (requiredType != null) ? requiredType.getSimpleName() : "unknown";
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            "Invalid parameter type: " + ex.getName() + " should be of type " + typeName
        );
        
        HashMap<String, Object> kafkaMessage = new HashMap<>();
        kafkaMessage.put("message", errorResponse.getMessage());
        kafkaMessage.put("error", "TYPE_MISMATCH_ERROR");
        kafkaProducerService.sendMessage(kafkaMessage, "Response");
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred: " + ex.getMessage()
        );
        
        HashMap<String, Object> kafkaMessage = new HashMap<>();
        kafkaMessage.put("message", "An unexpected error occurred");
        kafkaMessage.put("error", "INTERNAL_SERVER_ERROR");
        kafkaProducerService.sendMessage(kafkaMessage, "Response");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
