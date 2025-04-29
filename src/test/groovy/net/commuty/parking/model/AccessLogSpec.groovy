package net.commuty.parking.model

import groovy.json.JsonSlurper
import net.commuty.parking.rest.JsonMapperTest
import spock.lang.Specification

import java.time.LocalDateTime

import static net.commuty.parking.model.AccessDirection.IN
import static net.commuty.parking.model.AccessDirection.OUT

class AccessLogSpec extends Specification {

    def mapper = JsonMapperTest.create()
    def reader = new JsonSlurper()

    def """
        createInAccessLog(null userId, a date)
        throws an exception
        """() {
        given:
        def userId = null
        def date = LocalDateTime.now()

        when:
        AccessLog.createInAccessLog(userId, date)

        then:
        thrown(IllegalArgumentException)
    }

    def """
        createInAccessLog(valid userId, null date)
        throws an exception
        """() {
        given:
        def userId = UserId.fromBadgeNumber("1234")
        def date = null

        when:
        AccessLog.createInAccessLog(userId, date)

        then:
        thrown(IllegalArgumentException)
    }

    def """
        createInAccessLog(valid userId, valid date)
        returns an access log with way = IN
        """() {
        given:
        def userId = UserId.fromBadgeNumber("1234")
        def date = LocalDateTime.of(2019, 12, 25, 13, 37, 0)

        when:
        def accessLog = AccessLog.createInAccessLog(userId, date)

        then:
        accessLog != null
        accessLog.userId == userId.id
        accessLog.userIdType == userId.type
        accessLog.at == date
        accessLog.way == IN
        accessLog.identificationMethod == null
        accessLog.identificationValue == null
        accessLog.reason == null
    }

    def """
        createInAccessLog(valid userId, valid date)
        is parsed correctly
        """() {
        given:
        def userId = UserId.fromBadgeNumber("1234")
        def date = LocalDateTime.of(2019, 12, 25, 13, 37, 0)

        when:
        def accessLogString = mapper.write(AccessLog.createInAccessLog(userId, date))

        then:
        accessLogString != null
        def parsed = reader.parseText(accessLogString)
        parsed.userId == userId.id
        parsed.userIdType == "badgeNumber"
        parsed.at == "2019-12-25T13:37:00"
        parsed.way == "in"
        parsed.identificationMethod == null
        parsed.identificationValue == null
        parsed.reason == null
    }

    def """
        createOutAccessLog(valid userId, valid date)
        returns an access log with way = OUT
        """() {
        given:
        def userId = UserId.fromBadgeNumber("1234")
        def date = LocalDateTime.of(2019, 12, 25, 13, 37, 0)

        when:
        def accessLog = AccessLog.createOutAccessLog(userId, date)

        then:
        accessLog != null
        accessLog.userId == userId.id
        accessLog.userIdType == userId.type
        accessLog.at == date
        accessLog.way == OUT
        accessLog.identificationMethod == null
        accessLog.identificationValue == null
        accessLog.reason == null
    }

    def """
        createOutAccessLog(valid userId, valid date)
        is parsed correctly
        """() {
        given:
        def userId = UserId.fromBadgeNumber("1234")
        def date = LocalDateTime.of(2019, 12, 25, 13, 37, 0)

        when:
        def accessLogString = mapper.write(AccessLog.createOutAccessLog(userId, date))

        then:
        accessLogString != null
        def parsed = reader.parseText(accessLogString)
        parsed.userId == userId.id
        parsed.userIdType == "badgeNumber"
        parsed.at == "2019-12-25T13:37:00"
        parsed.way == "out"
        parsed.identificationMethod == null
        parsed.identificationValue == null
        parsed.reason == null
    }


    def """
        createOutAccessLog(valid userId, valid at, valid granted, valid identificationMethod, valid reason, null attributes)
        is parsed correctly
        """() {
        given:
        def userId = UserId.fromBadgeNumber("1234")
        def date = LocalDateTime.of(2019, 12, 25, 13, 37, 0)
        def granted = true
        def identificationMethod = "anpr"
        def identificationValue = "3-ARD-789"
        def reason = "That person was allowed by ANPR"

        when:
        def accessLogString = mapper.write(AccessLog.createOutAccessLog(userId, date, granted, identificationMethod, identificationValue, reason, null))

        then:
        accessLogString != null
        def parsed = reader.parseText(accessLogString)
        parsed.userId == userId.id
        parsed.userIdType == "badgeNumber"
        parsed.at == "2019-12-25T13:37:00"
        parsed.way == "out"
        parsed.identificationMethod == identificationMethod
        parsed.identificationValue == identificationValue
        parsed.reason == reason
        parsed.attributes == null
    }

    def """
        createOutAccessLog(valid userId, valid at, valid granted, valid identificationMethod, valid reason, valid attributes)
        is parsed correctly
        """() {
        given:
        def userId = UserId.fromBadgeNumber("1234")
        def date = LocalDateTime.of(2019, 12, 25, 13, 37, 0)
        def granted = true
        def identificationMethod = "anpr"
        def identificationValue = "3-ARD-789"
        def reason = "That person was allowed by ANPR"
        def attributes = new AttributesContainer("ENTRANCE-1", 1, true, [1,2,3], new PersonContainer("John"))

        when:
        def accessLogString = mapper.write(AccessLog.createOutAccessLog(userId, date, granted, identificationMethod, identificationValue, reason, attributes))

        then:
        accessLogString != null
        def parsed = reader.parseText(accessLogString)
        parsed.userId == userId.id
        parsed.userIdType == "badgeNumber"
        parsed.at == "2019-12-25T13:37:00"
        parsed.way == "out"
        parsed.identificationMethod == identificationMethod
        parsed.identificationValue == identificationValue
        parsed.reason == reason
        parsed.attributes.readerName == "ENTRANCE-1"
        parsed.attributes.readerId == 1
        parsed.attributes.valid == true
        parsed.attributes.numbers == [1,2,3]
        parsed.attributes.person.name == "John"
    }

    class AttributesContainer {
        String readerName
        Integer readerId
        Boolean valid
        List<Integer> numbers
        PersonContainer person

        AttributesContainer(String readerName, Integer readerId, Boolean valid, List<Integer> numbers, PersonContainer person) {
            this.readerName = readerName
            this.readerId = readerId
            this.valid = valid
            this.numbers = numbers
            this.person = person
        }
    }

    class PersonContainer {
        String name
        PersonContainer(String name) {
            this.name = name
        }
    }
}
