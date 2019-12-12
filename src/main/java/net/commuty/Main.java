package net.commuty;

import net.commuty.configuration.ClientBuilder;
import net.commuty.model.AccessRight;

import java.time.LocalDate;
import java.util.Collection;

public class Main {

    public static void main(String[] args) {
        ClientBuilder builder = ClientBuilder.create().withCredentials("test", "testtest").withHost("http://localhost:8080");
        ParkingAccess client = ParkingAccess.create(builder);
        try {
            Collection<AccessRight> accesses = client.listAccessRights(LocalDate.of(2019, 11, 29), false);
            System.out.println(accesses.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
