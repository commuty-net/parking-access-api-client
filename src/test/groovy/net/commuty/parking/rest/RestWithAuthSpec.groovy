package net.commuty.parking.rest

import groovy.json.JsonSlurper
import net.commuty.parking.Configuration
import net.commuty.parking.ParkingAccess
import org.mockserver.integration.ClientAndServer
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import static java.net.HttpURLConnection.*
import static org.mockserver.integration.ClientAndServer.startClientAndServer
import static org.mockserver.matchers.Times.once
import static org.mockserver.model.Header.header
import static org.mockserver.model.HttpRequest.request
import static org.mockserver.model.HttpResponse.response
import static org.mockserver.model.NottableString.not

class RestWithAuthSpec extends Specification {

    @Shared
    ParkingAccess parkingAccess

    @AutoCleanup('stop')
    ClientAndServer mockServer

    @Shared
    JsonSlurper reader = new JsonSlurper()

    @Shared
    JsonMapper mapper = JsonMapper.create()

    String token = "atoken"
    String tokenResponse = """{"token":"$token"}"""
    String tokenHeader = "Bearer $token"
    String expiredToken = "expired"
    String expiredTokenResponse = """{"token":"$expiredToken"}"""
    String expiredTokenHeader = "Bearer $expiredToken"

    def setup() {
        mockServer = startClientAndServer()
        parkingAccess = Configuration.Builder.create()
                .withCredentials("dummy", "dummy")
                .withRetryStrategy(5, 0)
                .withHost("http://localhost:${mockServer.getLocalPort()}")
                .build().toRestClient()
        mockAuthRoutes()
    }

    def mockAuthRoutes() {
        // get token
        mockServer.when(
                request()
                        .withMethod("POST")
                        .withPath("/v2/token-requests")
        ).respond(
                response(tokenResponse)
                        .withStatusCode(HTTP_OK)
        )

        // no token provided
        mockServer.when(
                request()
                        .withPath(not("/v2/token-requests"))
                        .withHeader(not("Authorization"))
        ).respond(
                response()
                        .withStatusCode(HTTP_UNAUTHORIZED)
        )

        // token expired
        mockServer.when(
                request()
                        .withPath(not("/v2/token-requests"))
                        .withHeaders(
                                header("Authorization", expiredTokenHeader)
                        )
        ).respond(
                response()
                        .withStatusCode(HTTP_FORBIDDEN)
        )
    }

    def authReturnsExpiredTokenOnceThenValidToken() {
        // cancel default handler
        mockServer.clear(
                request()
                        .withMethod("POST")
                        .withPath("/v2/token-requests")
        )

        // return an expired token once
        mockServer.when(
                request()
                        .withMethod("POST")
                        .withPath("/v2/token-requests"),
                once()
        ).respond(
                response(expiredTokenResponse)
                        .withStatusCode(HTTP_OK)
        )

        // for further calls, the token is valid
        mockServer.when(
                request()
                        .withMethod("POST")
                        .withPath("/v2/token-requests")
        ).respond(
                response(tokenResponse)
                        .withStatusCode(HTTP_OK)
        )
    }

    def authReturnsInvalidCredentials() {
        mockServer.clear(
                request()
                        .withMethod("POST")
                        .withPath("/v2/token-requests")
        )

        mockServer.when(
                request()
                        .withMethod("POST")
                        .withPath("/v2/token-requests")
        ).respond(
                response()
                        .withStatusCode(HTTP_FORBIDDEN)
        )
    }
}
