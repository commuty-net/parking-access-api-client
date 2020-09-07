package net.commuty.parking.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>This indicates what is the type of the user identifier.</p>
 * <p>A parking user is known to Commuty via one or more identifiers. Each identifier has a specific type (i.e. the identifier is an email, a number plate,...)</p>
 * <p>If the identifier type is not known by the client (for instance when the client is used against a newer version of the api), the the type will be shown as {@link #UNKNOWN}.</p>
 */
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

    @JsonProperty("licensePlateWim26")
    LICENSE_PLATE_WIM26,

    @JsonProperty("licensePlateWim64")
    LICENSE_PLATE_WIM64,

    @JsonProperty("commutyExternalId")
    COMMUTY_EXTERNAL_ID,

    @JsonEnumDefaultValue
    @JsonProperty("unknown")
    UNKNOWN
}
