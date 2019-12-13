package net.commuty.parking.configuration

import net.commuty.parking.exception.ClientBuilderException
import spock.lang.Specification

class ClientSpec extends Specification {

    def """
        #buildDefault(String, String)
        is valid
        """() {
        given:
        def username = "user"
        def password = "password"

        when:
        def client = Client.Builder.buildDefault(username, password)

        then:
        notThrown(ClientBuilderException)
        client.username == username
        client.password == password
        client.host.toString() == "https://parking-access.commuty.net"
    }

    def """
        #buildDefault(null username, null password)
        throws an exception
        """() {
        given:
        def username = null
        def password = null

        when:
        Client.Builder.buildDefault(username, password)

        then:
        thrown(ClientBuilderException)
    }

    def """
        #withHost(invalid host)
        throws an exception
        """() {
        when:
        Client.Builder.create().withHost(invalidHost)

        then:
        thrown(ClientBuilderException)

        where:
        invalidHost        | _
        null               | _
        "toto"             | _
        "htp://commuty.com" | _
    }

    def """
        #withHost(invalid host)
        is valid
        """() {
        given:
        def host = "http://app.commuty.net"

        when:
        Client.Builder.create().withHost(host)

        then:
        notThrown(ClientBuilderException)

    }

    def """
        #withCredentials(invalid credentials)
        throws an exception
        """() {
        when:
        Client.Builder.create().withCredentials(username, password)

        then:
        thrown(ClientBuilderException)

        where:
        username | password
        null     | null
        "toto"   | null
        null     | "toto"
        ""       | "toto"
        " "      | "toto"
    }

    def """
        #withCredentials(valid credentials)
        is valid
        """() {
        when:
        Client.Builder.create().withCredentials(username, password)

        then:
        notThrown(ClientBuilderException)

        where:
        username | password
        "toto"   | "tutu"
        "toto"   | ""
        "toto"   | " "
    }

    def """
        #withProxy(null proxy)
        throws an exception
        """() {
        when:
        Client.Builder.create().withProxy(null)

        then:
        thrown(ClientBuilderException)
    }

    def """
        #build() called alone
        throws an exception
        """() {
        when:
        Client.Builder.create().build()

        then:
        thrown(ClientBuilderException)
    }

    def """
        #build()
        withHost not called
        throws an exception
        """() {
        given:
        def username = "toto"
        def password = "pass"

        when:
        Client.Builder.create().withCredentials(username, password).build()

        then:
        thrown(ClientBuilderException)
    }

    def """
        #build()
        withCredentials not called
        throws an exception
        """() {
        given:
        def host = "http://www.test.com"

        when:
        Client.Builder.create().withHost(host).build()

        then:
        thrown(ClientBuilderException)
    }

    def """
        #build()
        withCredentials and withHost called
        isValid
        """() {
        given:
        def host = "http://www.test.com"
        def username = "toto"
        def password = "tutu"

        when:
        def client = Client.Builder.create().withHost(host).withCredentials(username, password).build()

        then:
        notThrown(ClientBuilderException)
        client.username == username
        client.password == password
        client.host.toString() == host
    }
}
