package net.commuty.parking.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static net.commuty.parking.model.UserIdType.*;

/**
 * This identifies a user via one {@link UserIdType} and one identifier value.<br />
 * A parking user is known in Commuty via one or more {@link UserId}. Each user id has a type of identifier (email, license plate,...) and the identification value.<br />
 * For instance, a correct user id could be:
 * <ul>
 *     <li>type: {@link UserIdType#EMAIL}</li>
 *     <li>id: parking-user@someorg.com</li>
 * </ul>
 */
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

    /**
     * Creates a user id based on an email.
     */
    public static UserId fromEmail(String email) {
        return new UserId(EMAIL, email);
    }

    /**
     * Creates a user id based on a license plate.
     */
    public static UserId fromLicensePlate(String licensePlate) {
        return new UserId(LICENSE_PLATE, licensePlate);
    }

    /**
     * Creates a user id based on an identification number
     */
    public static UserId fromIdentificationNumber(String identificationNumber) {
        return new UserId(UserIdType.IDENTIFICATION_NUMBER, identificationNumber);
    }

    /**
     * Creates a user id based on a generated qr code.
     */
    public static UserId fromQrCode(String qrCode) {
        return new UserId(QR_CODE, qrCode);
    }

    /**
     * Creates a user id based on a badge number.
     */
    public static UserId fromBadgeNumber(String badgeNumber) {
        return new UserId(BADGE_NUMBER, badgeNumber);
    }

    /**
     * Creates a user id based on a cardholder identifier.
     */
    public static UserId fromCardholderId(String cardholderId) {
        return new UserId(CARDHOLDER_ID, cardholderId);
    }

    /**
     * Creates a user id based on a pin code.
     */
    public static UserId fromPinCode(String pinCode) {
        return new UserId(PIN_CODE, pinCode);
    }

    /**
     * Creates a user id based on a hash of a license plate using the Wiegand 26 bit protocol.<br />
     * This is compatible with the "Wiegand Interface Module" device from Nedap.<br />
     * For more information, <a href="https://www.nedapidentification.com/products/anpr/wiegand-interface-module/">Click here to see the Wiegand Interface Module website.</a>
     */
    public static UserId fromWim26EncodedLicensePlate(String win26EncodedLicensePlate) {
        return new UserId(LICENSE_PLATE_WIM26, win26EncodedLicensePlate);
    }

    /**
     * Creates a user id based on a hash of a license plate using the Wiegand 64 bit protocol.<br />
     * This is compatible with the "Wiegand Interface Module" device from Nedap.<br />
     * For more information, <a href="https://www.nedapidentification.com/products/anpr/wiegand-interface-module/">Click here to see the Wiegand Interface Module website.</a>
     */
    public static UserId fromWim64EncodedLicensePlate(String win64EncodedLicensePlate) {
        return new UserId(LICENSE_PLATE_WIM64, win64EncodedLicensePlate);
    }

    /**
     * The value of the identifier.
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * The identification type.
     */
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
