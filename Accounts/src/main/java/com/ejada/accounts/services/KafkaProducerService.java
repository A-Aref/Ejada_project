package com.ejada.accounts.Services;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, HashMap<String,Object>> kafkaTemplate;

    public void sendMessage(HashMap<String,Object> message) {
        kafkaTemplate.send("Logs", message);
    }

}
