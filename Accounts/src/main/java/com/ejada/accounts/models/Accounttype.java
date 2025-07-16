package com.ejada.accounts.models;

public enum Accounttype {
    SAVINGS,
    CHECKING;

    public String getType() {
        return this.name();
    }

    public static Accounttype fromString(String type) {
        for (Accounttype accountType : Accounttype.values()) {
            if (accountType.name().equalsIgnoreCase(type)) {
                return accountType;
            }
        }
        return null;
    }
}
