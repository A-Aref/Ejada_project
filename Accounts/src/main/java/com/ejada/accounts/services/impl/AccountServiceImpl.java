package com.ejada.accounts.Services.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.ejada.accounts.Models.AccountModel;
import com.ejada.accounts.Models.AccountStatus;
import com.ejada.accounts.Repos.AccountRepo;
import com.ejada.accounts.Services.AccountService;
import com.ejada.accounts.dto.CreateAccountRequest;
import com.ejada.accounts.dto.TransferRequest;
import com.ejada.accounts.dto.TransferResponse;
import com.ejada.accounts.dto.AccountResponse;
import com.ejada.accounts.dto.AccountListResponse;
import com.ejada.accounts.dto.AccountMapper;
import com.ejada.accounts.exception.AccountNotFoundException;
import com.ejada.accounts.exception.InsufficientFundsException;
import com.ejada.accounts.exception.InvalidAccountDataException;
import com.ejada.accounts.exception.InvalidTransferException;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private WebClient webClientTransactions;

    @Override
    public AccountResponse createAccount(CreateAccountRequest request) {
        if (request.getUserId() == null || request.getAccountType() == null) {
            throw new InvalidAccountDataException("User ID and account type are required");
        }
        
        if (request.getInitialBalance() != null && request.getInitialBalance() < 0) {
            throw new InvalidAccountDataException("Initial balance cannot be negative");
        }
        
        AccountModel account = new AccountModel();
        account.setUserId(request.getUserId());
        account.setAccountType(request.getAccountType());
        account.setBalance(request.getInitialBalance() != null ? request.getInitialBalance() : 0.00);
        
        AccountModel savedAccount = accountRepo.save(account);
        return AccountMapper.toAccountResponse(savedAccount);
    }

    @Override
    public AccountResponse getAccount(UUID accountId) {
        AccountModel account = accountRepo.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));
        return AccountMapper.toAccountResponse(account);
    }

    @Override
    public AccountListResponse getAllAccounts(UUID userId) {
        List<AccountModel> accounts = accountRepo.findByUserId(userId);
        return AccountMapper.toAccountListResponse(accounts);
    }

    @Override
    public void setInactive(UUID accountId) {
        AccountModel account = accountRepo.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));
        
        account.setStatus(AccountStatus.INACTIVE);
        accountRepo.save(account);
    }

    @Override
    public List<AccountModel> getActiveAccounts() {
        return accountRepo.findByStatus(AccountStatus.ACTIVE);
    }

    @Override
    @Transactional
    public TransferResponse transferAmount(TransferRequest request) {
        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new InvalidTransferException("Cannot transfer to the same account");
        }
        
        if (request.getAmount() <= 0) {
            throw new InvalidTransferException("Transfer amount must be positive");
        }
        
        AccountModel fromAccount = accountRepo.findById(request.getFromAccountId())
            .orElseThrow(() -> new AccountNotFoundException("Source account not found with ID: " + request.getFromAccountId()));
            
        AccountModel toAccount = accountRepo.findById(request.getToAccountId())
            .orElseThrow(() -> new AccountNotFoundException("Destination account not found with ID: " + request.getToAccountId()));

        
        if (fromAccount.getBalance() < request.getAmount()) {
            throw new InsufficientFundsException("Insufficient funds in the source account. Available: " + fromAccount.getBalance() + ", Required: " + request.getAmount());
        }
        
        fromAccount.setBalance(fromAccount.getBalance() - request.getAmount());
        toAccount.setBalance(toAccount.getBalance() + request.getAmount());
        
        accountRepo.save(fromAccount);
        accountRepo.save(toAccount);

        return AccountMapper.toTransferResponse(
            "Transfer successful", 
            request.getFromAccountId(), 
            request.getToAccountId(), 
            request.getAmount()
        );
    }

    @Override
    public boolean shouldDeactivateAccount(AccountModel account, Timestamp cutoffTime) {
        // If account is just created, don't deactivate
        if (account.getCreatedAt().after(cutoffTime)) {
            return false;
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
                return transactionTimestamp.before(cutoffTime);
            }
        } catch (Exception e) {
            // In case of error, don't deactivate the account
            return false;
        }
        
        return false;
    }

}
