package com.ejada.accounts.Models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AccountType {
    SAVINGS,
    CHECKING;

    @JsonValue
    public String getString() {
        return this.name();
    }

    @JsonCreator
    public static AccountType fromString(String type) {
        if (type == null) {
            return null;
        }
        for (AccountType accountType : AccountType.values()) {
            if (accountType.name().equalsIgnoreCase(type.trim())) {
                return accountType;
            }
        }
        throw new IllegalArgumentException("Invalid AccountType: " + type + ". Valid values are: SAVINGS, CHECKING");
    }
}
