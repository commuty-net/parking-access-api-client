package net.commuty.parking;

import net.commuty.parking.configuration.Client;
import net.commuty.parking.exception.HttpRequestException;
import net.commuty.parking.model.AccessLog;
import net.commuty.parking.model.UserId;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

public class Main {

    public static void main(String[] args) {
        Client client = Client.Builder.create().withCredentials("test", "testtest").withHost("http://localhost:8080").build();
        ParkingAccess api = ParkingAccess.create(client);
        try {
            Collection<AccessLog> logs = Arrays.asList(
                    AccessLog.createInAccessLog(UserId.fromBadgeNumber("123345"), LocalDateTime.now()),
                    AccessLog.createOutAccessLog(UserId.fromQrCode("toto"), LocalDateTime.now())
            );
            String logId = api.reportAccessLog("d59b4606-cd94-4d1c-9a30-cfc3a4bf70f4", logs);
            System.out.println(logId);

        } catch(HttpRequestException h) {
            h.printStackTrace();
            System.err.println(h.getErrorResponse());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
