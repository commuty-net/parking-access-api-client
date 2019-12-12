package net.commuty.configuration

import net.commuty.exception.ClientBuilderException
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
        def client = Client.Builder.buildDefault(username, password)

        then:
        thrown(ClientBuilderException)
    }

    def """
        #withHost(invalid host)
        throws an exception
        """() {
        when:
        new Client.Builder().withHost(invalidHost)

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
        new Client.Builder().withHost(host);

        then:
        notThrown(ClientBuilderException)

    }

    def """
        #withCredentials(invalid credentials)
        throws an exception
        """() {
        when:
        new Client.Builder().withCredentials(username, password)

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
        new Client.Builder().withCredentials(username, password)

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
        new Client.Builder().withProxy(null)

        then:
        thrown(ClientBuilderException)
    }

    def """
        #build() called alone
        throws an exception
        """() {
        when:
        new Client.Builder().build();

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
        new Client.Builder().withCredentials(username, password).build()

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
        new Client.Builder().withHost(host).build()

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
        def client = new Client.Builder().withHost(host).withCredentials(username, password).build()

        then:
        notThrown(ClientBuilderException)
        client.username == username
        client.password == password
        client.host.toString() == host
    }
}
