package net.commuty.parking.http;

/**
 * This may contain more information when a {@link HttpRequestException} occurs.
 */
public interface Error {
    /**
     * A readable message.
     */
    String getMessage();

    /**
     * A technical reason.
     */
    String getReason();
}
