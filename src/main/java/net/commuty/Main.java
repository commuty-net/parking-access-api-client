package net.commuty;

import net.commuty.configuration.ClientBuilder;

public class Main {

    public static void main(String[] args) {
        ClientBuilder builder = ClientBuilder.create().withCredentials("test", "testtest").withHost("http://localhost:8080");
        ParkingAccess access = ParkingAccess.create(builder);
        try {
            access.authenticate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
