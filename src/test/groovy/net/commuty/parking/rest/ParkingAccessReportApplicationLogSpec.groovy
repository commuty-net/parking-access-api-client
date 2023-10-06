package net.commuty.parking.rest

import net.commuty.parking.http.CredentialsException
import net.commuty.parking.http.HttpClientException
import net.commuty.parking.http.HttpRequestException
import net.commuty.parking.model.ApplicationLog
import net.commuty.parking.model.ApplicationLogLevel

import java.time.LocalDateTime

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST
import static java.net.HttpURLConnection.HTTP_CREATED
import static org.mockserver.model.HttpRequest.request
import static org.mockserver.model.HttpResponse.response

class ParkingAccessReportApplicationLogSpec extends RestWithAuthSpec {
    def mockApplicationLogRoute() {
        mockServer.when(
                request()
                        .withMethod("POST")
                        .withPath("/v2/application-logs")
                        .withHeader("Authorization", tokenHeader)
        ).respond(
                response("""
                               {
                                    "id": "6e7cf204-fc3f-4262-8f7f-48cadf34952b"
                               }
                               """)
                        .withStatusCode(HTTP_CREATED)
        )
    }

    def """
        reportApplicationLog(null log)
        throws an error
        """() {
        given:
        def log = null

        when:
        parkingAccess.reportApplicationLog(log)

        then:
        thrown(IllegalArgumentException)
    }

    def """
        reportApplicationLog(valid Log)
        has a correct body
        returns a valid log id
        """() {
        given:
        mockApplicationLogRoute()

        def type = "synchronization-failed"
        def message = "The synchronization is succesful"
        def applicationId = "client-app"

        def applicationLog = new ApplicationLog(
                LocalDateTime.of(2023, 10, 10, 13, 37, 0),
                ApplicationLogLevel.WARN,
                type,
                message,
                applicationId,
                [first: 1, second: 2, map: [request: null, integer: 32, string: "any-value"]]
        )

        when:
        def logId = parkingAccess.reportApplicationLog(applicationLog)

        then:
        logId != null
        logId == UUID.fromString("6e7cf204-fc3f-4262-8f7f-48cadf34952b")
        mockServer.verify(
                request()
                    .withMethod("POST")
                    .withPath("/v2/application-logs")
        )

        def submittedRequests = mockServer.retrieveRecordedRequestsAndResponses(
                request()
                        .withMethod("POST")
                        .withPath("/v2/application-logs")
        )

        !submittedRequests.toList().empty
        with(reader.parseText(submittedRequests.first().httpRequest.bodyAsString)) {
            it != null
            it.type == type
            it.message == message
            it.applicationId == applicationId
            it.timestamp == "2023-10-10T13:37:00"
            it.level == "warn"
            it.context["first"] == 1
            it.context["second"] == 2
            it.context["map"]["request"] == null
            it.context["map"]["integer"] == 32
            it.context["map"]["string"] == "any-value"
        }
    }

    def """ reportApplicationLog(valid Log) has a correct body token is invalid then refreshed no exception is thrown and the final query has a http code 201 """() {
        given:
        authReturnsExpiredTokenOnceThenValidToken()
        mockApplicationLogRoute()
        def type = "synchronization-failed"
        def message = "The synchronization is succesful"
        def applicationId = "client-app"

        def applicationLog = new ApplicationLog(
                LocalDateTime.of(2023, 10, 10, 13, 37, 0),
                ApplicationLogLevel.WARN,
                type,
                message,
                applicationId,
                Map.of("first", 1, "second", 2, "map", Map.of("request", "test", "integer", 32, "string", "any-value"))
        )

        mockServer.when(
                request()
                        .withMethod("POST")
                        .withPath("/v2/application-logs")
                        .withHeader("Authorization", tokenHeader)
        ).respond(
                response("""
                               {
                                   "id": "6e7cf204-fc3f-4262-8f7f-48cadf34952b"
                               }
                               """)
                        .withStatusCode(HTTP_CREATED)
        )

        when:
        def logId = parkingAccess.reportApplicationLog(applicationLog)

        then:
        logId != null
        logId == UUID.fromString("6e7cf204-fc3f-4262-8f7f-48cadf34952b")
        mockServer.verify(
                request()
                        .withMethod("POST")
                        .withPath("/v2/application-logs")
        )

        def submittedRequests = mockServer.retrieveRecordedRequestsAndResponses(
                request()
                        .withMethod("POST")
                        .withPath("/v2/application-logs")
                        .withHeader("Authorization", tokenHeader)
        )

        !submittedRequests.toList().empty
        submittedRequests.first().httpResponse.getStatusCode() == HTTP_CREATED
    }

    def """
        reportApplicationLog(valid Log)
        credentials are invalid
        throws an error
        """() {
        given:
        def type = "synchronization-failed"
        def message = "The synchronization is succesful"
        def applicationId = "client-app"

        def applicationLog = new ApplicationLog(
                LocalDateTime.of(2023, 10, 10, 13, 37, 0),
                ApplicationLogLevel.WARN,
                type,
                message,
                applicationId,
                Map.of("first", 1, "second", 2, "map", Map.of("request", "test", "integer", 32, "string", "any-value"))
        )
        authReturnsInvalidCredentials()

        when:
        parkingAccess.reportApplicationLog(applicationLog)

        then:
        thrown(CredentialsException)
    }

    def """
        reportApplicationLog(valid Log)
        api returns an error (broken api)
        throws an error
        """() {
        given:

        def type = "synchronization-failed"
        def message = "The synchronization is succesful"
        def applicationId = "client-app"

        def applicationLog = new ApplicationLog(
                LocalDateTime.of(2023, 10, 10, 13, 37, 0),
                ApplicationLogLevel.WARN,
                type,
                message,
                applicationId,
                Map.of("first", 1, "second", 2, "map", Map.of("request", "test", "integer", 32, "string", "any-value"))
        )

        mockServer.when(
                request()
                        .withMethod("POST")
                        .withPath("/v2/application-logs")
                        .withHeader("Authorization", tokenHeader)
        ).respond(
                response()
                        .withStatusCode(HTTP_BAD_REQUEST)
        )

        when:
        parkingAccess.reportApplicationLog(applicationLog)

        then:
        thrown(HttpRequestException)
    }

    def """
        reportApplicationLog(valid Log)
        api is not reachable
        throws an error
        """() {
        given:
        def type = "synchronization-failed"
        def message = "The synchronization is succesful"
        def applicationId = "client-app"

        def applicationLog = new ApplicationLog(
                LocalDateTime.of(2023, 10, 10, 13, 37, 0),
                ApplicationLogLevel.WARN,
                type,
                message,
                applicationId,
                Map.of("first", 1, "second", 2, "map", Map.of("request", "test", "integer", 32, "string", "any-value"))
        )
        mockServer.stop()

        when:
        parkingAccess.reportApplicationLog(applicationLog)

        then:
        thrown(HttpClientException)
    }
}
