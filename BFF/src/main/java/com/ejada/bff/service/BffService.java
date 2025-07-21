package com.ejada.bff.service;

import com.ejada.bff.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
public class BffService {

    private final WebClient userWebClient;
    private final WebClient accountWebClient;
    private final WebClient transactionWebClient;

    public BffService(WebClient userWebClient,
                      WebClient accountWebClient,
                      WebClient transactionWebClient) {
        this.userWebClient = userWebClient;
        this.accountWebClient = accountWebClient;
        this.transactionWebClient = transactionWebClient;
    }

    private UserProfile getUserProfile(UUID userId){
        return userWebClient.get()
                .uri("/users/{userId}/profile", userId)
                .retrieve()
                .bodyToMono(UserProfile.class)
                .block();
    }
    private UserAccounts getUserAccounts(UUID userId){
        return accountWebClient.get()
                .uri("/accounts/users/{userId}", userId)
                .retrieve()
                .bodyToMono(UserAccounts.class)
                .block();
    }
    private AccountTransactions getAccountTransactions(UUID accountId){
        return transactionWebClient.get()
                .uri("/transactions/accounts/{accountId}", accountId)
                .retrieve()
                .bodyToMono(AccountTransactions.class)
                .block();
    }
    public DashboardResponse getDashboard(UUID userId) {
        UserProfile userProfile = getUserProfile(userId);
        UserAccounts userAccounts = getUserAccounts(userId);

        List<AccountWithTransactions> detailedAccounts = userAccounts.getAccounts().stream()
                .map(account -> {
                    AccountTransactions transactions = getAccountTransactions(account.getAccountId());
                    return new AccountWithTransactions(
                            account.getAccountId(),
                            account.getAccountNumber(),
                            account.getAccountType(),
                            account.getBalance(),
                            account.getStatus(),
                            transactions.getTransactions()
                    );
                })
                .toList();

        return new DashboardResponse(
                userProfile.getUserId(),
                userProfile.getUsername(),
                userProfile.getEmail(),
                userProfile.getFirstName(),
                userProfile.getLastName(),
                detailedAccounts
        );
    }


}

