package com.ejada.accounts.Controllers;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.ejada.accounts.Services.AccountService;

@Service

public class DeactivateAccount {

    @Autowired
    private AccountService accountService;

    @Autowired
    private WebClient webClientTransactions;

    @Scheduled(cron = "0 0 * * * * ")
    public void deactivateAccounts() {
        Timestamp oneDayAgo = Timestamp.from(Instant.now().minus(1, ChronoUnit.DAYS));
        accountService.getActiveAccounts().forEach(account -> {
            // if an account is just created what happens
            if (account.getCreatedAt().after(oneDayAgo)) {
                return;
            }
            try {
                ResponseEntity<HashMap<String, Object>> response = webClientTransactions.get()
                        .uri("/accounts/{accountId}/getLatest", account.getId())
                        .retrieve()
                        .toEntity(new ParameterizedTypeReference<HashMap<String, Object>>() {
                        })
                        .block();
                HashMap<String, Object> transaction = response.getBody();

                if (response.getStatusCode() == HttpStatusCode.valueOf(200) && transaction != null) {
                    Timestamp transactionTimestamp = Timestamp.valueOf((String) transaction.get("timestamp"));
                    if (transactionTimestamp.before(oneDayAgo)) {
                        accountService.setInactive(account.getId());
                    }
                }
            } catch (Exception e) {
                return;
            }

        });
    }
}
