package net.commuty.parking.model


import groovy.json.JsonSlurper
import net.commuty.parking.rest.JsonMapperTest
import spock.lang.Specification

import java.time.LocalDateTime

import static net.commuty.parking.model.AccessDirection.IN
import static net.commuty.parking.model.AccessDirection.OUT

class ApplicationLogSpec extends Specification {

    def mapper = JsonMapperTest.create()
    def reader = new JsonSlurper()

    def """
        constructor(timestamp, level, type longer than 30 char, message longer than 200 char, applicationId shorter than 30 char, context)
        happy path
        """() {
        given:
        def now = LocalDateTime.now()

        when:
        def log = new ApplicationLog(now, ApplicationLogLevel.ERROR, "i am way longer than 30 characters", "0123456789" * 100, "i am shorter than 30 char", [x: 1, y: 3])

        then:
        log.timestamp == now
        log.level == ApplicationLogLevel.ERROR
        log.type == "i am way longer than 30 charac"
        log.message == "0123456789" * 20
        log.applicationId == "i am shorter than 30 char"
        log.context["x"] == 1
        log.context["y"] == 3
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
    }
}
