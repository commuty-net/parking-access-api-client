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
                      "visitorSpot": false
                    },
                    {
                      "id": "869f4520-943e-40de-a38d-5b3d7be5bdb3",
                      "name": "-2/31",
                      "displayName": "Charleroi -2/31",
                      "evCharger": true,
                      "visitorSpot": false
                    },
                    {
                      "id": "87437c97-ff2e-44cb-948f-46570ea0d63f",
                      "name": "-2/17",
                      "displayName": "Charleroi -2/17",
                      "evCharger": false,
                      "visitorSpot": true
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
            !evCharger
            !visitorSpot
        }
        with(spots.get(1)) {
            id == UUID.fromString("869f4520-943e-40de-a38d-5b3d7be5bdb3")
            name == "-2/31"
            displayName == "Charleroi -2/31"
            evCharger
            !visitorSpot
        }
        with(spots.get(2)) {
            id == UUID.fromString("87437c97-ff2e-44cb-948f-46570ea0d63f")
            name == "-2/17"
            displayName == "Charleroi -2/17"
            !evCharger
            visitorSpot
        }
    }
}
