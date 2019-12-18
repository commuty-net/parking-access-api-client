package net.commuty.parking.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static net.commuty.parking.model.UserIdType.*;

public class UserId {

    private final String id;

    private final UserIdType type;

    @JsonCreator
    UserId(@JsonProperty("type") UserIdType type,
           @JsonProperty("id") String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Identifier of the user cannot be null or blank");
        }
        this.type = type;
        this.id = id;
    }

    public static UserId fromEmail(String email) {
        return new UserId(EMAIL, email);
    }

    public static UserId fromLicensePlate(String licensePlate) {
        return new UserId(LICENSE_PLATE, licensePlate);
    }

    public static UserId fromIdentificationNumber(String identificationNumber) {
        return new UserId(UserIdType.IDENTIFICATION_NUMBER, identificationNumber);
    }

    public static UserId fromQrCode(String qrCode) {
        return new UserId(QR_CODE, qrCode);
    }

    public static UserId fromBadgeNumber(String badgeNumber) {
        return new UserId(BADGE_NUMBER, badgeNumber);
    }

    public static UserId fromCardholderId(String cardholderId) {
        return new UserId(CARDHOLDER_ID, cardholderId);
    }

    public static UserId fromPinCode(String pinCode) {
        return new UserId(PIN_CODE, pinCode);
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("type")
    public UserIdType getType() {
        return type;
    }

    public boolean hasType(UserIdType type) {
        return this.type == type;
    }

    @Override
    public String toString() {
        return "UserId{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
