package net.commuty.parking


import net.commuty.parking.model.AccessLog
import net.commuty.parking.model.UserId

import java.time.LocalDateTime

import static java.net.HttpURLConnection.HTTP_OK
import static org.mockserver.model.HttpRequest.request
import static org.mockserver.model.HttpResponse.response

class ParkingAccessReportAccessLogSpec extends RestWithAuthSpec {

    def mockAccessLogRoute() {
        mockServer.when(
                request()
                    .withMethod("POST")
                    .withPath("/v2/parking-sites/.*/access-logs")
                    .withHeader("Authorization", "Bearer atoken")
        ).respond(
                response('{"logId": "a-valid-log-id"}')
                .withStatusCode(HTTP_OK)
        )
    }

    def """
        #reportAccessLog(null parkingSiteId, null Accesses)
        throws an error
        """() {
        given:
        def parkingSiteId = null
        def accesses = null

        when:
        parkingAccess.reportAccessLog(parkingSiteId, accesses)

        then:
        thrown(IllegalArgumentException)
    }

    def """
        #reportAccessLog(valid parkingSiteId, null Accesses)
        throws an error
        """() {
        given:
        def parkingSiteId = UUID.randomUUID().toString()
        def accesses = null

        when:
        parkingAccess.reportAccessLog(parkingSiteId, accesses)

        then:
        thrown(IllegalArgumentException)
    }

    def """
        #reportAccessLog(valid parkingSiteId, empty Accesses)
        throws an error
        """() {
        given:
        def parkingSiteId = UUID.randomUUID().toString()
        Collection<AccessLog> accesses = Collections.emptyList()

        when:
        parkingAccess.reportAccessLog(parkingSiteId, accesses)

        then:
        thrown(IllegalArgumentException)
    }

    def """
        #reportAccessLog(valid parkingSiteId, one valid access Log)
        has a correct body
        returns a valid log id
        """() {
        given:
        mockAccessLogRoute()
        def parkingSiteId = UUID.randomUUID().toString()
        def accessLog = AccessLog.createInAccessLog(UserId.fromLicensePlate("1-ABC-000"), LocalDateTime.of(2019, 10, 10, 13, 37, 0))
        Collection<AccessLog> accesses = Collections.singletonList(accessLog)

        when:
        def logId = parkingAccess.reportAccessLog(parkingSiteId, accesses)

        then:
        logId != null
        logId == "a-valid-log-id"
        mockServer.verify(
                request()
                    .withMethod("POST")
                    .withPath("/v2/parking-sites/${parkingSiteId}/access-logs")
        )

        def submittedRequests = mockServer.retrieveRecordedRequestsAndResponses(
                request()
                        .withMethod("POST")
                        .withPath("/v2/parking-sites/${parkingSiteId}/access-logs")
        )

        !submittedRequests.toList().empty
        with(reader.parseText(submittedRequests.first().httpRequest.bodyAsString)) {
            it.accesses != null
            it.accesses.size() == 1
            it.accesses.first().userId == "1-ABC-000"
            it.accesses.first().userIdType == "licensePlate"
            it.accesses.first().way == "in"
            it.accesses.first().at == "2019-10-10T13:37:00"
        }
    }
}
