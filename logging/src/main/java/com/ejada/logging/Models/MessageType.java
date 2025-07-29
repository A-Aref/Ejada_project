package com.ejada.logging.Models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MessageType {

    Request,
    Response;

    @JsonCreator
    public static MessageType fromString(String type) {
        if (type == null) {
            return null;
        }
        for (MessageType messageType : MessageType.values()) {
            if (messageType.name().equalsIgnoreCase(type.trim())) {
                return messageType;
            }
        }
        throw new IllegalArgumentException("Invalid MessageType: " + type + ". Valid values are: Request, Response");
    }

    @JsonValue
    public String getString() {
        return this.name();
    }

}
