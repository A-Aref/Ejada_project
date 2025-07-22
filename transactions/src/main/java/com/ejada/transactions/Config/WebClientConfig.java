package com.ejada.transactions.Config;

import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${ACCOUNT_SERVICE_URL:http://localhost:8082}")
    private String accountServiceUrl;

    @Bean
    public WebClient webClientAccounts()
    {
        return WebClient.builder()
        .baseUrl(accountServiceUrl+"/accounts")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
    }

}

