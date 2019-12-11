package net.commuty.model;

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

    protected final String serializedIdType;

    UserIdType(String serializedIdType) {
        this.serializedIdType = serializedIdType;
    }

    public static UserIdType parse(String providedIdType) {
        return Arrays.stream(UserIdType.values())
                .filter(type -> providedIdType.equals(type.serializedIdType))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
