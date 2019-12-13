package net.commuty.parking.model

import net.commuty.parking.configuration.JsonMapper
import spock.lang.Specification

import java.time.OffsetDateTime

import static net.commuty.parking.model.UserIdType.EMAIL
import static net.commuty.parking.model.UserIdType.PIN_CODE

class AccessRightSpec extends Specification {

    def mapper = JsonMapper.create()

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
        def right = mapper.read(new StringReader(json), AccessRight.class)

        then:
        right != null
        right.userIds.size() == 1
        right.userIds.first().id == "anonymised.19884@commuty.net"
        right.userIds.first().userIdType == EMAIL
        right.parkingSiteId == "d59b4606-cd94-4d1c-9a30-cfc3a4bf70f4"
        !right.granted
        right.startTime == OffsetDateTime.parse("2019-11-29T00:00:00+01:00")
        right.endTime == OffsetDateTime.parse("2019-11-30T00:00:00+01:00")

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
        def right = mapper.read(new StringReader(json), AccessRight.class)

        then:
        right != null
        right.userIds.size() == 2
        right.userIds.first().id == "anonymised.19884@commuty.net"
        right.userIds.first().userIdType == EMAIL
        right.userIds.last().id == "1234"
        right.userIds.last().userIdType == PIN_CODE
        right.parkingSiteId == "d59b4606-cd94-4d1c-9a30-cfc3a4bf70f4"
        right.granted
        right.startTime == OffsetDateTime.parse("2019-11-29T00:00:00+07:00")
        right.endTime == OffsetDateTime.parse("2019-11-30T00:00:00+07:00")
    }
}
