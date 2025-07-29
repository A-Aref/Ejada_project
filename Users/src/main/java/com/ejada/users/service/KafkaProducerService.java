package com.ejada.users.service;

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

    public void sendMessage(Object message, String messageType) {
        try {
            String jsonString = objectMapper.writeValueAsString(message);
            log.put("message", jsonString);
            log.put("messageType", messageType);
            log.put("dateTime", Instant.now().toString());
            kafkaTemplate.send("Logs", log);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
