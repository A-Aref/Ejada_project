package com.ejada.accounts.Services;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ejada.accounts.Models.AccountModel;
import com.ejada.accounts.Models.AccountStatus;
import com.ejada.accounts.Models.AccountType;
import com.ejada.accounts.Repos.AccountRepo;

@Service
public class AccountService {

    @Autowired
    private AccountRepo accountRepo;

    public AccountModel createAccount(HashMap<String, Object> accountData) {
        AccountType accountType = AccountType.fromString((String) accountData.get("accountType"));
        Double initialBalance = (Double) accountData.get("initialBalance");
        if(initialBalance < 0 || accountType == null) {
            return null;
        }
        AccountModel account = new AccountModel();
        account.setUserId((UUID) accountData.get("userId"));
        account.setAccountType(accountType);
        account.setBalance(initialBalance);
        return accountRepo.save(account);
    }

    public AccountModel getAccount(UUID accountId) {
        return accountRepo.findById(accountId).orElse(null);
    }

    public List<AccountModel> getAllAccounts(UUID userId) {
        return accountRepo.findByUserId(userId);
    }

    public void getAmount(UUID accountId) {
        accountRepo.deleteById(accountId);
    }

    public void setInactive(UUID accountId) {
        AccountModel account = accountRepo.findById(accountId).orElse(null);
        if (account != null) {
            account.setStatus(AccountStatus.INACTIVE);
        }
    }

    public void updateAmount(UUID fromAccountId,UUID toAccountId, Double amount) {
        AccountModel toAccount = accountRepo.findById(fromAccountId).orElse(null);
        AccountModel fromAccount = accountRepo.findById(toAccountId).orElse(null);

        if (toAccount != null && fromAccount != null) {
            if(fromAccount.getBalance() >= amount) {
                fromAccount.setBalance(fromAccount.getBalance() - amount);
                toAccount.setBalance(toAccount.getBalance() + amount);
                fromAccount.setUpdatedAt(Timestamp.from(Instant.now()));
                toAccount.setUpdatedAt(Timestamp.from(Instant.now()));
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
