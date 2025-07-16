
package com.ejada.accounts.Models;

public enum AccountStatus {
    ACTIVE,
    INACTIVE;

    public String getString() {
        return this.name();
    }

    public static AccountStatus fromString(String status) {
        for (AccountStatus accountStatus : AccountStatus.values()) {
            if (accountStatus.name().equalsIgnoreCase(status)) {
                return accountStatus;
            }
        }
        return null;
    }
}

