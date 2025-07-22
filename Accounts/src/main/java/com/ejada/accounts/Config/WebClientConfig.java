package com.ejada.accounts.Config;

import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${TRANSACTION_SERVICE_URL:http://localhost:8083}")
    private String transactionServiceUrl;

    @Bean
    public WebClient webClientTransactions()
    {
        return WebClient.builder()
        .baseUrl(transactionServiceUrl+"/transactions")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
    } 

}
