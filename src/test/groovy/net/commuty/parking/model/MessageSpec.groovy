package net.commuty.parking.model

import net.commuty.parking.configuration.JsonMapper
import spock.lang.Specification

class MessageSpec extends Specification {

    def mapper = JsonMapper.create();

    def """
        parse a message {"reason":not null, "message": null}
        is parsed correctly
        """() {
        given:
        def string = '{"reason":"a_valid_reason","message": null}'

        when:
        def message = mapper.read(new StringReader(string), Message.class)

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
        def string = '{"reason":"a_valid_reason"}'

        when:
        def message = mapper.read(new StringReader(string), Message.class)

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
        def string = '{"reason":"a_valid_reason","message":""}'

        when:
        def message = mapper.read(new StringReader(string), Message.class)

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
        def string = '{"reason":null,"message": "hello"}'

        when:
        def message = mapper.read(new StringReader(string), Message.class)

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
        def string = '{"message":"hello"}'

        when:
        def message = mapper.read(new StringReader(string), Message.class)

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
        def string = '{"reason":"", "message":"hello"}'

        when:
        def message = mapper.read(new StringReader(string), Message.class)

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
        def string = '{}'

        when:
        def message = mapper.read(new StringReader(string), Message.class)

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
        def string = ""

        when:
        mapper.read(new StringReader(string), Message.class)

        then:
        thrown(IOException)
    }


}
