package com.ejada.accounts.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.ejada.accounts.Models.AccountModel;
import com.ejada.accounts.Models.AccountStatus;
import com.ejada.accounts.Models.AccountType;

public class AccountMapperTest {

    @Test
    public void testToAccountResponse_WithValidData() {
        // Given
        AccountModel account = new AccountModel();
        account.setId(UUID.randomUUID());
        account.setAccountNumber("ACCT-12345678");
        account.setAccountType(AccountType.CHECKING);
        account.setBalance(1000.0);
        account.setStatus(AccountStatus.ACTIVE);

        // When
        AccountResponse response = AccountMapper.toAccountResponse(account);

        // Then
        assertNotNull(response);
        assertEquals(account.getId(), response.getAccountId());
        assertEquals(account.getAccountNumber(), response.getAccountNumber());
        assertEquals(account.getAccountType(), response.getAccountType());
        assertEquals(account.getBalance(), response.getBalance());
        assertEquals(account.getStatus(), response.getStatus());
    }

    @Test
    public void testToAccountResponse_WithNullAccount() {
        // When
        AccountResponse response = AccountMapper.toAccountResponse(null);

        // Then
        assertNull(response);
    }

    @Test
    public void testToAccountResponse_WithNullFields() {
        // Given
        AccountModel account = new AccountModel();
        account.setId(UUID.randomUUID());
        account.setAccountNumber("ACCT-12345678");
        // accountType and status are null
        account.setBalance(null);

        // When
        AccountResponse response = AccountMapper.toAccountResponse(account);

        // Then
        assertNotNull(response);
        assertEquals(0.0, response.getBalance()); // Should default to 0.0
        assertEquals(AccountType.CHECKING, response.getAccountType()); // Should default to CHECKING
        assertEquals(AccountStatus.ACTIVE, response.getStatus()); // Should default to ACTIVE
    }

    @Test
    public void testParseAccountType_ValidValues() {
        assertEquals(AccountType.SAVINGS, AccountMapper.parseAccountType("SAVINGS"));
        assertEquals(AccountType.CHECKING, AccountMapper.parseAccountType("CHECKING"));
        assertEquals(AccountType.SAVINGS, AccountMapper.parseAccountType("savings"));
        assertEquals(AccountType.CHECKING, AccountMapper.parseAccountType("checking"));
    }

    @Test
    public void testParseAccountType_AlternativeValues() {
        assertEquals(AccountType.SAVINGS, AccountMapper.parseAccountType("SAVING"));
        assertEquals(AccountType.CHECKING, AccountMapper.parseAccountType("CHECK"));
        assertEquals(AccountType.CHECKING, AccountMapper.parseAccountType("CURRENT"));
    }

    @Test
    public void testParseAccountType_InvalidValues() {
        assertEquals(AccountType.CHECKING, AccountMapper.parseAccountType("INVALID"));
        assertEquals(AccountType.CHECKING, AccountMapper.parseAccountType(""));
        assertEquals(AccountType.CHECKING, AccountMapper.parseAccountType(null));
        assertEquals(AccountType.CHECKING, AccountMapper.parseAccountType("   "));
    }

    @Test
    public void testParseAccountStatus_ValidValues() {
        assertEquals(AccountStatus.ACTIVE, AccountMapper.parseAccountStatus("ACTIVE"));
        assertEquals(AccountStatus.INACTIVE, AccountMapper.parseAccountStatus("INACTIVE"));
        assertEquals(AccountStatus.ACTIVE, AccountMapper.parseAccountStatus("active"));
        assertEquals(AccountStatus.INACTIVE, AccountMapper.parseAccountStatus("inactive"));
    }

    @Test
    public void testParseAccountStatus_AlternativeValues() {
        assertEquals(AccountStatus.ACTIVE, AccountMapper.parseAccountStatus("ENABLED"));
        assertEquals(AccountStatus.ACTIVE, AccountMapper.parseAccountStatus("OPEN"));
        assertEquals(AccountStatus.INACTIVE, AccountMapper.parseAccountStatus("DISABLED"));
        assertEquals(AccountStatus.INACTIVE, AccountMapper.parseAccountStatus("CLOSED"));
        assertEquals(AccountStatus.INACTIVE, AccountMapper.parseAccountStatus("SUSPENDED"));
    }

    @Test
    public void testParseAccountStatus_InvalidValues() {
        assertEquals(AccountStatus.ACTIVE, AccountMapper.parseAccountStatus("INVALID"));
        assertEquals(AccountStatus.ACTIVE, AccountMapper.parseAccountStatus(""));
        assertEquals(AccountStatus.ACTIVE, AccountMapper.parseAccountStatus(null));
        assertEquals(AccountStatus.ACTIVE, AccountMapper.parseAccountStatus("   "));
    }

    @Test
    public void testToAccountResponseList_WithValidData() {
        // Given
        AccountModel account1 = new AccountModel();
        account1.setId(UUID.randomUUID());
        account1.setAccountNumber("ACCT-11111111");
        account1.setAccountType(AccountType.SAVINGS);
        account1.setBalance(500.0);
        account1.setStatus(AccountStatus.ACTIVE);

        AccountModel account2 = new AccountModel();
        account2.setId(UUID.randomUUID());
        account2.setAccountNumber("ACCT-22222222");
        account2.setAccountType(AccountType.CHECKING);
        account2.setBalance(1500.0);
        account2.setStatus(AccountStatus.INACTIVE);

        List<AccountModel> accounts = Arrays.asList(account1, account2);

        // When
        List<AccountResponse> responses = AccountMapper.toAccountResponseList(accounts);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
    }

    @Test
    public void testToAccountResponseList_WithNullList() {
        // When
        List<AccountResponse> responses = AccountMapper.toAccountResponseList(null);

        // Then
        assertNull(responses);
    }

    @Test
    public void testToTransferResponse_WithValidData() {
        // Given
        String message = "Transfer successful";
        UUID fromAccountId = UUID.randomUUID();
        UUID toAccountId = UUID.randomUUID();
        Double amount = 100.0;

        // When
        TransferResponse response = AccountMapper.toTransferResponse(message, fromAccountId, toAccountId, amount);

        // Then
        assertNotNull(response);
        assertEquals(message, response.getMessage());
    }

    @Test
    public void testToTransferResponse_WithNullMessage() {
        // Given
        UUID fromAccountId = UUID.randomUUID();
        UUID toAccountId = UUID.randomUUID();
        Double amount = 100.0;

        // When
        TransferResponse response = AccountMapper.toTransferResponse(null, fromAccountId, toAccountId, amount);

        // Then
        assertNotNull(response);
        assertEquals("Transfer completed", response.getMessage()); // Should default
    }

    @Test
    public void testToTransferResponse_WithInvalidAmount() {
        // Given
        String message = "Transfer test";
        UUID fromAccountId = UUID.randomUUID();
        UUID toAccountId = UUID.randomUUID();
        Double amount = -100.0; // Invalid amount

        // When
        TransferResponse response = AccountMapper.toTransferResponse(message, fromAccountId, toAccountId, amount);

        // Then
        assertNotNull(response);
        assertTrue(response.getMessage().contains("Warning: Invalid amount"));
    }
}
