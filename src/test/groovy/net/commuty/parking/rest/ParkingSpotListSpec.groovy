package net.commuty.parking.rest


import static java.net.HttpURLConnection.HTTP_OK
import static java.util.UUID.randomUUID
import static org.mockserver.model.HttpRequest.request
import static org.mockserver.model.HttpResponse.response

class ParkingSpotListSpec extends RestWithAuthSpec {

    def mockParkingSpotsRoute(String jsonResponsePayload) {
        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/v2/parking-sites/.*/parking-spots")
                        .withHeader("Authorization", tokenHeader)
        ).respond(
                response()
                        .withBody(jsonResponsePayload)
                        .withStatusCode(HTTP_OK)
        )
    }

    def """
        listParkingSpots()
        no results are expected
        returns an empty list
        """() {
        given:
        def parkingSiteId = randomUUID().toString()
        mockParkingSpotsRoute("""
            {
              "parkingSpots": []
            }
        """)

        when:
        def spots = parkingAccess.listParkingSpots(parkingSiteId)

        then:
        spots != null
        spots.isEmpty()
    }

    def """
        listParkingSpots()
        three spots expected
        returns a list of three spots
        """() {
        given:
        def parkingSiteId = randomUUID().toString()
        mockParkingSpotsRoute("""
            {
              "parkingSpots": [
                    {
                      "id": "865aed8c-96a6-45b3-a9ff-646a1f8dc549",
                      "name": "-2/44",
                      "displayName": "Charleroi -2/44",
                      "evCharger": false,
                      "visitorSpot": false,
                      "forCarpoolersOnly": false,
                      "forDisabled": false,
                      "large": false,
                      "zoneId": "4fcc53d1-01db-4cea-ada5-6f4ec91b4100"
                    },
                    {
                      "id": "869f4520-943e-40de-a38d-5b3d7be5bdb3",
                      "name": "-2/31",
                      "displayName": "Charleroi -2/31",
                      "evCharger": true,
                      "visitorSpot": false,
                      "forCarpoolersOnly": false,
                      "forDisabled": true,
                      "large": false,
                      "zoneId": "4fcc53d1-01db-4cea-ada5-6f4ec91b4100"
                    },
                    {
                      "id": "87437c97-ff2e-44cb-948f-46570ea0d63f",
                      "name": "-2/17",
                      "displayName": "Charleroi -2/17",
                      "evCharger": false,
                      "visitorSpot": true,
                      "forCarpoolersOnly": true,
                      "forDisabled": false,
                      "large": true,
                      "zoneId": "5fcc53d1-01db-4cea-ada5-6f4ec91b4100"
                    }
                ]
            }
        """)

        when:
        def spots = parkingAccess.listParkingSpots(parkingSiteId)

        then:
        spots != null
        spots.size() == 3
        with(spots.get(0)) {
            id == UUID.fromString("865aed8c-96a6-45b3-a9ff-646a1f8dc549")
            name == "-2/44"
            displayName == "Charleroi -2/44"
            !evCharger
            !visitorSpot
            !forCarpoolersOnly
            !forDisabled
            !large
            zoneId == UUID.fromString("4fcc53d1-01db-4cea-ada5-6f4ec91b4100")
        }
        with(spots.get(1)) {
            id == UUID.fromString("869f4520-943e-40de-a38d-5b3d7be5bdb3")
            name == "-2/31"
            displayName == "Charleroi -2/31"
            evCharger
            !visitorSpot
            !forCarpoolersOnly
            forDisabled
            !large
            zoneId == UUID.fromString("4fcc53d1-01db-4cea-ada5-6f4ec91b4100")
        }
        with(spots.get(2)) {
            id == UUID.fromString("87437c97-ff2e-44cb-948f-46570ea0d63f")
            name == "-2/17"
            displayName == "Charleroi -2/17"
            !evCharger
            visitorSpot
            forCarpoolersOnly
            !forDisabled
            large
            zoneId == UUID.fromString("5fcc53d1-01db-4cea-ada5-6f4ec91b4100")
        }
    }
}
