package com.ejada.transactions.Config;

import org.springframework.http.HttpHeaders;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClientAccounts()
    {
        return WebClient.builder()
        .baseUrl("http://localhost:8082/accounts")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
    }

}

