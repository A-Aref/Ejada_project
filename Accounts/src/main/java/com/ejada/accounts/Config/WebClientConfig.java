package com.ejada.accounts.Config;

import org.springframework.http.HttpHeaders;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClientTransactions()
    {
        return WebClient.builder()
        .baseUrl("http://localhost:8083/transactions")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
    }
    @Bean
    public WebClient webClientUsers()
    {
        return WebClient.builder()
                .baseUrl("http://localhost:8080/users")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

}
