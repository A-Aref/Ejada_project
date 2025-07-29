package com.ejada.transactions.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class TransactionConfig {

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public WebClient webClientAccounts() {
        return WebClient.builder()
                .baseUrl("http://localhost:8082/accounts")
                .build();
    }
}
