package net.commuty.parking.model;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum UserIdType {
    EMAIL("email"),
    LICENSE_PLATE("licensePlate"),
    IDENTIFICATION_NUMBER("identificationNumber"),
    QR_CODE("qrCode"),
    BADGE_NUMBER("badgeNumber"),
    CARDHOLDER_ID("cardholderId"),
    PIN_CODE("pinCode"),
    UNKNOWN("unknown");

    final String serializedIdType;

    UserIdType(String serializedIdType) {
        this.serializedIdType = serializedIdType;
    }

    static UserIdType parse(String providedIdType) {
        return Arrays.stream(UserIdType.values())
                .filter(type -> providedIdType.equals(type.serializedIdType))
                .findFirst()
                .orElse(UNKNOWN);
    }

    @JsonValue
    private String getSerializedIdType() {
        return serializedIdType;
    }
}
