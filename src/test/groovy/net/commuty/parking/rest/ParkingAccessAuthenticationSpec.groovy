package net.commuty.parking.rest

import net.commuty.parking.Configuration
import net.commuty.parking.http.CredentialsException
import org.mockserver.integration.ClientAndServer
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import static java.net.HttpURLConnection.HTTP_FORBIDDEN
import static java.net.HttpURLConnection.HTTP_OK
import static org.mockserver.integration.ClientAndServer.startClientAndServer
import static org.mockserver.model.HttpRequest.request
import static org.mockserver.model.HttpResponse.response

class ParkingAccessAuthenticationSpec extends Specification {

    @Shared String host

    @Shared JsonMapper mapper = JsonMapper.create()

    @AutoCleanup('stop')
    ClientAndServer mockServer

    def setup() {
        mockServer = startClientAndServer()
        host = "http://localhost:${mockServer.getLocalPort()}"
    }

    def """
        #authenticate()
        wrong credentials
        throws an exception
        """() {
        given:
        def username = "test"
        def password = "blu"
        def parkingAccess = Configuration.Builder.create().withCredentials(username, password).withHost(host).build().toRestClient()
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
        def parkingAccess = Configuration.Builder.create().withCredentials(username, password).withHost(host).build().toRestClient()
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
