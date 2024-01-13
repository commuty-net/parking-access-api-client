package net.commuty.parking.rest

import groovy.transform.CompileStatic
import net.commuty.parking.http.CredentialsException
import net.commuty.parking.http.HttpClientException
import net.commuty.parking.http.HttpRequestException

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST
import static java.net.HttpURLConnection.HTTP_OK
import static net.commuty.parking.model.AccessRightAttributeName.ID
import static net.commuty.parking.model.AccessRightAttributeName.REASON
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
                        .withHeader("Authorization", tokenHeader)
        ).respond(
                response()
                        .withBody('{"accessRights": []}')
                        .withStatusCode(HTTP_OK)
        )
    }

    def """
        listAccessRightsForToday()
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
        listAccessRightsForToday()
        one result is expected
        returns one element parsed correctly
        """() {
        given:
        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/v2/access-rights")
                        .withHeader("Authorization", tokenHeader)
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
                                                  "endTime": "2019-11-30T00:00:00+01:00",
                                                  "unknown": "parameter"
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
            userIds.first().type == EMAIL
            userIds.first().id == "anonymised.19884@commuty.net"
            userIds.last().type == BADGE_NUMBER
            userIds.last().id == "123456"
            parkingSiteId == "d59b4606-cd94-4d1c-9a30-cfc3a4bf70f4"
            !granted
            startTime == OffsetDateTime.of(2019, 11, 29, 0, 0, 0, 0, ZoneOffset.ofHours(1))
            endTime == OffsetDateTime.of(2019, 11, 30, 0, 0, 0, 0, ZoneOffset.ofHours(1))
        }
        mockServer.verify(
                request()
                        .withMethod("GET")
                        .withPath("/v2/access-rights")
        )
    }

    def """
        listAccessRightsForToday()
        two results are expected
        returns 2 elements
        """() {
        given:
        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/v2/access-rights")
                        .withHeader("Authorization", tokenHeader)
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
        listAccessRightsForToday(unreadOnly = true)
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
        )
    }

    def """
        listAccessRightsForToday(unreadOnly = false)
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
        )
    }

    def """
        listAccessRights(null date, null boolean)
        query all accesses for today
        the query is correct
        """() {
        given:
        def unreadOnly = null
        def day = null
        mockDefaultListRightRoute()

        when:
        parkingAccess.listAccessRights(day, unreadOnly, null, null, null, null, null, [ID] as Set)

        then:
        mockServer.verify(
                request()
                        .withMethod("GET")
                        .withPath("/v2/access-rights")
                        .withQueryStringParameter("includeAttributes", ID.getAttributeName())
        )
    }

    def """
        listAccessRights(a valid date, null boolean)
        query all accesses for a specific date
        the query is correct
        """() {
        given:
        def unreadOnly = null
        def day = LocalDate.of(2019, 10, 21)
        mockDefaultListRightRoute()

        when:
        parkingAccess.listAccessRights(day, unreadOnly, true, null, null, null, null, [REASON] as Set)

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
        listAccessRights(null date, unreadOnly = true)
        query unread accesses for today
        the query is correct
        """() {
        given:
        def unreadOnly = true
        def day = null
        mockDefaultListRightRoute()

        when:
        parkingAccess.listAccessRights(day, unreadOnly, false, null,null, null, null, [ID, REASON] as Set)

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
        listAccessRights(a valid date, unreadOnly = false)
        query all accesses for a specific day
        the query is correct
        """() {
        given:
        def unreadOnly = false
        def day = LocalDate.of(2019, 10, 21)
        mockDefaultListRightRoute()

        when:
        parkingAccess.listAccessRights(day, unreadOnly, true, null,null, null, null, [REASON] as Set)

        then:
        mockServer.verify(
                request()
                        .withMethod("GET")
                        .withPath("/v2/access-rights")
                        .withQueryStringParameter("unreadOnly", "false")
                        .withQueryStringParameter("day", "2019-10-21")
        )
    }

    def """
        listAccessRights(a valid date, unreadOnly = false, dryRun = true, createdAfter = a valid date, includeAttributes = ID, REASON)
        query all accesses for a specific day
        the query is correct
        """() {
        given:
        def unreadOnly = false
        def dryRun = true
        def createdAfter = LocalDateTime.of(2021, 2, 22, 13, 37, 42, 1234)
        def includeAttributes = [ID, REASON]
        def day = LocalDate.of(2019, 10, 21)
        mockDefaultListRightRoute()

        when:
        parkingAccess.listAccessRights(day, unreadOnly, dryRun, createdAfter, null, null, null, includeAttributes as Set)

        then:
        mockServer.verify(
                request()
                        .withMethod("GET")
                        .withPath("/v2/access-rights")
                        .withQueryStringParameter("unreadOnly", "false")
                        .withQueryStringParameter("day", "2019-10-21")
                        .withQueryStringParameter("dryRun", "true")
                        .withQueryStringParameter("createdAfter", "2021-02-22T13:37:42Z")
                        .withQueryStringParameter("includeAttributes", "id", "reason")
        )
    }

    def """
        listAccessRights(a valid date, unreadOnly = false, dryRun = false, createdAfter = null, granted = true, parkingSiteId = null includeAttributes = ID, REASON)
        query all accesses for a specific day
        the query is correct
        """() {
        given:
        def unreadOnly = false
        def dryRun = true
        def createdAfter = LocalDateTime.of(2021, 2, 22, 13, 37, 42, 1234)
        def includeAttributes = [ID, REASON]
        def day = LocalDate.of(2019, 10, 21)
        mockDefaultListRightRoute()

        when:
        parkingAccess.listAccessRights(day, unreadOnly, dryRun, createdAfter, true, null, null, includeAttributes as Set)

        then:
        mockServer.verify(
                request()
                        .withMethod("GET")
                        .withPath("/v2/access-rights")
                        .withQueryStringParameter("unreadOnly", "false")
                        .withQueryStringParameter("day", "2019-10-21")
                        .withQueryStringParameter("dryRun", "true")
                        .withQueryStringParameter("createdAfter", "2021-02-22T13:37:42Z")
                        .withQueryStringParameter("granted", "true")
                        .withQueryStringParameter("includeAttributes", "id", "reason")
        )
    }

    def """
        listAccessRights(a valid date, unreadOnly = false, dryRun = false, createdAfter = null, granted = false, parkingSiteId = 'acme', subjectId='ba164c2e-bdeb-4ded-bd8d-0bd455ed2a6e', includeAttributes = ID, REASON)
        query all accesses for a specific day
        the query is correct
        """() {
        given:
        def unreadOnly = false
        def dryRun = true
        def createdAfter = LocalDateTime.of(2021, 2, 22, 13, 37, 42, 1234)
        def includeAttributes = [ID, REASON]
        def day = LocalDate.of(2019, 10, 21)
        def parkingSiteId = "acme"
        def subjectId = UUID.fromString("ba164c2e-bdeb-4ded-bd8d-0bd455ed2a6e")
        mockDefaultListRightRoute()

        when:
        parkingAccess.listAccessRights(day, unreadOnly, dryRun, createdAfter, false, parkingSiteId, subjectId, includeAttributes as Set)

        then:
        mockServer.verify(
                request()
                        .withMethod("GET")
                        .withPath("/v2/access-rights")
                        .withQueryStringParameter("unreadOnly", "false")
                        .withQueryStringParameter("day", "2019-10-21")
                        .withQueryStringParameter("dryRun", "true")
                        .withQueryStringParameter("createdAfter", "2021-02-22T13:37:42Z")
                        .withQueryStringParameter("granted", "false")
                        .withQueryStringParameter("parkingSiteId", parkingSiteId)
                        .withQueryStringParameter("subjectId", subjectId.toString())
                        .withQueryStringParameter("includeAttributes", "id", "reason")
        )
    }

    def """
        listAccessRights(a valid date, unreadOnly = false)
        query all accesses for a specific day
        the token is invalid then refreshed
        no exception is thrown and the final query has a http code 200
        """() {
        given:
        authReturnsExpiredTokenOnceThenValidToken()
        def unreadOnly = false
        def day = LocalDate.of(2019, 10, 21)
        mockDefaultListRightRoute()

        when:
        parkingAccess.listAccessRights(day, unreadOnly, null, null, null, null, null, [ID] as Set)

        then:
        mockServer.verify(
                request()
                        .withMethod("GET")
                        .withPath("/v2/access-rights")
                        .withHeader("Authorization", tokenHeader)
        )
        def accessRightsRequest = mockServer.retrieveRecordedRequestsAndResponses(
                request()
                        .withMethod("GET")
                        .withPath("/v2/access-rights")
                        .withHeader("Authorization", tokenHeader)
        )
        accessRightsRequest.size() == 1
        accessRightsRequest.first().httpResponse.getStatusCode() == HTTP_OK
    }

    def """
        listAccessRights(a valid date, unreadOnly = false)
        credentials are invalid
        an exception is thrown
        """() {
        given:
        def unreadOnly = false
        def day = LocalDate.of(2019, 10, 21)
        authReturnsInvalidCredentials()

        when:
        parkingAccess.listAccessRights(day, unreadOnly, false, null, null, null, null, null)

        then:
        thrown(CredentialsException)
    }

    def """
        listAccessRights(a valid date, unreadOnly = false)
        api returns an error
        an exception is thrown
        """() {
        given:
        def unreadOnly = false
        def day = LocalDate.of(2019, 10, 21)
        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/v2/access-rights")
                        .withHeader("Authorization", tokenHeader)
        ).respond(
                response()
                        .withStatusCode(HTTP_BAD_REQUEST)
        )

        when:
        parkingAccess.listAccessRights(day, unreadOnly, false, null,null, null, null, [REASON, ID] as Set)

        then:
        thrown(HttpRequestException)
    }

    def """
        listAccessRights(a valid date, unreadOnly = false)
        api is not reachable
        an exception is thrown
        """() {
        given:
        def unreadOnly = false
        def day = LocalDate.of(2019, 10, 21)
        mockServer.stop()

        when:
        parkingAccess.listAccessRights(day, unreadOnly, false, null, null,null, null, null)

        then:
        thrown(HttpClientException)
    }
}
