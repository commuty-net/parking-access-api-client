package net.commuty.parking.model

import com.fasterxml.jackson.core.type.TypeReference
import net.commuty.parking.rest.JsonMapperTest
import spock.lang.Specification

import static java.nio.charset.Charset.defaultCharset
import static org.apache.commons.io.IOUtils.toInputStream

class ParkingSpotSpec extends Specification {

    def mapper = JsonMapperTest.create()

    def """parsing a list of spots works fine"""() {
        given:
        def json = """
                [
                    {
                      "id": "865aed8c-96a6-45b3-a9ff-646a1f8dc549",
                      "name": "-2/44",
                      "displayName": "Charleroi -2/44",
                      "evCharger": false,
                      "visitorSpot": false,
                      "zoneId": "4fcc53d1-01db-4cea-ada5-6f4ec91b4100",
                      "forCarpoolersOnly": false,
                      "forDisabled": true,
                      "large": true
                    },
                    {
                      "id": "869f4520-943e-40de-a38d-5b3d7be5bdb3",
                      "name": "-2/31",
                      "displayName": "Charleroi -2/31",
                      "evCharger": true,
                      "visitorSpot": false,
                      "zoneId": "4fcc53d1-01db-4cea-ada5-6f4ec91b4100",
                      "forCarpoolersOnly": true,
                      "forDisabled": false,
                      "large": true
                    },
                    {
                      "id": "87437c97-ff2e-44cb-948f-46570ea0d63f",
                      "name": "-2/17",
                      "displayName": "Charleroi -2/17",
                      "evCharger": false,
                      "visitorSpot": true,
                      "zoneId": "ccd93770-da4c-49d3-bc68-a196f96c1e41",
                      "forCarpoolersOnly": false,
                      "forDisabled": false,
                      "large": false
                    }
                ]
        """

        when:
        List<ParkingSpot> spots = mapper.mapper.readValue(toInputStream(json, defaultCharset()), new TypeReference<List<ParkingSpot>>(){})

        then:
        spots != null
        spots.size() == 3
        with(spots.get(0)) {
            id == UUID.fromString("865aed8c-96a6-45b3-a9ff-646a1f8dc549")
            name == "-2/44"
            displayName == "Charleroi -2/44"
            zoneId.toString() == "4fcc53d1-01db-4cea-ada5-6f4ec91b4100"
            !evCharger
            !visitorSpot
            !forCarpoolersOnly
            forDisabled
            large
        }
        with(spots.get(1)) {
            id == UUID.fromString("869f4520-943e-40de-a38d-5b3d7be5bdb3")
            name == "-2/31"
            displayName == "Charleroi -2/31"
            zoneId.toString() == "4fcc53d1-01db-4cea-ada5-6f4ec91b4100"
            evCharger
            !visitorSpot
            forCarpoolersOnly
            !forDisabled
            large
        }
        with(spots.get(2)) {
            id == UUID.fromString("87437c97-ff2e-44cb-948f-46570ea0d63f")
            name == "-2/17"
            displayName == "Charleroi -2/17"
            zoneId.toString() == "ccd93770-da4c-49d3-bc68-a196f96c1e41"
            !evCharger
            visitorSpot
            !forCarpoolersOnly
            !forDisabled
            !large
        }
    }
}
