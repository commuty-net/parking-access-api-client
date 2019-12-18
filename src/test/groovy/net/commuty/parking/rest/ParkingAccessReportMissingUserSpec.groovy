package net.commuty.parking.rest


import net.commuty.parking.model.UserId
import net.commuty.parking.model.UserIdType
import spock.lang.IgnoreRest

import static java.net.HttpURLConnection.HTTP_OK
import static net.commuty.parking.model.UserIdType.QR_CODE
import static org.mockserver.model.HttpRequest.request
import static org.mockserver.model.HttpResponse.response

class ParkingAccessReportMissingUserSpec extends RestWithAuthSpec {

    def """
        #reportMissingUserId(null userId)
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
        #reportMissingUserId(a valid userId)
        """() {
        given:
        def userId = UserId.fromQrCode("F1F2F013D4C9D00D0A")
        mockServer.when(
                request()
                        .withMethod("POST")
                        .withPath("/v2/missing-user-ids")
                        .withHeader("Authorization", token)
        ).respond(
                response("""
                               {
                                    "id": "F1F2F013D4C9D00D0A", 
                                    "type": "qrCode"
                               }""")
                        .withStatusCode(HTTP_OK)
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
}
