package com.ejada.transactions.Controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

@RestController
@RequestMapping("/api-spec")
@Tag(name = "API Specification", description = "Download OpenAPI specification files")
public class OpenApiController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping(value = "/download", produces = "application/x-yaml")
    @Operation(summary = "Download OpenAPI YAML", 
               description = "Downloads the OpenAPI specification file in YAML format")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "OpenAPI YAML file downloaded successfully",
                    content = @Content(mediaType = "application/x-yaml")),
        @ApiResponse(responseCode = "500", 
                    description = "Error generating OpenAPI specification")
    })
    public ResponseEntity<String> downloadOpenApiYaml() {
        try {
            // Get the JSON from the built-in api-docs endpoint
            String jsonContent = restTemplate.getForObject("http://localhost:8083/api-docs", String.class);
            
            // Convert JSON to YAML
            ObjectMapper jsonMapper = new ObjectMapper();
            ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
            JsonNode jsonNode = jsonMapper.readTree(jsonContent);
            String yamlContent = yamlMapper.writeValueAsString(jsonNode);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/x-yaml"));
            headers.setContentDispositionFormData("attachment", "transactions-service-openapi.yml");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(yamlContent);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error generating OpenAPI specification: " + e.getMessage());
        }
    }

    @GetMapping(value = "/view", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "View OpenAPI YAML", 
               description = "View the OpenAPI specification file in YAML format")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "OpenAPI YAML content displayed successfully",
                    content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "500", 
                    description = "Error generating OpenAPI specification")
    })
    public ResponseEntity<String> viewOpenApiYaml() {
        try {
            // Get the JSON from the built-in api-docs endpoint
            String jsonContent = restTemplate.getForObject("http://localhost:8083/api-docs", String.class);
            
            // Convert JSON to YAML
            ObjectMapper jsonMapper = new ObjectMapper();
            ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
            JsonNode jsonNode = jsonMapper.readTree(jsonContent);
            String yamlContent = yamlMapper.writeValueAsString(jsonNode);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(yamlContent);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error generating OpenAPI specification: " + e.getMessage());
        }
    }
}
