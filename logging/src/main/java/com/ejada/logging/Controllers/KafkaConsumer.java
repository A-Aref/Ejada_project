package com.ejada.logging.Controllers;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.ejada.logging.Services.LogsService;


@Service
public class KafkaConsumer {

    @Autowired
    private LogsService logsService;

    @KafkaListener(topics = "Logs", groupId = "logging-group")
    public void listen(HashMap<String,Object> message) {
        try {
            // Process the incoming message
            System.out.println("Received message: " + message);
            logsService.saveLog(message);
            System.out.println("Message processed successfully");
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
