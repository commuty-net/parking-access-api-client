package net.commuty.parking.rest

import net.commuty.parking.http.CredentialsException
import net.commuty.parking.http.HttpClientException
import net.commuty.parking.http.HttpRequestException
import net.commuty.parking.model.AccessLog
import net.commuty.parking.model.UserId

import java.time.LocalDateTime

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST
import static java.net.HttpURLConnection.HTTP_CREATED
import static org.mockserver.model.HttpRequest.request
import static org.mockserver.model.HttpResponse.response

class ParkingAccessReportAccessLogSpec extends RestWithAuthSpec {

    def mockAccessLogRoute() {
        mockServer.when(
                request()
                    .withMethod("POST")
                    .withPath("/v2/parking-sites/.*/access-logs")
                    .withHeader("Authorization", tokenHeader)
        ).respond(
                response("""
                               {
                                    "logId": "a-valid-log-id"
                               }
                               """)
                .withStatusCode(HTTP_CREATED)
        )
    }

    def """
        reportAccessLog(null parkingSiteId, null Accesses)
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
        reportAccessLog(valid parkingSiteId, null Accesses)
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
        reportAccessLog(valid parkingSiteId, empty Accesses)
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
        reportAccessLog(valid parkingSiteId, one valid access Log)
        has a correct body
        returns a valid log id
        """() {
        given:
        mockAccessLogRoute()
        def parkingSiteId = UUID.randomUUID().toString()

        def reason = "QR Code read properly"
        def identificationMethod = "qr-Code"
        def identificationValue = "TEXT-UEYRU-789"
        def attributes = [readerName: "ENTRANCE-1", readerId: 1, valid: true]
        def accessLog = AccessLog.createInAccessLog(UserId.fromLicensePlate("1-ABC-000"), LocalDateTime.of(2019, 10, 10, 13, 37, 0), true, identificationMethod, identificationValue, reason, attributes)
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
            it.accesses.first().identificationMethod == identificationMethod
            it.accesses.first().identificationValue == identificationValue
            it.accesses.first().reason == reason
            it.accesses.first().attributes.readerName == "ENTRANCE-1"
            it.accesses.first().attributes.readerId == 1
            it.accesses.first().attributes.valid == true
        }
    }

    def """
        reportAccessLog(valid parkingSiteId, one valid access Log)
        has a correct body
        token is invalid then refreshed
        no exception is thrown and the final query has a http code 201
        """() {
        given:
        authReturnsExpiredTokenOnceThenValidToken()
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
                        .withHeader("Authorization", tokenHeader)
        )

        !submittedRequests.toList().empty
        submittedRequests.first().httpResponse.getStatusCode() == HTTP_CREATED
    }

    def """
        reportAccessLog(valid parkingSiteId, valid Accesses)
        credentials are invalid
        throws an error
        """() {
        given:
        def parkingSiteId = UUID.randomUUID().toString()
        def accessLog = AccessLog.createInAccessLog(UserId.fromLicensePlate("1-ABC-000"), LocalDateTime.of(2019, 10, 10, 13, 37, 0))
        Collection<AccessLog> accesses = Collections.singletonList(accessLog)
        authReturnsInvalidCredentials()

        when:
        parkingAccess.reportAccessLog(parkingSiteId, accesses)

        then:
        thrown(CredentialsException)
    }

    def """
        reportAccessLog(valid parkingSiteId, valid Accesses)
        api returns an error (broken api)
        throws an error
        """() {
        given:
        def parkingSiteId = UUID.randomUUID().toString()
        def accessLog = AccessLog.createInAccessLog(UserId.fromLicensePlate("1-ABC-000"), LocalDateTime.of(2019, 10, 10, 13, 37, 0))
        Collection<AccessLog> accesses = Collections.singletonList(accessLog)
        mockServer.when(
                request()
                        .withMethod("POST")
                        .withPath("/v2/parking-sites/.*/access-logs")
                        .withHeader("Authorization", tokenHeader)
        ).respond(
                response()
                        .withStatusCode(HTTP_BAD_REQUEST)
        )

        when:
        parkingAccess.reportAccessLog(parkingSiteId, accesses)

        then:
        thrown(HttpRequestException)
    }

    def """
        reportAccessLog(valid parkingSiteId, valid Accesses)
        api is not reachable
        throws an error
        """() {
        given:
        def parkingSiteId = UUID.randomUUID().toString()
        def accessLog = AccessLog.createInAccessLog(UserId.fromLicensePlate("1-ABC-000"), LocalDateTime.of(2019, 10, 10, 13, 37, 0))
        Collection<AccessLog> accesses = Collections.singletonList(accessLog)
        mockServer.stop()

        when:
        parkingAccess.reportAccessLog(parkingSiteId, accesses)

        then:
        thrown(HttpClientException)
    }
}
