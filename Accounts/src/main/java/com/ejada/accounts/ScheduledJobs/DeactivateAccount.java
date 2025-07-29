package com.ejada.accounts.ScheduledJobs;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ejada.accounts.Services.AccountService;

@Service

public class DeactivateAccount {

    @Autowired
    private AccountService accountService;

    @Scheduled(cron = "0 0 * * * * ")
    public void deactivateAccounts() {
        Timestamp oneDayAgo = Timestamp.from(Instant.now().minus(1, ChronoUnit.DAYS));
        accountService.getActiveAccounts().forEach(account -> {
            if (accountService.shouldDeactivateAccount(account, oneDayAgo)) {
                accountService.setInactive(account.getId());
            }
        });
    }
}
