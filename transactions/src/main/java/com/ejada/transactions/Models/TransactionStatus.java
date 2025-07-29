package com.ejada.transactions.Models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TransactionStatus {
    INITIATED,
    SUCCESS,
    FAILED;

    @JsonValue
    public String getString() {
        return this.name();
    }

    @JsonCreator
    public static TransactionStatus fromString(String value) {
        if (value == null) {
            return null;
        }
        for (TransactionStatus status : TransactionStatus.values()) {
            if (status.name().equalsIgnoreCase(value.trim())) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid TransactionStatus: " + value + ". Valid values are: INITIATED, SUCCESS, FAILED");
    }

}
