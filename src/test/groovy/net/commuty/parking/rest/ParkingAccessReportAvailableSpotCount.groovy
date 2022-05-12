package net.commuty.parking.rest

import net.commuty.parking.http.CredentialsException
import net.commuty.parking.http.HttpClientException
import net.commuty.parking.http.HttpRequestException

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST
import static java.net.HttpURLConnection.HTTP_CREATED
import static org.mockserver.model.HttpRequest.request
import static org.mockserver.model.HttpResponse.response

class ParkingAccessReportAvailableSpotCount extends RestWithAuthSpec {

    def """
        reportAvailableSpotCount(null parkingSiteId)
        throws an exception
        """() {
        given:
        def parkingSiteId = null
        def availableSpotCount = 3
        def totalSpotCount = 10

        when:
        parkingAccess.reportAvailableSpotCount(parkingSiteId, availableSpotCount, totalSpotCount)

        then:
        thrown(IllegalArgumentException)
    }

    def """
        reportAvailableSpotCount(empty parkingSiteId)
        throws an exception
        """() {
        given:
        def parkingSiteId = "  "
        def availableSpotCount = 3
        def totalSpotCount = 10

        when:
        parkingAccess.reportAvailableSpotCount(parkingSiteId, availableSpotCount, totalSpotCount)

        then:
        thrown(IllegalArgumentException)
    }

    def """
        reportAvailableSpotCount(valid parkingSiteId, count, null total)
        """() {
        given:
        def parkingSiteId = "ABCD"
        def availableSpotCount = 3
        def totalSpotCount = null
        mockServer.when(
                request()
                .withMethod("POST")
                .withPath("/v2/parking-sites/${parkingSiteId}/counts")
                .withHeader("Authorization", tokenHeader)
        ).respond(
                response("""
                               {
                                   "count": ${availableSpotCount},
                                   "total": null
                               }
                               """)
                .withStatusCode(HTTP_CREATED)
        )


        when:
        def count = parkingAccess.reportAvailableSpotCount(parkingSiteId, availableSpotCount, totalSpotCount)

        then:
        count != null
        count.count == availableSpotCount
        count.total == null
        mockServer.verify(
                request()
                .withMethod("POST")
                .withPath("/v2/parking-sites/${parkingSiteId}/counts")
        )
    }

    def """
        reportAvailableSpotCount(valid parkingSiteId, count, total)
        """() {
        given:
        def parkingSiteId = "ABCD"
        def availableSpotCount = 3
        def totalSpotCount = 10
        mockServer.when(
                request()
                        .withMethod("POST")
                        .withPath("/v2/parking-sites/${parkingSiteId}/counts")
                        .withHeader("Authorization", tokenHeader)
        ).respond(
                response("""
                               {
                                   "count": ${availableSpotCount},
                                   "total": ${totalSpotCount}
                               }
                               """)
                        .withStatusCode(HTTP_CREATED)
        )

        when:
        def count = parkingAccess.reportAvailableSpotCount(parkingSiteId, availableSpotCount, totalSpotCount)

        then:
        count != null
        count.count == availableSpotCount
        count.total == totalSpotCount
        mockServer.verify(
                request()
                        .withMethod("POST")
                        .withPath("/v2/parking-sites/${parkingSiteId}/counts")
        )
    }

    // --------

    def """
        reportAvailableSpotCount(valid parkingSiteId, count, total)
        token is invalid then refreshed
        no exception is thrown and the final query has a http code 201
        """() {
        given:
        authReturnsExpiredTokenOnceThenValidToken()
        def parkingSiteId = "ABCD"
        def availableSpotCount = 3
        def totalSpotCount = 10
        mockServer.when(
                request()
                        .withMethod("POST")
                        .withPath("/v2/parking-sites/${parkingSiteId}/counts")
                        .withHeader("Authorization", tokenHeader)
        ).respond(
                response("""
                               {
                                   "count": ${availableSpotCount},
                                   "total": ${totalSpotCount}
                               }
                               """)
                        .withStatusCode(HTTP_CREATED)
        )

        when:
        def count = parkingAccess.reportAvailableSpotCount(parkingSiteId, availableSpotCount, totalSpotCount)

        then:
        count != null
        count.count == availableSpotCount
        count.total == totalSpotCount
        mockServer.verify(
                request()
                        .withMethod("POST")
                        .withPath("/v2/parking-sites/${parkingSiteId}/counts")
        )

        def submittedRequests = mockServer.retrieveRecordedRequestsAndResponses(
                request()
                        .withMethod("POST")
                        .withPath("/v2/parking-sites/${parkingSiteId}/counts")
                        .withHeader("Authorization", tokenHeader)
        )

        !submittedRequests.toList().empty
        submittedRequests.first().httpResponse.getStatusCode() == HTTP_CREATED

    }

    def """
        reportAvailableSpotCount(valid parkingSiteId, count, total)
        invalid credentials
        throws an exception
        """() {
        given:
        def parkingSiteId = "ABCD"
        def availableSpotCount = 3
        def totalSpotCount = 10
        authReturnsInvalidCredentials()

        when:
        parkingAccess.reportAvailableSpotCount(parkingSiteId, availableSpotCount, totalSpotCount)

        then:
        thrown(CredentialsException)
    }

    def """
        reportAvailableSpotCount(valid parkingSiteId, count, total)
        api returns an error
        throws an exception
        """() {
        given:
        def parkingSiteId = "ABCD"
        def availableSpotCount = 3
        def totalSpotCount = 10
        mockServer.when(
                request()
                        .withMethod("POST")
                        .withPath("/v2/parking-sites/${parkingSiteId}/counts")
                        .withHeader("Authorization", tokenHeader)
        ).respond(
                response()
                        .withStatusCode(HTTP_BAD_REQUEST)
        )

        when:
        parkingAccess.reportAvailableSpotCount(parkingSiteId, availableSpotCount, totalSpotCount)

        then:
        thrown(HttpRequestException)
    }

    def """
        reportAvailableSpotCount(valid parkingSiteId, count, total)
        api is unreachable
        throws an exception
        """() {
        given:
        def parkingSiteId = "ABCD"
        def availableSpotCount = 3
        def totalSpotCount = 10
        mockServer.stop()

        when:
        parkingAccess.reportAvailableSpotCount(parkingSiteId, availableSpotCount, totalSpotCount)

        then:
        thrown(HttpClientException)
    }
}
