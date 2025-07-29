package com.ejada.accounts.dto;

import com.ejada.accounts.Models.AccountModel;
import com.ejada.accounts.Models.AccountType;
import com.ejada.accounts.Models.AccountStatus;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AccountMapper {
    
    public static AccountResponse toAccountResponse(AccountModel account) {
        if (account == null) {
            return null;
        }
        
        try {
            // Validate and handle enum fields with comprehensive error checking
            AccountType accountType = validateAccountType(account.getAccountType());
            AccountStatus accountStatus = validateAccountStatus(account.getStatus());
            
            // Validate other critical fields
            UUID accountId = account.getId();
            if (accountId == null) {
                throw new IllegalArgumentException("Account ID cannot be null");
            }
            
            String accountNumber = account.getAccountNumber();
            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Account number cannot be null or empty");
            }
            
            Double balance = account.getBalance();
            if (balance == null) {
                balance = 0.0; // Default to 0 if null
            }
            
            return new AccountResponse(
                accountId,
                accountNumber,
                accountType,
                balance,
                accountStatus
            );
        } catch (IllegalArgumentException e) {
            // Log the error and return null or throw based on business requirements
            System.err.println("Error mapping AccountModel to AccountResponse: " + e.getMessage());
            // Return null for graceful degradation, or rethrow if strict validation is required
            return null;
        } catch (Exception e) {
            // Handle any unexpected errors
            System.err.println("Unexpected error in AccountMapper.toAccountResponse: " + e.getMessage());
            return null;
        }
    }
    
    public static List<AccountResponse> toAccountResponseList(List<AccountModel> accounts) {
        if (accounts == null) {
            return null;
        }
        
        try {
            return accounts.stream()
                    .map(AccountMapper::toAccountResponse)
                    .filter(response -> response != null) // Filter out null responses from failed mappings
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error mapping AccountModel list to AccountResponse list: " + e.getMessage());
            return null;
        }
    }
    
    public static AccountListResponse toAccountListResponse(List<AccountModel> accounts) {
        if (accounts == null) {
            return new AccountListResponse(null);
        }
        
        try {
            List<AccountResponse> accountResponses = toAccountResponseList(accounts);
            return new AccountListResponse(accountResponses);
        } catch (Exception e) {
            System.err.println("Error creating AccountListResponse: " + e.getMessage());
            return new AccountListResponse(null);
        }
    }
    
    public static TransferResponse toTransferResponse(String message, UUID fromAccountId, UUID toAccountId, Double amount) {
        try {
            // Validate input parameters
            if (message == null || message.trim().isEmpty()) {
                message = "Account updated successfully."; // Default message to match API spec
            }
            
            if (fromAccountId == null) {
                System.err.println("Warning: From Account ID is null in transfer response");
            }
            
            if (toAccountId == null) {
                System.err.println("Warning: To Account ID is null in transfer response");
            }
            
            if (amount == null || amount <= 0) {
                System.err.println("Warning: Transfer amount is invalid: " + amount);
                message = "Transfer failed due to invalid amount";
            }
            
            return new TransferResponse(message);
        } catch (IllegalArgumentException e) {
            System.err.println("Error creating TransferResponse: " + e.getMessage());
            return new TransferResponse("Transfer failed: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error in toTransferResponse: " + e.getMessage());
            return new TransferResponse("Transfer failed due to unexpected error");
        }
    }
    
    /**
     * Maps AccountModel to CreateAccountResponse for account creation API
     */
    public static CreateAccountResponse toCreateAccountResponse(AccountModel account) {
        if (account == null) {
            return null;
        }
        
        try {
            return new CreateAccountResponse(
                account.getId(),
                account.getAccountNumber(),
                "Account created successfully."
            );
        } catch (Exception e) {
            System.err.println("Error creating CreateAccountResponse: " + e.getMessage());
            return new CreateAccountResponse(null, null, "Account creation failed");
        }
    }
    
    /**
     * Validates AccountType enum and handles all possible cases
     */
    private static AccountType validateAccountType(AccountType accountType) {
        if (accountType == null) {
            System.err.println("AccountType is null, defaulting to CHECKING");
            return AccountType.CHECKING; // Default to CHECKING if null
        }
        
        // Validate that the enum value is one of the expected values
        try {
            switch (accountType) {
                case SAVINGS:
                case CHECKING:
                    return accountType;
                default:
                    System.err.println("Unknown AccountType: " + accountType + ", defaulting to CHECKING");
                    return AccountType.CHECKING;
            }
        } catch (Exception e) {
            System.err.println("Error validating AccountType: " + e.getMessage() + ", defaulting to CHECKING");
            return AccountType.CHECKING;
        }
    }
    
    /**
     * Validates AccountStatus enum and handles all possible cases
     */
    private static AccountStatus validateAccountStatus(AccountStatus accountStatus) {
        if (accountStatus == null) {
            System.err.println("AccountStatus is null, defaulting to ACTIVE");
            return AccountStatus.ACTIVE; // Default to ACTIVE if null
        }
        
        // Validate that the enum value is one of the expected values
        try {
            switch (accountStatus) {
                case ACTIVE:
                case INACTIVE:
                    return accountStatus;
                default:
                    System.err.println("Unknown AccountStatus: " + accountStatus + ", defaulting to ACTIVE");
                    return AccountStatus.ACTIVE;
            }
        } catch (Exception e) {
            System.err.println("Error validating AccountStatus: " + e.getMessage() + ", defaulting to ACTIVE");
            return AccountStatus.ACTIVE;
        }
    }
    
    /**
     * Safe enum parsing with comprehensive error handling for AccountType
     */
    public static AccountType parseAccountType(String typeString) {
        if (typeString == null || typeString.trim().isEmpty()) {
            System.err.println("AccountType string is null or empty, defaulting to CHECKING");
            return AccountType.CHECKING;
        }
        
        try {
            // Try direct enum parsing first
            return AccountType.valueOf(typeString.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            try {
                // Try using the fromString method if available
                AccountType result = AccountType.fromString(typeString);
                if (result != null) {
                    return result;
                }
            } catch (Exception innerE) {
                System.err.println("Error using AccountType.fromString: " + innerE.getMessage());
            }
            
            // Fallback to case-insensitive matching
            String normalizedType = typeString.trim().toUpperCase();
            switch (normalizedType) {
                case "SAVINGS":
                    return AccountType.SAVINGS;
                case "CHECKING":
                    return AccountType.CHECKING;
                default:
                    System.err.println("Unknown AccountType string: " + typeString + ", defaulting to CHECKING");
                    return AccountType.CHECKING;
            }
        }
    }
    
    /**
     * Safe enum parsing with comprehensive error handling for AccountStatus
     */
    public static AccountStatus parseAccountStatus(String statusString) {
        if (statusString == null || statusString.trim().isEmpty()) {
            System.err.println("AccountStatus string is null or empty, defaulting to ACTIVE");
            return AccountStatus.ACTIVE;
        }
        
        try {
            // Try direct enum parsing first
            return AccountStatus.valueOf(statusString.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            try {
                // Try using the fromString method if available
                AccountStatus result = AccountStatus.fromString(statusString);
                if (result != null) {
                    return result;
                }
            } catch (Exception innerE) {
                System.err.println("Error using AccountStatus.fromString: " + innerE.getMessage());
            }
            
            // Fallback to case-insensitive matching
            String normalizedStatus = statusString.trim().toUpperCase();
            switch (normalizedStatus) {
                case "ACTIVE":
                    return AccountStatus.ACTIVE;
                case "INACTIVE":
                    return AccountStatus.INACTIVE;
                default:
                    System.err.println("Unknown AccountStatus string: " + statusString + ", defaulting to ACTIVE");
                    return AccountStatus.ACTIVE;
            }
        }
    }
}
