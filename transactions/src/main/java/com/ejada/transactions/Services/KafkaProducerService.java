package com.ejada.transactions.Services;

import java.time.Instant;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, HashMap<String, Object>> kafkaTemplate;

    private HashMap<String, Object> log = new HashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendMessage(HashMap<String, Object> message, String messageType) {
        try {
            if(message == null) {
                message = new HashMap<>();
            }
            String jsonString = objectMapper.writeValueAsString(message);
            String escapeString = objectMapper.writeValueAsString(jsonString);
            System.out.println("Sending message to Kafka: " + escapeString);
            log.put("message", escapeString);
            log.put("messageType", messageType);
            log.put("dateTime", Instant.now().toString());
            kafkaTemplate.send("Logs", log);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
