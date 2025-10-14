package net.commuty.parking.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Map;

import static net.commuty.parking.model.AccessDirection.IN;
import static net.commuty.parking.model.AccessDirection.OUT;

/**
 * <p>This corresponds to a user that entered or exited a parking site at a point in time.</p>
 *
 * <p>You can construct this entity using the {@link #createInAccessLog(UserId, LocalDateTime)} or
 * {@link #createOutAccessLog(UserId, LocalDateTime)} methods, depending on the type of access log you want to report.</p>
 */
public class AccessLog {

    private final String userId;

    private final UserIdType userIdType;

    private final AccessDirection way;

    private final LocalDateTime at;

    private final boolean granted;

    private final String identificationMethod;

    private final String identificationValue;

    private final String reason;

    private final Map<String, Object> attributes;

    @JsonCreator
    public AccessLog(@JsonProperty("userId") String userId,
                     @JsonProperty("userIdType") UserIdType userIdType,
                     @JsonProperty("way") AccessDirection way,
                     @JsonProperty("at") LocalDateTime at,
                     @JsonProperty("granted") boolean granted,
                     @JsonProperty("identificationMethod") String identificationMethod,
                     @JsonProperty("identificationValue") String identificationValue,
                     @JsonProperty("reason") String reason,
                     @JsonProperty("attributes") Map<String, Object> attributes) {
        if (at == null) {
            throw new IllegalArgumentException("Log date cannot be null");
        }
        this.userId = userId;
        this.userIdType = userIdType;
        this.way = way;
        this.at = at;
        this.granted = granted;
        this.identificationMethod = identificationMethod;
        this.identificationValue = identificationValue;
        this.reason = reason;
        this.attributes = attributes;
    }

    public AccessLog(String userId,
                     UserIdType userIdType,
                     AccessDirection way,
                     LocalDateTime at) {
        this(userId, userIdType, way, at, true, null, null, null, null);
    }

    /**
     * Create a report for a user that entered the parking site at the specified time.
     * @param userId The {@link UserId} concerned by the access log. Cannot be null.
     * @param at The moment when the user entered the parking site, in UTC. Cannot be null.
     * @return the {@link AccessLog} entity.
     */
    public static AccessLog createInAccessLog(UserId userId, LocalDateTime at) {
        return createInAccessLog(userId, at, true, null, null, null, null);
    }

    /**
     * Create a report for a user that exited the parking site at the specified time.
     * @param userId The {@link UserId} concerned by the access log. Cannot be null.
     * @param at The moment when the user exited the parking site, in UTC. Cannot be null.
     * @return the {@link AccessLog} entity.
     */
    public static AccessLog createOutAccessLog(UserId userId, LocalDateTime at) {
        return createOutAccessLog(userId, at, true, null, null, null, null);
    }

