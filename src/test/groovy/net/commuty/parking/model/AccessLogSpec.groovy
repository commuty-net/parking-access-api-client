package net.commuty.parking.model

import groovy.json.JsonSlurper
import net.commuty.parking.configuration.JsonMapper
import spock.lang.Specification

import java.time.LocalDateTime

import static net.commuty.parking.model.AccessDirection.IN
import static net.commuty.parking.model.AccessDirection.OUT

class AccessLogSpec extends Specification {

    def mapper = JsonMapper.create()
    def reader = new JsonSlurper()

    def """
        #createInAccessLog(null userId, a date)
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
        #createInAccessLog(valid userId, null date)
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
        #createInAccessLog(valid userId, valid date)
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
        accessLog.userIdType == userId.userIdType
        accessLog.at == date
        accessLog.way == IN
    }

    def """
        #createInAccessLog(valid userId, valid date)
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
        #createOutAccessLog(valid userId, valid date)
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
        accessLog.userIdType == userId.userIdType
        accessLog.at == date
        accessLog.way == OUT
    }

    def """
        #createOutAccessLog(valid userId, valid date)
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
