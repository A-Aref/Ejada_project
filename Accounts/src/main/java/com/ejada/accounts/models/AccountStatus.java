
package com.ejada.accounts.Models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AccountStatus {
    ACTIVE,
    INACTIVE;

    @JsonValue
    public String getString() {
        return this.name();
    }

    @JsonCreator
    public static AccountStatus fromString(String status) {
        if (status == null) {
            return null;
        }
        for (AccountStatus accountStatus : AccountStatus.values()) {
            if (accountStatus.name().equalsIgnoreCase(status.trim())) {
                return accountStatus;
            }
        }
        throw new IllegalArgumentException("Invalid AccountStatus: " + status + ". Valid values are: ACTIVE, INACTIVE");
    }
}

