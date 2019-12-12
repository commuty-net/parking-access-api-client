package net.commuty.http.request;

import net.commuty.model.UserId;

public class VerificationRequest implements Requestable {

    private final UserId userId;

    public VerificationRequest(UserId userId) {
        this.userId = userId;
    }

    public UserId getUserId() {
        return userId;
    }
}
