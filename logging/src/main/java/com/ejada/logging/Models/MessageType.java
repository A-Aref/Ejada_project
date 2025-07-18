package com.ejada.logging.Models;

public enum MessageType {

    Request,
    Response;

    public static MessageType fromString(String type) {
        for (MessageType messageType : MessageType.values()) {
            if (messageType.name().equalsIgnoreCase(type)) {
                return messageType;
            }
        }
        return null;
    }

    public String geString() {
        return this.name();
    }

}
