package com.ejada.logging.Controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import com.ejada.logging.Services.LogsService;

public class KafkaConsumer {

    @Autowired
    private LogsService logsService;

    @KafkaListener(topics = "Logs", groupId = "logging-group")
    public void listen(Map<String,Object> message) {
        // Process the incoming message
        System.out.println("Received message: " + message);
        logsService.saveLog(message);
    }

}
