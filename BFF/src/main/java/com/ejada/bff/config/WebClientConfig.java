package com.ejada.bff.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${services.user.base-url}")
    private String userServiceUrl;

    @Value("${services.account.base-url}")
    private String accountServiceUrl;

    @Value("${services.transaction.base-url}")
    private String transactionServiceUrl;

    @Bean
    public WebClient userWebClient() {
        return WebClient.builder()
                .baseUrl(userServiceUrl)
                .build();
    }

    @Bean
    public WebClient accountWebClient() {
        return WebClient.builder()
                .baseUrl(accountServiceUrl)
                .build();
    }

    @Bean
    public WebClient transactionWebClient() {
        return WebClient.builder()
                .baseUrl(transactionServiceUrl)
                .build();
    }
}
