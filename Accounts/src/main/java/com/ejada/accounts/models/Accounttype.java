package com.ejada.accounts.Models;

public enum AccountType {
    SAVINGS,
    CHECKING;

    public String getString() {
        return this.name();
    }

    public static AccountType fromString(String type) {
        for (AccountType accountType : AccountType.values()) {
            if (accountType.name().equalsIgnoreCase(type)) {
                return accountType;
            }
        }
        return null;
    }
}
