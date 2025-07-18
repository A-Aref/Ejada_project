package com.ejada.logging.Services;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

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

    public LogsModel saveLog(Map<String,Object> log) {
        LogsModel logsModel = new LogsModel();
        logsModel.setMessage((String) log.get("message"));
        logsModel.setMessageType(MessageType.valueOf((String) log.get("messageType")));
        logsModel.setDateTime(Timestamp.valueOf((String) log.get("timestamp")));
        return logsRepo.save(logsModel);
    } 
}
