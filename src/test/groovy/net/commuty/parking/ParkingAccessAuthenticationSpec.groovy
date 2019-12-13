package net.commuty.parking


import net.commuty.parking.exception.CredentialsException
import net.commuty.parking.http.request.TokenRequest
import org.mockserver.integration.ClientAndServer
import spock.lang.AutoCleanup
import spock.lang.Specification

import static java.net.HttpURLConnection.HTTP_FORBIDDEN
import static java.net.HttpURLConnection.HTTP_OK
import static org.mockserver.integration.ClientAndServer.startClientAndServer
import static org.mockserver.model.HttpRequest.request
import static org.mockserver.model.HttpResponse.response

class ParkingAccessAuthenticationSpec extends Specification implements RestTrait {

    @AutoCleanup('stop')
    ClientAndServer mockServer

    def setup() {
        mockServer = startClientAndServer(8080)
    }

    def """
        #authenticate()
        wrong credentials
        throws an exception
        """() {
        given:
        def username = "test"
        def password = "blu"
        def parkingAccess = ParkingAccess.create(username, password, host)
        mockServer.when(
                request()
                .withMethod("POST")
                .withPath("/v2/token-requests")
                .withBody(mapper.write(new TokenRequest(username, password)))
        ).respond(
                response()
                .withStatusCode(HTTP_FORBIDDEN)
        )

        when:
        parkingAccess.authenticate()

        then:
        thrown(CredentialsException)
    }

    def """
        #authenticate()
        with correct credentials
        returns a token
        """() {
        given:
        def username = "test"
        def password = "valid"
        def responseToken = """
            {
                "token": "some-valid-token"
            }
        """
        def parkingAccess = ParkingAccess.create(username, password, host)
        mockServer.when(
                request()
                .withMethod("POST")
                .withPath("/v2/token-requests")
                .withBody(mapper.write(new TokenRequest(username, password)))
        ).respond(
                response(responseToken)
                .withStatusCode(HTTP_OK)
        )

        when:
        def token = parkingAccess.authenticate()

        then:
        token == "some-valid-token"
    }
}
