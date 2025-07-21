package com.ejada.logging.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ejada.logging.Models.LogsModel;
import com.ejada.logging.Services.LogsService;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/logs")
public class LogsController {

    @Autowired
    private LogsService logsService;

    @GetMapping("/")
    public ResponseEntity<HashMap<String,Object>> getLogs() {
        HashMap<String, Object> response = new HashMap<>();
        response.put("message", "Logs retrieved successfully");
        List<LogsModel> logs = logsService.getLogs();
        if (logs.isEmpty()) {
            response.put("message", "No logs found");
            return ResponseEntity.status(404).body(response);
        }
        response.put("logs", logsService.getLogs());
        return ResponseEntity.status(200).body(response);
    }
    

}
