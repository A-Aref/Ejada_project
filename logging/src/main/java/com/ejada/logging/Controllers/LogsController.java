package com.ejada.logging.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ejada.logging.Services.LogsService;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/logs")
public class LogsController {

    @Autowired
    private LogsService logsService;

    @GetMapping("/")
    public HashMap<String,Object> getLogs() {
        HashMap<String, Object> response = new HashMap<>();
        response.put("message", "Logs retrieved successfully");
        response.put("logs", logsService.getLogs());
        return response;
    }
    

}
