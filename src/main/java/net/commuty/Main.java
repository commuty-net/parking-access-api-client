package net.commuty;

import net.commuty.configuration.ClientBuilder;
import net.commuty.exception.HttpRequestException;
import net.commuty.model.AccessLog;
import net.commuty.model.UserId;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

public class Main {

    public static void main(String[] args) {
        ClientBuilder builder = ClientBuilder.create().withCredentials("test", "testtest").withHost("http://localhost:8080");
        ParkingAccess client = ParkingAccess.create(builder);
        try {
            Collection<AccessLog> logs = Arrays.asList(
                    AccessLog.createInAccessLog(UserId.fromBadgeNumber("123345"), LocalDateTime.now()),
                    AccessLog.createOutAccessLog(UserId.fromQrCode("toto"), LocalDateTime.now())
            );
            String logId = client.reportAccessLog("d59b4606-cd94-4d1c-9a30-cfc3a4bf70f4", logs);
            System.out.println(logId);

        } catch(HttpRequestException h) {
            h.printStackTrace();
            System.err.println(h.getErrorResponse());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
