package net.commuty.parking.http.request;

import net.commuty.parking.model.UserId;

public class VerificationRequest implements Requestable {

    private final UserId userId;

    public VerificationRequest(UserId userId) {
        this.userId = userId;
    }

    public UserId getUserId() {
        return userId;
    }
}
