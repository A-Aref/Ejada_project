package com.ejada.transactions.Models;

public enum TransactionStatus {
    INITIATED,
    SUCCESS,
    FAILED;

    public String getString() {
        return this.name();
    }

    public static TransactionStatus fromString(String value) {
        for (TransactionStatus status : TransactionStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        return null;
    }

}
