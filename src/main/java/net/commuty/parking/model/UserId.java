package net.commuty.parking.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserId {

    private final String id;

    private final String type;

    @JsonCreator
    private UserId(String type, String id) {
        this.type = type;
        this.id = id;
    }

    private UserId(UserIdType type, String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Identifier of the user cannot be null or blank");
        }
        this.type = type.serializedIdType;
        this.id = id;
    }

    public static UserId fromEmail(String email) {
        return new UserId(UserIdType.EMAIL, email);
    }

    public static UserId fromLicensePlate(String licensePlate) {
        return new UserId(UserIdType.LICENSE_PLATE, licensePlate);
    }

    public static UserId fromIdentificationNumber(String identificationNumber) {
        return new UserId(UserIdType.IDENTIFICATION_NUMBER, identificationNumber);
    }

    public static UserId fromQrCode(String qrCode) {
        return new UserId(UserIdType.QR_CODE, qrCode);
    }

    public static UserId fromBadgeNumber(String badgeNumber) {
        return new UserId(UserIdType.BADGE_NUMBER, badgeNumber);
    }

    public static UserId fromCardholderId(String cardholderId) {
        return new UserId(UserIdType.CARDHOLDER_ID, cardholderId);
    }

    public static UserId fromPinCode(String pinCode) {
        return new UserId(UserIdType.PIN_CODE, pinCode);
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    @JsonIgnore
    public UserIdType getUserIdType() {
        return UserIdType.parse(type);
    }

    public boolean hasType(UserIdType type) {
        return getUserIdType() == type;
    }

    @Override
    public String toString() {
        return "UserId{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
