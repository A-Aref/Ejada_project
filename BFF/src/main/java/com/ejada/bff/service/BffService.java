package com.ejada.bff.service;

import com.ejada.bff.dto.*;
import com.ejada.bff.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
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
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> {
                    if (response.statusCode() == HttpStatus.NOT_FOUND) {
                        return response.bodyToMono(String.class)
                                .map(body -> new NotFoundException("User with ID " + userId + " not found."));
                    }
                    return response.bodyToMono(String.class)
                            .map(body -> new RuntimeException( "Failed to retrieve dashboard data due to an issue with downstream services."));
                })
                .bodyToMono(UserProfile.class)
                .block();
    }
    private UserAccounts getUserAccounts(UUID userId){
        return accountWebClient.get()
                .uri("/accounts/users/{userId}", userId)
                .retrieve()
                .onStatus(status->status.is5xxServerError(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException("Downstream Account Service error"))))
                .bodyToMono(UserAccounts.class)
                .onErrorResume(ex -> {
                    System.out.println("point2");
                    return Mono.just(new UserAccounts());
                })
                .block();
    }

    private AccountTransactions getAccountTransactions(UUID accountId){
        return transactionWebClient.get()
                .uri("/transactions/accounts/{accountId}", accountId)
                .retrieve()
                .onStatus(status->status.is5xxServerError(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException("Downstream Transaction Service error"))))
                .bodyToMono(AccountTransactions.class)
                .onErrorResume(ex -> {
                    return Mono.just(new AccountTransactions());
                })
                .block();
    }

    public DashboardResponse getDashboard(UUID userId) {
        UserProfile userProfile = getUserProfile(userId);
        UserAccounts userAccounts = getUserAccounts(userId);

        List<AccountWithTransactions> detailedAccounts = !userAccounts.getAccounts().isEmpty()?userAccounts.getAccounts().stream()
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
                .toList():new ArrayList<>();

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

