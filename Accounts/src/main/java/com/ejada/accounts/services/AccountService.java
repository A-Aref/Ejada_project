package com.ejada.accounts.Services;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import com.ejada.accounts.Models.AccountModel;
import com.ejada.accounts.dto.CreateAccountRequest;
import com.ejada.accounts.dto.TransferRequest;
import com.ejada.accounts.dto.TransferResponse;
import com.ejada.accounts.dto.AccountResponse;
import com.ejada.accounts.dto.AccountListResponse;

import jakarta.transaction.Transactional;

public interface AccountService {
    AccountResponse createAccount(CreateAccountRequest request);

    AccountResponse getAccount(UUID accountId);

    AccountListResponse getAllAccounts(UUID userId);

    void setInactive(UUID accountId);

    List<AccountModel> getActiveAccounts();

    @Transactional
    TransferResponse transferAmount(TransferRequest request);

    boolean shouldDeactivateAccount(AccountModel account, Timestamp cutoffTime);
}