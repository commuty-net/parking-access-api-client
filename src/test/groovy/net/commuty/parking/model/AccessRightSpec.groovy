package net.commuty.parking.model

import net.commuty.parking.rest.JsonMapperTest
import spock.lang.Specification

import java.time.OffsetDateTime

import static java.nio.charset.Charset.defaultCharset
import static net.commuty.parking.model.AccessRightReason.NONE
import static net.commuty.parking.model.UserIdType.*
import static org.apache.commons.io.IOUtils.toInputStream

class AccessRightSpec extends Specification {

    def mapper = JsonMapperTest.create()

    def """
        parse a message {
            "userIds": one valid userId
            "parkingSiteId": a valid string
            "startTime": a valid date
            "endTime": a valid date
            "granted": a boolean
        }
        is parsed correctly
        """() {
        given:
        def json = """
                {
                    "userIds": [{
                        "id": "anonymised.19884@commuty.net",
                        "type": "email"
                    }],
                    "parkingSiteId": "d59b4606-cd94-4d1c-9a30-cfc3a4bf70f4",
                    "granted": false,
                    "startTime": "2019-11-29T00:00:00+01:00",
                    "endTime": "2019-11-30T00:00:00+01:00"
                }"""

        when:
        def right = mapper.read(toInputStream(json, defaultCharset()), AccessRight.class)

        then:
        right != null
        right.userIds.size() == 1
        right.userIds.first().id == "anonymised.19884@commuty.net"
        right.userIds.first().type == EMAIL
        right.parkingSiteId == "d59b4606-cd94-4d1c-9a30-cfc3a4bf70f4"
        !right.granted
        right.startTime == OffsetDateTime.parse("2019-11-29T00:00:00+01:00")
        right.endTime == OffsetDateTime.parse("2019-11-30T00:00:00+01:00")
        right.attributes.isEmpty()
        right.id == null
        right.reason == null

    }

    def """
        parse a message {
            "userIds": two valid userIds
            "parkingSiteId": a valid string
            "startTime": a valid date
            "endTime": a valid date
            "granted": a boolean
        }
        is parsed correctly
        """() {
        given:
        def json = """
                {
                    "userIds": [{
                        "id": "anonymised.19884@commuty.net",
                        "type": "email"
                    },{
                        "id": "1234",
                        "type": "pinCode"
                    }],
                    "parkingSiteId": "d59b4606-cd94-4d1c-9a30-cfc3a4bf70f4",
                    "granted": true,
                    "startTime": "2019-11-29T00:00:00+07:00",
                    "endTime": "2019-11-30T00:00:00+07:00"
                }"""

        when:
        def right = mapper.read(toInputStream(json, defaultCharset()), AccessRight.class)

        then:
        right != null
        right.userIds.size() == 2
        right.userIds.first().id == "anonymised.19884@commuty.net"
        right.userIds.first().type == EMAIL
        right.userIds.last().id == "1234"
        right.userIds.last().type == PIN_CODE
        right.parkingSiteId == "d59b4606-cd94-4d1c-9a30-cfc3a4bf70f4"
        right.granted
        right.startTime == OffsetDateTime.parse("2019-11-29T00:00:00+07:00")
        right.endTime == OffsetDateTime.parse("2019-11-30T00:00:00+07:00")
        right.attributes.isEmpty()
        right.id == null
        right.reason == null
    }

    def """
        parse a message {
            "userIds": one valid userId from a unknown type in the library
            "parkingSiteId": a valid string
            "startTime": a valid date
            "endTime": a valid date
            "granted": a boolean
        }
        is parsed correctly
        """() {
        given:
        def json = """
                {
                    "userIds": [{
                        "id": "unknownId",
                        "type": "unknownTypeId"
                    }],
                    "parkingSiteId": "d59b4606-cd94-4d1c-9a30-cfc3a4bf70f4",
                    "granted": false,
                    "startTime": "2019-11-29T00:00:00+07:00",
                    "endTime": "2019-11-30T00:00:00+07:00"
                }"""

        when:
        def right = mapper.read(toInputStream(json, defaultCharset()), AccessRight.class)

        then:
        right != null
        right.userIds.size() == 1
        right.userIds.first().id == "unknownId"
        right.userIds.first().type == UNKNOWN
        right.parkingSiteId == "d59b4606-cd94-4d1c-9a30-cfc3a4bf70f4"
        !right.granted
        right.startTime == OffsetDateTime.parse("2019-11-29T00:00:00+07:00")
        right.endTime == OffsetDateTime.parse("2019-11-30T00:00:00+07:00")
        right.attributes.isEmpty()
        right.id == null
        right.reason == null
    }

    def """
        parse a message {
            "userIds": one valid userId
            "parkingSiteId": a valid string
            "startTime": a valid date
            "endTime": a valid date
            "granted": a boolean
            "attributes": {
                "reason": a valid reason,
                "id": a valid uuid
            }
        }
        is parsed correctly
        """() {
        given:
        def json = """
                {
                      "userIds": [
                        {
                          "id": "anonymous+17824@commuty.net",
                          "type": "email"
                        }
                      ],
                      "parkingSiteId": "0f7af2c0-5d0d-4c7c-bdc0-469c3d47b345",
                      "granted": false,
                      "attributes": {
                        "reason": "none",
                        "id": "2696420b-1a2f-42c4-b9d5-2e05c5fc877c"
                      },
                      "startTime": "2020-12-03T00:00:00+01:00",
                      "endTime": "2020-12-04T00:00:00+01:00"
                }"""

        when:
        def right = mapper.read(toInputStream(json, defaultCharset()), AccessRight.class)

        then:
        right != null
        right.userIds.size() == 1
        right.userIds.first().id == "anonymous+17824@commuty.net"
        right.userIds.first().type == EMAIL
        right.parkingSiteId == "0f7af2c0-5d0d-4c7c-bdc0-469c3d47b345"
        !right.granted
        right.startTime == OffsetDateTime.parse("2020-12-03T00:00:00+01:00")
        right.endTime == OffsetDateTime.parse("2020-12-04T00:00:00+01:00")
        right.attributes.size() == 2
        right.id == UUID.fromString("2696420b-1a2f-42c4-b9d5-2e05c5fc877c")
        right.reason == NONE
    }
}
