package com.ejada.users.exception;

import com.ejada.users.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class GlobalExceptionHandler {
    @Autowired
    private KafkaProducerService kafkaProducerService;

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFound(UserNotFoundException ex) {
        Error error=new Error(HttpStatus.NOT_FOUND.value()+"","User Not Found",ex.getMessage());
        kafkaProducerService.sendMessage(error, "Response");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Object> handleUnauthorized(UnauthorizedException ex) {
        Error error=new Error(HttpStatus.UNAUTHORIZED.value()+"","Unauthorized",ex.getMessage());
        kafkaProducerService.sendMessage(error, "Response");
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<Object> handleDuplicateUser(DuplicateUserException ex) {
        Error error=new Error(HttpStatus.CONFLICT.value()+"","Conflict",ex.getMessage());
        kafkaProducerService.sendMessage(error, "Response");
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleOtherException(Exception ex) {
        Error error=new Error(HttpStatus.INTERNAL_SERVER_ERROR.value()+"","Internal Server Error",ex.getMessage());
        kafkaProducerService.sendMessage(error, "Response");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
