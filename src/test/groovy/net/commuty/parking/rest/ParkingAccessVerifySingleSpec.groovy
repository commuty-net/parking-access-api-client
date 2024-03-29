package net.commuty.parking.rest

import net.commuty.parking.http.CredentialsException
import net.commuty.parking.http.HttpClientException
import net.commuty.parking.http.HttpRequestException
import spock.lang.Shared

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST
import static java.net.HttpURLConnection.HTTP_OK
import static net.commuty.parking.model.UserId.fromBadgeNumber
import static net.commuty.parking.model.UserId.fromEmail
import static org.mockserver.model.HttpRequest.request
import static org.mockserver.model.HttpResponse.response

class ParkingAccessVerifySingleSpec extends RestWithAuthSpec {

    @Shared
    def validParkingSite = "parking-site"
    @Shared
    def unknownParkingSite = "wrongsiteid"
    @Shared
    def validUser = fromBadgeNumber("1234")
    @Shared
    def unknownUser = fromEmail("anon@commuty.net")

    def mockVerificationRoutes() {
        // valid route (must match parking site and body)
        mockServer.when(
                request()
                        .withMethod("POST")
                        .withPath("/v2/parking-sites/${validParkingSite}/access-requests")
                        .withHeader("Authorization", tokenHeader)
                        .withBody(mapper.write(new VerificationRequest(validUser)))
        ).respond(
                response("""
                               {
                                   "granted":true
                               }""")
                        .withStatusCode(HTTP_OK)
        )

        // other routes:
        // wrong parking site
        // correct parking site but wrong body
        mockServer.when(
                request()
                        .withMethod("POST")
                        .withPath("/v2/parking-sites/.*/access-requests")
                        .withHeader("Authorization", tokenHeader)
        ).respond(
                response('{"granted":false}')
                        .withStatusCode(HTTP_OK)
        )
    }

    def """
        #verifySingle(null parking site id, valid userId)
        throws an exception
        """() {
        given:
        def parkingSiteId = null
        def userId = validUser

        when:
        parkingAccess.isGranted(parkingSiteId, userId)

        then:
        thrown(IllegalArgumentException)
    }

    def """
        #verifySingle(empty parking site id, valid userId)
        throws an exception
        """() {
        given:
        def parkingSiteId = " "
        def userId = validUser

        when:
        parkingAccess.isGranted(parkingSiteId, userId)

        then:
        thrown(IllegalArgumentException)
    }

    def """
        #verifySingle(valid parking site id, null userId)
        throws an exception
        """() {
        given:
        def parkingSiteId = validParkingSite
        def userId = null

        when:
        parkingAccess.isGranted(parkingSiteId, userId)

        then:
        thrown(IllegalArgumentException)
    }

    def "verifySingle(unknown parking site, unknown user) returns false"() {
        given:
        mockVerificationRoutes()

        when:
        def isGranted = parkingAccess.isGranted(unknownParkingSite, unknownUser)

        then:
        !isGranted
    }

    def """
        verifySingle(known parking site, unknown user)
        returns false
        """() {
        given:
        mockVerificationRoutes()

        when:
        def isGranted = parkingAccess.isGranted(validParkingSite, unknownUser)

        then:
        !isGranted
    }

    def """
        #verifySingle(unknown parking site, known user)
        returns false
        """() {
        given:
        mockVerificationRoutes()

        when:
        def isGranted = parkingAccess.isGranted(unknownParkingSite, validUser)

        then:
        !isGranted
    }

    def """
        #verifySingle(known parking site, known user)
        returns true
        """() {
        given:
        mockVerificationRoutes()

        when:
        def isGranted = parkingAccess.isGranted(validParkingSite, validUser)

        then:
        isGranted
        mockServer.verify(
                request()
                        .withPath("/v2/parking-sites/${validParkingSite}/access-requests")
                        .withBody(mapper.write(new VerificationRequest(validUser)))
        )

    }

    def """
        #verifySingle(known parking site, known user)
        token is invalid (expired) then refreshed
        returns true
        """() {
        given:
        mockVerificationRoutes()
        authReturnsExpiredTokenOnceThenValidToken()

        when:
        def isGranted = parkingAccess.isGranted(validParkingSite, validUser)

        then:
        isGranted
        mockServer.verify(
                request()
                        .withPath("/v2/parking-sites/${validParkingSite}/access-requests")
                        .withBody(mapper.write(new VerificationRequest(validUser)))
        )

    }

    def """
        #verifySingle(known parking site, known user)
        token is valid
        api returns an error (broken api)
        throws an exception
        """() {
        given:
        def parkingSiteName = "parking-that-throws-error"
        mockServer.when(
                request()
                        .withMethod("POST")
                        .withPath("/v2/parking-sites/${parkingSiteName}/access-requests")
                        .withHeader("Authorization", tokenHeader)
        ).respond(
                response()
                        .withStatusCode(HTTP_BAD_REQUEST)
        )


        when:
        parkingAccess.isGranted(parkingSiteName, validUser)

        then:
        thrown(HttpRequestException)
    }

    def """
        #verifySingle(known parking site, known user)
        credentials are invalid
        throws an exception
        """() {
        given:
        authReturnsInvalidCredentials()

        when:
        parkingAccess.isGranted(validParkingSite, validUser)

        then:
        thrown(CredentialsException)
    }

    def """
        #verifySingle(known parking site, known user)
        Server is not reachable
        throws an exception
        """() {
        given:
        mockServer.stop()

        when:
        parkingAccess.isGranted(validParkingSite, validUser)

        then:
        thrown(HttpClientException)
    }
}
