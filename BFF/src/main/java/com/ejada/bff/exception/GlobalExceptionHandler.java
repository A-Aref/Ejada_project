package com.ejada.bff.exception;

import com.ejada.bff.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @Autowired
    private KafkaProducerService kafkaProducerService;
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFound(NotFoundException ex) {
        Error error=new Error(HttpStatus.NOT_FOUND.value()+"","User Not Found",ex.getMessage());
        kafkaProducerService.sendMessage(error, "Response");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleOtherException(Exception ex) {
        Error error=new Error(HttpStatus.INTERNAL_SERVER_ERROR.value()+"","Internal Server Error",ex.getMessage());
        kafkaProducerService.sendMessage(error, "Response");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
