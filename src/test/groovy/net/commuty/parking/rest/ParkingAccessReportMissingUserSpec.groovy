package net.commuty.parking.rest

import net.commuty.parking.http.CredentialsException
import net.commuty.parking.http.HttpClientException
import net.commuty.parking.http.HttpRequestException
import net.commuty.parking.model.UserId

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST
import static java.net.HttpURLConnection.HTTP_CREATED
import static org.mockserver.model.HttpRequest.request
import static org.mockserver.model.HttpResponse.response

class ParkingAccessReportMissingUserSpec extends RestWithAuthSpec {

    def """
        reportMissingUserId(null userId)
        throws an exception
        """() {
        given:
        def userId = null

        when:
        parkingAccess.reportMissingUserId(userId)

        then:
        thrown(IllegalArgumentException)
    }

    def """
        reportMissingUserId(a valid userId)
        """() {
        given:
        def userId = UserId.fromQrCode("F1F2F013D4C9D00D0A")
        mockServer.when(
                request()
                        .withMethod("POST")
                        .withPath("/v2/missing-user-ids")
                        .withHeader("Authorization", tokenHeader)
        ).respond(
                response("""
                               {
                                    "id": "F1F2F013D4C9D00D0A", 
                                    "type": "qrCode"
                               }""")
                        .withStatusCode(HTTP_CREATED)
        )

        when:
        def missingUser = parkingAccess.reportMissingUserId(userId)

        then:
        missingUser != null
        missingUser.id == userId.id
        missingUser.type == userId.type
        mockServer.verify(
                request()
                        .withMethod("POST")
                        .withPath("/v2/missing-user-ids")
        )

        def submittedRequests = mockServer.retrieveRecordedRequestsAndResponses(
                request()
                        .withMethod("POST")
                        .withPath("/v2/missing-user-ids")
        )

        !submittedRequests.toList().empty
        with(reader.parseText(submittedRequests.first().httpRequest.bodyAsString)) {
            id == userId.id
            type == "qrCode"
        }

    }

    def """
        reportMissingUserId(a valid userId)
        token is invalid then refreshed
        no exception is thrown and the final query has a http code 201
        """() {
        given:
        authReturnsExpiredTokenOnceThenValidToken()
        def userId = UserId.fromQrCode("F1F2F013D4C9D00D0A")
        mockServer.when(
                request()
                        .withMethod("POST")
                        .withPath("/v2/missing-user-ids")
                        .withHeader("Authorization", tokenHeader)
        ).respond(
                response("""
                               {
                                    "id": "F1F2F013D4C9D00D0A", 
                                    "type": "qrCode"
                               }""")
                        .withStatusCode(HTTP_CREATED)
        )

        when:
        def missingUser = parkingAccess.reportMissingUserId(userId)

        then:
        missingUser != null
        missingUser.id == userId.id
        missingUser.type == userId.type
        mockServer.verify(
                request()
                        .withMethod("POST")
                        .withPath("/v2/missing-user-ids")
        )

        def submittedRequests = mockServer.retrieveRecordedRequestsAndResponses(
                request()
                        .withMethod("POST")
                        .withPath("/v2/missing-user-ids")
                        .withHeader("Authorization", tokenHeader)
        )

        !submittedRequests.toList().empty
        submittedRequests.first().httpResponse.getStatusCode() == HTTP_CREATED

    }

    def """
        reportMissingUserId(valid userId)
        invalid credentials
        throws an exception
        """() {
        given:
        def userId = UserId.fromQrCode("F1F2F013D4C9D00D0A")
        authReturnsInvalidCredentials()

        when:
        parkingAccess.reportMissingUserId(userId)

        then:
        thrown(CredentialsException)
    }

    def """
        reportMissingUserId(valid userId)
        api returns an error
        throws an exception
        """() {
        given:
        def userId = UserId.fromQrCode("F1F2F013D4C9D00D0A")
        mockServer.when(
                request()
                        .withMethod("POST")
                        .withPath("/v2/missing-user-ids")
                        .withHeader("Authorization", tokenHeader)
        ).respond(
                response()
                        .withStatusCode(HTTP_BAD_REQUEST)
        )

        when:
        parkingAccess.reportMissingUserId(userId)

        then:
        thrown(HttpRequestException)
    }

    def """
        reportMissingUserId(valid userId)
        api is unreachable
        throws an exception
        """() {
        given:
        def userId = UserId.fromQrCode("F1F2F013D4C9D00D0A")
        mockServer.stop()

        when:
        parkingAccess.reportMissingUserId(userId)

        then:
        thrown(HttpClientException)
    }
}