    /**
     * Create a report for a user that entered the parking site at the specified time.
     * @param userId The {@link UserId} concerned by the access log. This value is used internally by Commuty to identify the individual, and it does not need to match the specific identification method used by the Access Control system (such as ANPR or badge scanning). Cannot be null.
     * @param at The moment when the user entered the parking site, in UTC. Cannot be null.
     * @param granted Whether or not the user was allowed to enter the parking or not;
     * @param identificationMethod Defines how a person or vehicle is identified when entering or exiting a parking facility. This attribute records the method through which the system recognizes or verifies the identity of the individual or vehicle to grant access. This attribute plays a critical role in tracking the method of identification for logging purposes and can help differentiate between various access mechanisms, such as license plate recognition, badge scans, or QR code usage. For example: "qr-code", "nedap-nvite", "anpr", "badge", etc. This can be null.
     * @param identificationValue Defines the specific value used in conjunction with the 'identificationMethod' to identify a person or vehicle during entry or exit. This value captures the actual data used by the system to verify the identity, such as a license plate number, badge ID, or QR code. This attribute serves as a log for the value that was processed during identification and is useful for logging, auditing, and debugging purposes. It does not store personal information or indicate who the person is, but it logs the data recognized by the system.
     * @param reason reason to specify why the user has been authorized (or not) to enter. This also can be used as a free-text field for ad-hoc comments.
     * @param attributes additional attributes providing supplementary details about the access log. This property will be serialized as a JSON object. Supports a null value.
     * @return the {@link AccessLog} entity.
     */
    public static AccessLog createInAccessLog(UserId userId, LocalDateTime at, boolean granted, String identificationMethod, String identificationValue, String reason, Map<String, Object> attributes) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        return new AccessLog(userId.getId(), userId.getType(), IN, at, granted, identificationMethod, identificationValue, reason, attributes);
    }

    /**
     * Create a report for a user that exited the parking site at the specified time.
     * @param userId The {@link UserId} concerned by the access log. This value is used internally by Commuty to identify the individual, and it does not need to match the specific identification method used by the Access Control system (such as ANPR or badge scanning). Cannot be null.
     * @param at The moment when the user exited the parking site, in UTC. Cannot be null.
     * @param granted Whether or not the user was allowed to enter the parking or not;
     * @param identificationMethod Defines how a person or vehicle is identified when entering or exiting a parking facility. This attribute records the method through which the system recognizes or verifies the identity of the individual or vehicle to grant access. This attribute plays a critical role in tracking the method of identification for logging purposes and can help differentiate between various access mechanisms, such as license plate recognition, badge scans, or QR code usage. For example: "qr-code", "nedap-nvite", "anpr", "badge", etc. This can be null.
     * @param identificationValue Defines the specific value used in conjunction with the 'identificationMethod' to identify a person or vehicle during entry or exit. This value captures the actual data used by the system to verify the identity, such as a license plate number, badge ID, or QR code. This attribute serves as a log for the value that was processed during identification and is useful for logging, auditing, and debugging purposes. It does not store personal information or indicate who the person is, but it logs the data recognized by the system.
     * @param reason reason to specify why the user has been authorized (or not) to exit. This also can be used as a free-text field for ad-hoc comments.
     * @param attributes additional attributes providing supplementary details about the access log. This property will be serialized as a JSON object. Supports a null value.
     * @return the {@link AccessLog} entity.
     */
    public static AccessLog createOutAccessLog(UserId userId, LocalDateTime at, boolean granted, String identificationMethod, String identificationValue, String reason, Map<String, Object> attributes) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        return new AccessLog(userId.getId(), userId.getType(), OUT, at, granted, identificationMethod, identificationValue, reason, attributes);
    }

    /**
     * Represents the unique identifier of the user who entered or exited the parking facility.
     * This identifier is linked with the {@link #getUserIdType()} property, which specifies the
     * type of identifier being used (such as an email or another unique identifier).
     *
     * This value is used internally by Commuty to identify the individual, and it does not need to
     * match the specific identification method used by the Access Control system (such as ANPR
     * or badge scanning). For example, even if the person entered using a badge or license plate
     * recognition, the userId might still be their email or another identifier used by Commuty
     * for user management.
     *
     * This flexibility allows the system to map various access methods to a consistent identifier,
     * regardless of how the person physically entered or exited the parking facility.
     *
     * Example:
     * - "john.doe@example.com" when the user is identified by their email
     */
    @JsonProperty("userId")
    public String getUserId() {
        return userId;
    }

    /**
     * Specifies the type of identifier used in the {@link #getUserId()} property to identify the
     * person who entered or exited the parking facility.
     *
     * This value indicates the nature of the identifier used, such as whether it's an email,
     * employee ID, or other forms of user identification. While the Access Control system may use
     * other methods (like ANPR or badge scanning) to permit entry or exit, this field allows
     * Commuty to store and track a consistent user identifier, which can be of any type based on
     * the context.
     *
     * Example {@link UserIdType} values could include:
     * - `EMAIL` if the `userId` is an email address
     * - `EMPLOYEE_ID` if the `userId` refers to an internal employee ID
     *
     * This property ensures that different types of user identifiers are tracked in a structured
     * way, allowing for consistent logging and auditing of users regardless of their access method.
     */
    @JsonProperty("userIdType")
    public UserIdType getUserIdType() {
        return userIdType;
    }

    /**
     * The direction (either {@link AccessDirection#IN} or {@link AccessDirection#OUT}) of the user entering/exiting the parking site.
     */
    @JsonProperty("way")
    public AccessDirection getWay() {
        return way;
    }

    /**
     * The moment when the user entered/exited the parking site.
     */
    @JsonProperty("at")
    public LocalDateTime getAt() {
        return at;
    }

    /**
     * Whether or not the user has been granted to enter/exit the parking.
     */
    @JsonProperty("granted")
    public boolean isGranted() {
        return granted;
    }

    /**
     * An optional reason to specify why the user has been authorized (or not) to enter. This also can be used as a free-text field for ad-hoc comments.
     */
    @JsonProperty("reason")
    public String getReason() {
        return reason;
    }

    /**
     * Specifies how a person or vehicle is identified when entering or exiting a parking facility.
     * This attribute indicates the method used by the system to recognize or verify the identity
     * of the individual or vehicle for access control.
     *
     * It is used to log the specific mechanism employed during entry or exit, such as license plate
     * recognition, badge scanning, or QR code scanning. Tracking this method allows for a clear
     * understanding of how access was granted and helps differentiate between the various access
     * methods available.
     *
     * Example valid values include:
     * - "qr-code" for QR code scanning
     * - "nedap-nvite" for specific systems like Nedap NVITE
     * - "anpr" for Automatic Number Plate Recognition
     * - "badge" for access via a badge reader
     *
     * This attribute is essential for logging and auditing purposes, but it can be null if the
     * method of identification is not applicable or unavailable.
     */
    @JsonProperty("identificationMethod")
    public String getIdentificationMethod() {
        return identificationMethod;
    }

    /**
     * Represents the specific value used in conjunction with the 'identificationMethod' to identify
     * a person or vehicle during entry or exit. This value captures the actual data used by the
     * system to verify the identity, such as a license plate number, badge ID, or QR code.
     *
     * This attribute serves as a log for the value that was processed during identification and is
     * useful for logging, auditing, and debugging purposes. It does not store personal information
     * or indicate who the person is, but it logs the data recognized by the system.
     *
     * - Example: "1AAA000" when 'identificationMethod' is "anpr" (Automatic Number Plate Recognition)
     * - Example: "04 A2 58 1B 93 6F D4" when 'identificationMethod' is "badge" (a badge's unique ID)
     *
     * Important: If 'identificationMethod' is not specified or is null, this attribute is ignored
     * as it cannot be processed independently.
     *
     * This attribute can also be null if no identification value is captured or applicable.
     *
     */
    @JsonProperty("identificationValue")
    public String getIdentificationValue() {
        return identificationValue;
    }

    /**
     * Represent additional attributes of an Access Log. It provides a flexible mechanism for including additional information, metadata, or contextual details.
     * It serves as an extensible container for supplementary data, allowing developers to enrich the access log with structured information that enhances its comprehensiveness.
     * This is optional.
     */
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "AccessLog{" +
                "userId='" + userId + '\'' +
                ", userIdType=" + userIdType +
                ", way=" + way +
                ", at=" + at +
                ", granted=" + granted +
                ", identificationMethod=" + identificationMethod +
                ", reason='" + reason + '\'' +
                '}';
    }
}
