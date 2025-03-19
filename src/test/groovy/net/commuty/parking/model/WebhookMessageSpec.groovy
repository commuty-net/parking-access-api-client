package net.commuty.parking.model

import net.commuty.parking.rest.JsonMapperTest
import spock.lang.Specification

import java.time.OffsetDateTime

import static java.nio.charset.Charset.defaultCharset
import static net.commuty.parking.model.AccessRightReason.NONE
import static net.commuty.parking.model.UserIdType.*
import static org.apache.commons.io.IOUtils.toInputStream

class WebhookMessageSpec extends Specification {

    def mapper = JsonMapperTest.create()

    def """
        parse a message {
            "operation": create
            "id": valid UUID
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
                "id": "95b813e7-53bd-47e0-b0fb-c07feda96191",
                "operation": "create",
                "accessRight": {
                    "userIds": [{
                        "id": "anonymised.19884@commuty.net",
                        "type": "email"
                    }],
                    "parkingSiteId": "d59b4606-cd94-4d1c-9a30-cfc3a4bf70f4",
                    "granted": false,
                    "startTime": "2019-11-29T00:00:00+01:00",
                    "endTime": "2019-11-30T00:00:00+01:00"
                }
            }
        """

        when:
        def message = mapper.read(toInputStream(json, defaultCharset()), WebhookMessage.class)

        then:
        message != null
        message.operation == WebhookMessageOperation.CREATE
        message.getId().toString() == "95b813e7-53bd-47e0-b0fb-c07feda96191"
        def right = message.accessRight

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
            "operation" is removal
            "id" is a valid UUID
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
                "id": "95b813e7-53bd-47e0-b0fb-c07feda96191",
                "operation": "remove",
                "accessRight": {
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
                }
            }
        """

        when:
        def message = mapper.read(toInputStream(json, defaultCharset()), WebhookMessage.class)

        then:
        message != null
        message.operation == WebhookMessageOperation.REMOVE
        message.getId().toString() == "95b813e7-53bd-47e0-b0fb-c07feda96191"
        def right = message.accessRight
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
            "operation" is "create"
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
                "id": "95b813e7-53bd-47e0-b0fb-c07feda96191",
                "operation": "create",
                "accessRight": {
                    "userIds": [{
                        "id": "unknownId",
                        "type": "unknownTypeId"
                    }],
                    "parkingSiteId": "d59b4606-cd94-4d1c-9a30-cfc3a4bf70f4",
                    "granted": false,
                    "startTime": "2019-11-29T00:00:00+07:00",
                    "endTime": "2019-11-30T00:00:00+07:00"
                }
            }
        """

        when:
        def message = mapper.read(toInputStream(json, defaultCharset()), WebhookMessage.class)

        then:
        message != null
        message.operation == WebhookMessageOperation.CREATE
        message.getId().toString() == "95b813e7-53bd-47e0-b0fb-c07feda96191"
        def right = message.accessRight
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
        right.parkingSpotId == null
        right.parkingSpotName == null
        right.parkingSpotDisplayName == null
        right.parkingSpotZoneId == null
        right.subjectId == null
        right.subjectLabel == null
        !right.isVisitor()
    }

    def """
        parse a message {
            "operation" is create
            "id" is a valid UUID
            "userIds": one valid userId
            "parkingSiteId": a valid string
            "startTime": a valid date
            "endTime": a valid date
            "granted": a boolean
            "attributes": {
                "reason": a valid reason,
                "id": a valid uuid,
                "parkingSpotId": a valid uuid,
                "parkingSpotName": a valid name,
                "parkingSpotDisplayName": a valid name,
                "parkingSpotZoneId": a valid uuid,
                "subjectId": a valid uuid,
                "subjectLabel": a valid string
                "isVisitor": a valid boolean
            }
        }
        is parsed correctly
        """() {
        given:
        def json = """
            {
                "id": "2696420b-1a2f-42c4-b9d5-2e05c5fc877c",
                "operation": "create",
                "accessRight": {
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
                        "id": "2696420b-1a2f-42c4-b9d5-2e05c5fc877c",
                        "parkingSpotId": "e71a3670-d1f7-4095-8e1d-a19003141411",
                        "parkingSpotName": "P1234",
                        "parkingSpotDisplayName": "Zone B",
                        "parkingSpotZoneId": "bdae073e-5c1b-4025-9739-f13f00682df0",
                        "subjectId": "80861212-0735-426b-8a53-fc4a63fe9cee",
                        "subjectLabel": "John Doe",
                        "isVisitor": true
                      },
                      "startTime": "2020-12-03T00:00:00+01:00",
                      "endTime": "2020-12-04T00:00:00+01:00"
                }
            }
        """

        when:
        def message = mapper.read(toInputStream(json, defaultCharset()), WebhookMessage.class)

        then:
        message != null
        message.operation == WebhookMessageOperation.CREATE
        message.getId().toString() == "2696420b-1a2f-42c4-b9d5-2e05c5fc877c"
        def right = message.accessRight
        right != null
        right.userIds.size() == 1
        right.userIds.first().id == "anonymous+17824@commuty.net"
        right.userIds.first().type == EMAIL
        right.parkingSiteId == "0f7af2c0-5d0d-4c7c-bdc0-469c3d47b345"
        !right.granted
        right.startTime == OffsetDateTime.parse("2020-12-03T00:00:00+01:00")
        right.endTime == OffsetDateTime.parse("2020-12-04T00:00:00+01:00")
        right.attributes.size() == 9

        right.reason == NONE
        right.id == UUID.fromString("2696420b-1a2f-42c4-b9d5-2e05c5fc877c")
        right.parkingSpotId ==  UUID.fromString("e71a3670-d1f7-4095-8e1d-a19003141411")
        right.parkingSpotName == "P1234"
        right.parkingSpotDisplayName == "Zone B"
        right.parkingSpotZoneId == UUID.fromString("bdae073e-5c1b-4025-9739-f13f00682df0")
        right.subjectId ==  UUID.fromString("80861212-0735-426b-8a53-fc4a63fe9cee")
        right.subjectLabel == "John Doe"
        right.isVisitor()
    }
}
