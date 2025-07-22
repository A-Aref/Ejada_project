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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/logs")
@Tag(name = "Logs", description = "System logging API endpoints")
public class LogsController {

    @Autowired
    private LogsService logsService;

    @GetMapping("/")
    @Operation(summary = "Get all system logs", 
               description = "Retrieves all system logs from the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Logs retrieved successfully",
                    content = @Content(mediaType = "application/json",
                               examples = @ExampleObject(value = """
                                   {
                                       "message": "Logs retrieved successfully",
                                       "logs": [
                                           {
                                               "id": 1,
                                               "timestamp": "2024-01-01T10:00:00",
                                               "level": "INFO",
                                               "service": "users",
                                               "message": "User login successful",
                                               "details": "{\"userId\": \"123\", \"action\": \"login\"}"
                                           }
                                       ]
                                   }
                                   """))),
        @ApiResponse(responseCode = "404", 
                    description = "No logs found",
                    content = @Content(mediaType = "application/json",
                               examples = @ExampleObject(value = """
                                   {
                                       "message": "No logs found"
                                   }
                                   """)))
    })
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
