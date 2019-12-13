package net.commuty.parking.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AccessDirection {
    IN("in"),
    OUT("out");

    private final String serializedDirection;

    AccessDirection(String serializedDirection) {
        this.serializedDirection = serializedDirection;
    }

    @JsonValue
    private String getSerializedDirection() {
        return serializedDirection;
    }
}
