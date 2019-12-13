package net.commuty.parking

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

class ParkingAccessListRightsSpec extends Specification implements RestTrait {

    @Shared def parkingAccess = ParkingAccess.create("dummy", "dummy", "http://localhost:8080")

    @AutoCleanup('stop')
    ClientAndServer mockServer

    def setup() {
        mockServer = startClientAndServer(8080)
        mockAuthRoutes()
        mockListRightsRoutes()
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

    def mockListRightsRoutes() {

    }
}
