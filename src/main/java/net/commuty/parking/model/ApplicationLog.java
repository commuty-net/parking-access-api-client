package net.commuty.parking.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static java.util.Optional.ofNullable;
import static net.commuty.parking.model.AccessDirection.IN;
import static net.commuty.parking.model.AccessDirection.OUT;

/**
 * <p>The "ApplicationLog" serves as a structured representation of log data emitted by client applications utilizing this API.</p>
 * <p>It plays a crucial role in providing valuable insights and facilitating effective monitoring and troubleshooting within the application ecosystem.</p>
 *
 * <p>Application logs hold significant importance as they offer key information to enhance developer experience and operational efficiency.</p>
 * <p>These logs are essential for various purposes, including but not limited to:</p>
 * <ul>
 *   <li>Integration Validation: Determining the successful completion of integrations, such as synchronization processes with external systems, like access control systems.</li>
 *   <li>Discrepancy Detection: Identifying discrepancies or inconsistencies between multiple systems, such as disparities in access control configurations or user identifiers.</li>
 *   <li>Real-time Status Reporting: Conveying real-time information about the operational status of the client application, enabling developers to respond promptly to emerging issues or opportunities for improvement.</li>
 * </ul>
 */
public class ApplicationLog {
    public static final int TYPE_MAX_SIZE = 30;
    public static final int MESSAGE_MAX_SIZE = 200;
    public static final int APPLICATION_ID_MAX_SIZE = 30;

    private final LocalDateTime timestamp;
    private final ApplicationLogLevel level;
    private final String type;
    private final String message;
    private final String applicationId;
    private final Map<String, Object> context;

    @JsonCreator
    public ApplicationLog(@JsonProperty("timestamp") LocalDateTime timestamp,
                          @JsonProperty("level") ApplicationLogLevel level,
                          @JsonProperty("type") String type,
                          @JsonProperty("message") String message,
                          @JsonProperty("applicationId") String applicationId,
                          @JsonProperty("context") Map<String, Object> context) {
        if (timestamp == null) {
            throw new IllegalArgumentException("timestamp cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("level cannot be null");
        }
        if (type == null || type.length() == 0) {
            throw new IllegalArgumentException("type cannot be blank");
        }
        if (message == null || message.length() == 0) {
            throw new IllegalArgumentException("message cannot be blank");
        }
        if (applicationId == null || applicationId.length() == 0) {
            throw new IllegalArgumentException("applicationId cannot be blank");
        }
        this.timestamp = timestamp;
        this.level = level;
        this.type = truncate(type, TYPE_MAX_SIZE);
        this.message = truncate(message, MESSAGE_MAX_SIZE);
        this.applicationId = truncate(applicationId, APPLICATION_ID_MAX_SIZE);
        this.context = context;
    }

    /**
     * @return The "timestamp" property denotes the precise date and time (in Coordinated Universal Time - UTC) when the application log event occurred.
     * This timestamp serves as a critical reference point for temporal analysis, ensuring accurate chronological sequencing of log entries.
     */
    @JsonProperty("timestamp")
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * @return The "level" property indicates the severity or significance of the application log entry.
     * It serves as a classification mechanism that helps prioritize and categorize log messages for effective monitoring and troubleshooting.
     * Other common levels (such as "debug" and "trace") are not included because this API is not intended to receive the entire set of logs of the client application. It only targets logs that must be shared with Commuty.
     */
    @JsonProperty("level")
    public ApplicationLogLevel getLevel() {
        return level;
    }

    /**
     * @return The "type" property serves as a categorization attribute within the application log entry, providing a high-level classification of the log message's purpose or nature.
     * It aids in organizing and distinguishing log entries based on their broader context.
     * While the "level" property indicates the severity of the log entry, the "type" property helps to further contextualize it.
     * Examples of `type`: `user-synchronization`, `synchronization-failure`, `synchrozization-completed`, `performance-issue`
     */
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    /**
     * @return The "message" property represents the core content of the application log entry.
     * It contains a detailed description or narrative of the event, providing valuable context, insights, or diagnostic information.
     * The "message" serves as the primary textual payload of the log entry, conveying the essence of the logged event.
     *
     * Key Considerations:
     *   * The "message" property should be informative and concise, aiming to communicate the essential details of the event or situation.
     *   * It is a fundamental component of the log entry, enabling developers and operators to understand the nature of the event and take appropriate actions.
     *   * While other properties, such as "level," "type," and "timestamp," provide structured metadata, the "message" property offers the narrative that completes the log entry, aiding in effective debugging, monitoring, and analysis.
     *
     * Developers are encouraged to craft informative and context-rich messages within their log entries, as these messages play a pivotal role in enhancing the overall log readability and utility.
     */
    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    /**
     * @return The "applicationId" property serves as a unique identifier associated with the client application generating the log entry.
     * It plays a pivotal role in distinguishing log entries originating from different applications within the ecosystem.
     * This identifier is valuable for tracking and attributing log events to specific application instances.
     */
    @JsonProperty("applicationId")
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * @return The "context" property provides a flexible mechanism for including additional information, metadata, or contextual details relevant to the logged event.
     * It serves as an extensible container for supplementary data, allowing developers to enrich log entries with custom key-value pairs or structured information that enhances the comprehensiveness of the log record.
     * This is optional.
     */
    public Map<String, Object> getContext() {
        return context;
    }

    private static String truncate(String text, int length) {
        if (text == null || text.length() <= length) {
            return text;
        } else {
            return text.substring(0, length);
        }
    }

    @Override
    public String toString() {
        return "ApplicationLog{" +
                "timestamp=" + timestamp +
                ", level=" + level +
                ", type='" + type + '\'' +
                ", message='" + message + '\'' +
                ", applicationId='" + applicationId + '\'' +
                ", context=" + context +
                '}';
    }
}
