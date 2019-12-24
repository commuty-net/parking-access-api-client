package net.commuty.parking

import spock.lang.Shared
import spock.lang.Specification

class ConfigurationSpec extends Specification {

    @Shared String defaultHost = "https://parking-access.commuty.net"

    def """
        #buildDefault(String, String)
        is valid
        """() {
        given:
        def username = "user"
        def password = "password"

        when:
        def configuration = Configuration.Builder.buildDefault(username, password)

        then:
        notThrown(IllegalArgumentException)
        configuration.username == username
        configuration.password == password
        configuration.host.toString() == defaultHost
    }

    def """
        #buildDefault(null username, null password)
        throws an exception
        """() {
        given:
        def username = null
        def password = null

        when:
        Configuration.Builder.buildDefault(username, password)

        then:
        thrown(IllegalArgumentException)
    }

    def """
        #withHost(invalid host)
        throws an exception
        """() {
        when:
        Configuration.Builder.create().withHost(invalidHost)

        then:
        thrown(IllegalArgumentException)

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
        Configuration.Builder.create().withHost(host)

        then:
        notThrown(IllegalArgumentException)

    }

    def """
        #withCredentials(invalid credentials)
        throws an exception
        """() {
        when:
        Configuration.Builder.create().withCredentials(username, password)

        then:
        thrown(IllegalArgumentException)

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
        Configuration.Builder.create().withCredentials(username, password)

        then:
        notThrown(IllegalArgumentException)

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
        Configuration.Builder.create().withProxy(null)

        then:
        thrown(IllegalArgumentException)
    }

    def """
        #build() called alone
        throws an exception
        """() {
        when:
        Configuration.Builder.create().build()

        then:
        thrown(IllegalArgumentException)
    }

    def """
        #build()
        withHost not called
        builds with the default host
        is valid
        """() {
        given:
        def username = "toto"
        def password = "pass"

        when:
        def configuration = Configuration.Builder.create().withCredentials(username, password).build()

        then:
        configuration.username == username
        configuration.password == password
        configuration.host.toString() == defaultHost
    }

    def """
        #build()
        withCredentials not called
        throws an exception
        """() {
        given:
        def host = "http://www.test.com"

        when:
        Configuration.Builder.create().withHost(host).build()

        then:
        thrown(IllegalArgumentException)
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
        def configuration = Configuration.Builder.create().withHost(host).withCredentials(username, password).build()

        then:
        notThrown(IllegalArgumentException)
        configuration.username == username
        configuration.password == password
        configuration.host.toString() == host
    }
}
