package net.commuty.parking.http;

import java.io.Serializable;

/**
 * This may contain more information when a {@link HttpRequestException} occurs.
 */
public interface Error extends Serializable {
    /**
     * A readable message.
     */
    String getMessage();

    /**
     * A technical reason.
     */
    String getReason();
}
