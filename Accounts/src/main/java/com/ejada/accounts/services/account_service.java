package com.ejada.accounts.services;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ejada.accounts.Repos.account_repo;
import com.ejada.accounts.models.Accountstatus;
import com.ejada.accounts.models.Accounttype;
import com.ejada.accounts.models.account_model;

@Service
public class account_service {

    @Autowired
    private account_repo accountRepo;

    public account_model create_account(HashMap<String, Object> accountData) {
        Accounttype accountType = Accounttype.fromString((String) accountData.get("accountType"));
        Double initialBalance = (Double) accountData.get("initialBalance");
        if(initialBalance < 0 || accountType == null) {
            return null;
        }
        account_model account = new account_model();
        account.setUser_id((UUID) accountData.get("userId"));
        account.setAccount_type(accountType);
        account.setBalance(initialBalance);
        return accountRepo.save(account);
    }

    public account_model get_account(UUID accountId) {
        return accountRepo.findById(accountId).orElse(null);
    }

    public List<account_model> get_all_accounts(UUID userId) {
        return accountRepo.findByUser_id(userId);
    }

    public void getAmount(UUID accountId) {
        accountRepo.deleteById(accountId);
    }

    public void set_inactive(UUID accountId) {
        account_model account = accountRepo.findById(accountId).orElse(null);
        if (account != null) {
            account.setStatus(Accountstatus.INACTIVE);
        }
    }

    public void update_amount(UUID fromAccountId,UUID toAccountId, Double amount) {
        account_model toAccount = accountRepo.findById(fromAccountId).orElse(null);
        account_model fromAccount = accountRepo.findById(toAccountId).orElse(null);

        if (toAccount != null && fromAccount != null) {
            if(fromAccount.getBalance() >= amount) {
                fromAccount.setBalance(fromAccount.getBalance() - amount);
                toAccount.setBalance(toAccount.getBalance() + amount);
                fromAccount.setUpdate_at(Timestamp.from(Instant.now()));
                toAccount.setUpdate_at(Timestamp.from(Instant.now()));
                accountRepo.save(fromAccount);
                accountRepo.save(toAccount);
            } else {
                throw new IllegalArgumentException("Insufficient funds in the source account.");
            }
        } else {
            throw new IllegalArgumentException("Account not found.");
        }
    }

}
