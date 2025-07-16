package com.ejada.accounts.models;

public enum Accountstatus {
    ACTIVE,
    INACTIVE;

    public String getStatus() {
        return this.name();
    }

    public static Accountstatus fromString(String status) {
        for (Accountstatus accountStatus : Accountstatus.values()) {
            if (accountStatus.name().equalsIgnoreCase(status)) {
                return accountStatus;
            }
        }
        return null;
    }
}
