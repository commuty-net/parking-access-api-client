package net.commuty.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import static net.commuty.model.UserIdType.*;

public class UserId {

    private final String id;

    private final String type;

    @JsonCreator
    private UserId(String type, String id) {
        this.type = type;
        this.id = id;
    }

    private UserId(UserIdType type, String id) {
        this.type = type.serializedIdType;
        this.id = id;
    }

    public static UserId fromEmail(String email) {
        return new UserId(EMAIL, email);
    }

    public static UserId fromLicensePlate(String licensePlate) {
        return new UserId(LICENSE_PLATE, licensePlate);
    }

    public static UserId fromIdentificationNumber(String identificationNumber) {
        return new UserId(IDENTIFICATION_NUMBER, identificationNumber);
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

    public String getId() {
        return id;
    }

    public UserIdType getUserIdType() {
        return UserIdType.parse(type);
    }

    @Override
    public String toString() {
        return "UserId{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
