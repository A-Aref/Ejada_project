package com.ejada.accounts.Services;

import java.util.List;
import java.util.UUID;
import com.ejada.accounts.Models.AccountModel;
import com.ejada.accounts.dto.CreateAccountRequest;
import com.ejada.accounts.dto.TransferRequest;
import com.ejada.accounts.dto.TransferResponse;

import jakarta.transaction.Transactional;

public interface AccountService {
    AccountModel createAccount(CreateAccountRequest request);

    AccountModel getAccount(UUID accountId);

    List<AccountModel> getAllAccounts(UUID userId);

    void setInactive(UUID accountId);

    List<AccountModel> getActiveAccounts();

    @Transactional
    TransferResponse transferAmount(TransferRequest request);
}