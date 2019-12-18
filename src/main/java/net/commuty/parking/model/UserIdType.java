package net.commuty.parking.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum UserIdType {
    @JsonProperty("email")
    EMAIL,

    @JsonProperty("licensePlate")
    LICENSE_PLATE,

    @JsonProperty("identificationNumber")
    IDENTIFICATION_NUMBER,

    @JsonProperty("qrCode")
    QR_CODE,

    @JsonProperty("badgeNumber")
    BADGE_NUMBER,

    @JsonProperty("cardholderId")
    CARDHOLDER_ID,

    @JsonProperty("pinCode")
    PIN_CODE,

    @JsonEnumDefaultValue
    @JsonProperty("unknown")
    UNKNOWN;
// TODO remove?
//
//    private final String serializedIdType;
//
//    UserIdType(String serializedIdType) {
//        this.serializedIdType = serializedIdType;
//    }
//
//    static UserIdType parse(String providedIdType) {
//        return stream(UserIdType.values())
//                .filter(type -> providedIdType.equals(type.serializedIdType))
//                .findFirst()
//                .orElse(UNKNOWN);
//    }
//
//    @JsonValue
//    private String getSerializedIdType() {
//        return serializedIdType;
//    }
}
