package net.commuty.parking

import groovy.json.JsonSlurper
import net.commuty.parking.configuration.JsonMapper
import org.mockserver.integration.ClientAndServer
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import static java.net.HttpURLConnection.HTTP_OK
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import static org.mockserver.integration.ClientAndServer.startClientAndServer
import static org.mockserver.model.HttpRequest.request
import static org.mockserver.model.HttpResponse.response
import static org.mockserver.model.NottableString.not

class RestWithAuthSpec extends Specification {

    @Shared ParkingAccess parkingAccess

    @AutoCleanup('stop')
    ClientAndServer mockServer

    @Shared JsonSlurper reader = new JsonSlurper()
    @Shared JsonMapper mapper = JsonMapper.create()

    String tokenRequest = """{"token":"atoken"}"""
    String token = "Bearer atoken"

    def setup() {
        mockServer = startClientAndServer()
        parkingAccess = ParkingAccess.create("dummy", "dummy", "http://localhost:${mockServer.getLocalPort()}")
        mockAuthRoutes()
    }

    def mockAuthRoutes() {
        // get token
        mockServer.when(
                request()
                        .withMethod("POST")
                        .withPath("/v2/token-requests")
        ).respond(
                response(tokenRequest)
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
}
