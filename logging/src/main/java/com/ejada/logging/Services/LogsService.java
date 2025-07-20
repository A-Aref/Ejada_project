package com.ejada.logging.Services;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ejada.logging.Models.LogsModel;
import com.ejada.logging.Models.MessageType;
import com.ejada.logging.Repos.LogsRepo;

@Service
public class LogsService {

    @Autowired
    private LogsRepo logsRepo;

    public List<LogsModel> getLogs() {
        return logsRepo.findAll();
    }

    public LogsModel saveLog(HashMap<String,Object> log) {
        LogsModel logsModel = new LogsModel();
        logsModel.setMessage((String) log.get("message"));
        logsModel.setMessageType(MessageType.fromString((String) log.get("messageType")));
        logsModel.setDateTime(Timestamp.from(Instant.parse((String) log.get("dateTime"))));
        return logsRepo.save(logsModel);
    } 
}
