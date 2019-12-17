package net.commuty.parking


import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset

import static java.net.HttpURLConnection.HTTP_OK
import static net.commuty.parking.model.UserIdType.BADGE_NUMBER
import static net.commuty.parking.model.UserIdType.EMAIL
import static org.mockserver.model.HttpRequest.request
import static org.mockserver.model.HttpResponse.response
import static org.mockserver.model.NottableString.not

class ParkingAccessListRightsSpec extends RestWithAuthSpec {

    def mockDefaultListRightRoute() {
        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/v2/access-rights")
                        .withHeader("Authorization", token)
        ).respond(
                response()
                        .withBody('{"accessRights": []}')
                        .withStatusCode(HTTP_OK)
        )
    }

    def """
        #listAccessRightsForToday()
        no results are expected
        returns an empty list
        """() {
        given:
        mockDefaultListRightRoute()

        when:
        def accesses = parkingAccess.listAccessRightsForToday()

        then:
        accesses != null
        accesses.isEmpty()
        mockServer.verify(
                request()
                .withMethod("GET")
                .withPath("/v2/access-rights")
        )
    }

    def """
        #listAccessRightsForToday()
        one result is expected
        returns one element parsed correctly
        """() {
        given:
        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/v2/access-rights")
                        .withHeader("Authorization", token)
        ).respond(
                response()
                        .withBody("""
                                        {
                                            "accessRights": [
                                                {
                                                  "userIds": [
                                                    {
                                                      "id": "anonymised.19884@commuty.net",
                                                      "type": "email"
                                                    },{
                                                      "id": "123456",
                                                      "type": "badgeNumber"
                                                    }
                                                  ],
                                                  "parkingSiteId": "d59b4606-cd94-4d1c-9a30-cfc3a4bf70f4",
                                                  "granted": false,
                                                  "startTime": "2019-11-29T00:00:00+01:00",
                                                  "endTime": "2019-11-30T00:00:00+01:00"
                                                }
                                            ]
                                        }
                                        """)
                        .withStatusCode(HTTP_OK)
        )

        when:
        def accesses = parkingAccess.listAccessRightsForToday()

        then:
        accesses != null
        accesses.size() == 1
        with(accesses.first()) {
            userIds.size() == 2
            userIds.first().userIdType == EMAIL
            userIds.first().id == "anonymised.19884@commuty.net"
            userIds.last().userIdType == BADGE_NUMBER
            userIds.last().id == "123456"
            parkingSiteId == "d59b4606-cd94-4d1c-9a30-cfc3a4bf70f4"
            !granted
            startTime == OffsetDateTime.of(2019, 11, 29, 0,0,0,0, ZoneOffset.ofHours(1))
            endTime == OffsetDateTime.of(2019, 11, 30, 0,0,0,0, ZoneOffset.ofHours(1))
        }
        mockServer.verify(
                request()
                        .withMethod("GET")
                        .withPath("/v2/access-rights")
        )
    }

    def """
        #listAccessRightsForToday()
        two results are expected
        returns 2 elements
        """() {
        given:
        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/v2/access-rights")
                        .withHeader("Authorization", token)
        ).respond(
                response()
                        .withBody("""
                                        {
                                            "accessRights": [
                                                {
                                                  "userIds": [{"id": "anonymised.19884@commuty.net", "type": "email" }],
                                                  "parkingSiteId": "d59b4606-cd94-4d1c-9a30-cfc3a4bf70f4",
                                                  "granted": false,
                                                  "startTime": "2019-11-29T00:00:00+01:00",
                                                  "endTime": "2019-11-30T00:00:00+01:00"
                                                },
                                                {
                                                  "userIds": [{"id": "anonymised.20012@commuty.net", "type": "email" }],
                                                  "parkingSiteId": "d59b4606-cd94-4d1c-9a30-cfc3a4bf70f4",
                                                  "granted": true,
                                                  "startTime": "2019-11-29T00:00:00+01:00",
                                                  "endTime": "2019-11-30T00:00:00+01:00"
                                                }
                                            ]
                                        }
                                        """)
                        .withStatusCode(HTTP_OK)
        )

        when:
        def accesses = parkingAccess.listAccessRightsForToday()

        then:
        accesses != null
        accesses.size() == 2
        mockServer.verify(
                request()
                        .withMethod("GET")
                        .withPath("/v2/access-rights")
        )
    }

    def """
        #listAccessRightsForToday(unreadOnly = true)
        query accesses for today with unreadOnly = true
        the query is correct
        """() {
        given:
        def unreadOnly = true
        mockDefaultListRightRoute()

        when:
        parkingAccess.listAccessRightsForToday(unreadOnly)

        then:
        mockServer.verify(
                request()
                .withMethod("GET")
                .withPath("/v2/access-rights")
                .withQueryStringParameter("unreadOnly", "true")
                .withQueryStringParameter(not("day"))
        )
    }

    def """
        #listAccessRightsForToday(unreadOnly = false)
        query accesses for today with unreadOnly = false
        the query is correct
        """() {
        given:
        def unreadOnly = false
        mockDefaultListRightRoute()

        when:
        parkingAccess.listAccessRightsForToday(unreadOnly)

        then:
        mockServer.verify(
                request()
                        .withMethod("GET")
                        .withPath("/v2/access-rights")
                        .withQueryStringParameter("unreadOnly", "false")
                        .withQueryStringParameter(not("day"))
        )
    }

    def """
        #listAccessRights(null date, null boolean)
        query all accesses for today
        the query is correct
        """() {
        given:
        def unreadOnly = null
        def day = null
        mockDefaultListRightRoute()

        when:
        parkingAccess.listAccessRights(day, unreadOnly)

        then:
        mockServer.verify(
                request()
                        .withMethod("GET")
                        .withPath("/v2/access-rights")
                        .withQueryStringParameter(not("unreadOnly"))
                        .withQueryStringParameter(not("day"))
        )
    }

    def """
        #listAccessRights(a valid date, null boolean)
        query all accesses for a specific date
        the query is correct
        """() {
        given:
        def unreadOnly = null
        def day = LocalDate.of(2019,10,21)
        mockDefaultListRightRoute()

        when:
        parkingAccess.listAccessRights(day, unreadOnly)

        then:
        mockServer.verify(
                request()
                        .withMethod("GET")
                        .withPath("/v2/access-rights")
                        .withQueryStringParameter(not("unreadOnly"))
                        .withQueryStringParameter("day", "2019-10-21")
        )
    }

    def """
        #listAccessRights(null date, unreadOnly = true)
        query unread accesses for today
        the query is correct
        """() {
        given:
        def unreadOnly = true
        def day = null
        mockDefaultListRightRoute()

        when:
        parkingAccess.listAccessRights(day, unreadOnly)

        then:
        mockServer.verify(
                request()
                        .withMethod("GET")
                        .withPath("/v2/access-rights")
                        .withQueryStringParameter("unreadOnly", "true")
                        .withQueryStringParameter(not("day"))
        )
    }

    def """
        #listAccessRights(a valid date, unreadOnly = false)
        query all accesses for a specific day
        the query is correct
        """() {
        given:
        def unreadOnly = false
        def day = LocalDate.of(2019,10,21)
        mockDefaultListRightRoute()

        when:
        parkingAccess.listAccessRights(day, unreadOnly)

        then:
        mockServer.verify(
                request()
                        .withMethod("GET")
                        .withPath("/v2/access-rights")
                        .withQueryStringParameter("unreadOnly", "false")
                        .withQueryStringParameter("day", "2019-10-21")
        )
    }
}
