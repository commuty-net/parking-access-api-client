package net.commuty.http.request;

import net.commuty.model.AccessLog;

import java.util.Collection;

public class AccessLogRequest implements Requestable{

    private final Collection<AccessLog> accesses;

    public AccessLogRequest(Collection<AccessLog> accesses) {
        this.accesses = accesses;
    }

    public Collection<AccessLog> getAccesses() {
        return accesses;
    }
}
