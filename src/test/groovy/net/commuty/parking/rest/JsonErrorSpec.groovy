package net.commuty.parking.rest


import spock.lang.Specification

import static java.nio.charset.Charset.defaultCharset
import static org.apache.commons.io.IOUtils.toInputStream

class JsonErrorSpec extends Specification {

    def mapper = JsonMapper.create()

    def """
        parse a message {"reason":not null, "message": null}
        is parsed correctly
        """() {
        given:
        def json = '{"reason":"a_valid_reason","message": null}'

        when:
        def message = mapper.readError(toInputStream(json, defaultCharset()))

        then:
        message != null
        message.reason == "a_valid_reason"
        message.message == null
    }

    def """
        parse a message {"reason":not null}
        is parsed correctly
        """() {
        given:
        def json = '{"reason":"a_valid_reason"}'

        when:
        def message = mapper.readError(toInputStream(json, defaultCharset()))

        then:
        message != null
        message.reason == "a_valid_reason"
        message.message == null
    }

    def """
        parse a message {"reason":not null, "message":empty string}
        is parsed correctly
        """() {
        given:
        def json = '{"reason":"a_valid_reason","message":""}'

        when:
        def message = mapper.readError(toInputStream(json, defaultCharset()))

        then:
        message != null
        message.reason == "a_valid_reason"
        message.message == ""
    }

    def """
        parse a message {"reason":null, "message": not null}
        is parsed correctly
        """() {
        given:
        def json = '{"reason":null,"message": "hello"}'

        when:
        def message = mapper.readError(toInputStream(json, defaultCharset()))

        then:
        message != null
        message.reason == null
        message.message == "hello"
    }

    def """
        parse a message {"message":not null}
        is parsed correctly
        """() {
        given:
        def json = '{"message":"hello"}'

        when:
        def message = mapper.readError(toInputStream(json, defaultCharset()))

        then:
        message != null
        message.reason == null
        message.message == "hello"
    }

    def """
        parse a message {"reason":empty string, "message":not null}
        is parsed correctly
        """() {
        given:
        def json = '{"reason":"", "message":"hello"}'

        when:
        def message = mapper.readError(toInputStream(json, defaultCharset()))

        then:
        message != null
        message.reason == ""
        message.message == "hello"
    }

    def """
        parse a message {}
        is parsed correctly
        """() {
        given:
        def json = '{}'

        when:
        def message = mapper.readError(toInputStream(json, defaultCharset()))

        then:
        message != null
        message.reason == null
        message.message == null
    }

    def """
        parse an empty message
        exception is thrown
        """() {
        given:
        def json = ""

        when:
        mapper.readError(toInputStream(json, defaultCharset()))

        then:
        thrown(IOException)
    }


}
