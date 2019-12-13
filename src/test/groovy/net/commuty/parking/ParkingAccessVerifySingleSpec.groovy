package net.commuty.parking

import net.commuty.parking.http.request.VerificationRequest
import org.mockserver.integration.ClientAndServer
import org.mockserver.verify.VerificationTimes
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import static java.net.HttpURLConnection.HTTP_OK
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import static net.commuty.parking.model.UserId.fromBadgeNumber
import static net.commuty.parking.model.UserId.fromEmail
import static org.mockserver.integration.ClientAndServer.startClientAndServer
import static org.mockserver.model.HttpRequest.request
import static org.mockserver.model.HttpResponse.response
import static org.mockserver.model.NottableString.not
import static org.mockserver.verify.VerificationTimes.once

class ParkingAccessVerifySingleSpec extends Specification implements RestTrait {

    @Shared def parkingAccess = ParkingAccess.create("dummy", "dummy", "http://localhost:8080")

    @Shared def validParkingSite = "parking-site"
    @Shared def unknownParkingSite = "wrongsiteid"
    @Shared def validUser = fromBadgeNumber("1234")
    @Shared def unknownUser = fromEmail("anon@commuty.net")

    @AutoCleanup('stop')
    ClientAndServer mockServer

    def setup() {
        mockServer = startClientAndServer(8080)
        mockAuthRoutes()
        mockVerificationRoutes()
    }

    def mockAuthRoutes() {
        // get token
        mockServer.when(
                request()
                        .withMethod("POST")
                        .withPath("/v2/token-requests")
        ).respond(
                response('{"token":"atoken"}')
                        .withStatusCode(HTTP_OK)
        )

        // unauthorized
        mockServer.when(
                request()
                        .withPath(not("/v2/token-requests"))
                        .withHeader(not("Authorization"))
        ).respond(
                response()
                        .withStatusCode(HTTP_UNAUTHORIZED)
        )
    }

    def mockVerificationRoutes() {
        // valid route (must match parking site and body)
        mockServer.when(
                request()
                .withMethod("POST")
                .withPath("/v2/parking-sites/${validParkingSite}/access-requests")
                .withHeader("Authorization", "Bearer atoken")
                .withBody(mapper.write(new VerificationRequest(validUser)))
        ).respond(
                response('{"granted":true}')
                .withStatusCode(HTTP_OK)
        )

        // other routes:
        // wrong parking site
        // correct parking site but wrong body
        mockServer.when(
                request()
                .withMethod("POST")
                .withPath("/v2/parking-sites/.*/access-requests")
                .withHeader("Authorization", "Bearer atoken")
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
        parkingAccess.verifySingle(parkingSiteId, userId)

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
        parkingAccess.verifySingle(parkingSiteId, userId)

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
        parkingAccess.verifySingle(parkingSiteId, userId)

        then:
        thrown(IllegalArgumentException)
    }

    def """
        #verifySingle(unknown parking site, unknown user)
        returns false
        """() {

        when:
        def isGranted = parkingAccess.verifySingle(unknownParkingSite, unknownUser)

        then:
        !isGranted
    }

    def """
        #verifySingle(known parking site, unknown user)
        returns false
        """() {

        when:
        def isGranted = parkingAccess.verifySingle(validParkingSite, unknownUser)

        then:
        !isGranted
    }

    def """
        #verifySingle(unknown parking site, known user)
        returns false
        """() {

        when:
        def isGranted = parkingAccess.verifySingle(unknownParkingSite, validUser)

        then:
        !isGranted
    }

    def """
        #verifySingle(known parking site, known user)
        returns true
        """() {

        when:
        def isGranted = parkingAccess.verifySingle(validParkingSite, validUser)

        then:
        isGranted
        mockServer.verify(
                request()
                .withPath("/v2/parking-sites/${validParkingSite}/access-requests")
                .withBody(mapper.write(new VerificationRequest(validUser)))
        )

    }
}
