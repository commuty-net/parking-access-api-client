package net.commuty.parking.model

import groovy.json.JsonSlurper
import net.commuty.parking.rest.JsonMapperTest
import spock.lang.Specification

import static net.commuty.parking.model.UserIdType.*

class UserIdSpec extends Specification {

    def mapper = JsonMapperTest.create()
    def reader = new JsonSlurper()

    def """
        #fromEmail(invalid email)
        throws an exception
        """() {
        when:
        UserId.fromEmail(email)

        then:
        thrown(IllegalArgumentException)

        where:
        email | _
        null  | _
        ""    | _
        " "   | _
    }

    def """
        #fromEmail(valid string)
        returns an user with UserIdType == EMAIL
        """() {
        given:
        def email = "anon@commuty.net"

        when:
        def userId = UserId.fromEmail(email)

        then:
        userId != null
        userId.id == email
        userId.type == EMAIL
    }

    def """
        #fromEmail(valid string)
        is parsed correctly
        """() {
        given:
        def email = "anon@commuty.net"

        when:
        def userIdString = mapper.write(UserId.fromEmail(email))

        then:
        userIdString != null
        def parsed = reader.parseText(userIdString)
        parsed != null
        parsed.id == email
        parsed.type == "email"
    }

    def """
        #fromLicensePlate(valid string)
        returns an user with UserIdType == LICENSE_PLATE
        """() {
        given:
        def licensePlate = "1-ABC-000"

        when:
        def userId = UserId.fromLicensePlate(licensePlate)

        then:
        userId != null
        userId.id == licensePlate
        userId.type == LICENSE_PLATE
    }

    def """
        #fromLicensePlate(valid string)
        is parsed correctly
        """() {
        given:
        def licensePlate = "1-ABC-000"

        when:
        def userIdString = mapper.write(UserId.fromLicensePlate(licensePlate))

        then:
        userIdString != null
        def parsed = reader.parseText(userIdString)
        parsed != null
        parsed.id == licensePlate
        parsed.type == "licensePlate"
    }

    def """
        #fromIdentificationNumber(valid string)
        returns an user with UserIdType == IDENTIFICATION_NUMBER
        """() {
        given:
        def identificationNumber = "1234567890"

        when:
        def userId = UserId.fromIdentificationNumber(identificationNumber)

        then:
        userId != null
        userId.id == identificationNumber
        userId.type == IDENTIFICATION_NUMBER
    }

    def """
        #fromIdentificationNumber(valid string)
        is parsed correctly
        """() {
        given:
        def identificationNumber = "1234567890"

        when:
        def userIdString = mapper.write(UserId.fromIdentificationNumber(identificationNumber))

        then:
        userIdString != null
        def parsed = reader.parseText(userIdString)
        parsed != null
        parsed.id == identificationNumber
        parsed.type == "identificationNumber"
    }

    def """
        #fromQrCode(valid string)
        returns an user with UserIdType == QR_CODE
        """() {
        given:
        def qrCode = "A1F200"

        when:
        def userId = UserId.fromQrCode(qrCode)

        then:
        userId != null
        userId.id == qrCode
        userId.type == QR_CODE
    }

    def """
        #fromQrCode(valid string)
        is parsed correctly
        """() {
        given:
        def qrCode = "A1F200"

        when:
        def userIdString = mapper.write(UserId.fromQrCode(qrCode))

        then:
        userIdString != null
        def parsed = reader.parseText(userIdString)
        parsed != null
        parsed.id == qrCode
        parsed.type == "qrCode"
    }

    def """
        #fromBadgeNumber(valid string)
        returns an user with UserIdType == BADGE_NUMBER
        """() {
        given:
        def badgeNumber = "A1F200"

        when:
        def userId = UserId.fromBadgeNumber(badgeNumber)

        then:
        userId != null
        userId.id == badgeNumber
        userId.type == BADGE_NUMBER
    }

    def """
        #fromBadgeNumber(valid string)
        is parsed correctly
        """() {
        given:
        def badgeNumber = "A1F200"

        when:
        def userIdString = mapper.write(UserId.fromBadgeNumber(badgeNumber))

        then:
        userIdString != null
        def parsed = reader.parseText(userIdString)
        parsed != null
        parsed.id == badgeNumber
        parsed.type == "badgeNumber"
    }

    def """
        #fromCardholderId(valid string)
        returns an user with UserIdType == CARDHOLDER_ID
        """() {
        given:
        def cardholderId = "666"

        when:
        def userId = UserId.fromCardholderId(cardholderId)

        then:
        userId != null
        userId.id == cardholderId
        userId.type == CARDHOLDER_ID
    }

    def """
        #fromCardholderId(valid string)
        is parsed correctly
        """() {
        given:
        def cardholderId = "666"

        when:
        def userIdString = mapper.write(UserId.fromCardholderId(cardholderId))

        then:
        userIdString != null
        def parsed = reader.parseText(userIdString)
        parsed != null
        parsed.id == cardholderId
        parsed.type == "cardholderId"
    }

    def """
        #fromPinCode(valid string)
        returns an user with UserIdType == PIN_CODE
        """() {
        given:
        def pinCode = "1111"

        when:
        def userId = UserId.fromPinCode(pinCode)

        then:
        userId != null
        userId.id == pinCode
        userId.type == PIN_CODE
    }

    def """
        #fromPinCode(valid string)
        is parsed correctly
        """() {
        given:
        def pinCode = "1111"

        when:
        def userIdString = mapper.write(UserId.fromPinCode(pinCode))

        then:
        userIdString != null
        def parsed = reader.parseText(userIdString)
        parsed != null
        parsed.id == pinCode
        parsed.type == "pinCode"
    }

    def """
        #fromWim64EncodedLicensePlate(valid string)
        returns an user with UserIdType == LICENSE_PLATE_WIM64
        """() {
        given:
        def win64EncodedLicensePlate = "F0F1F2F3"

        when:
        def userId = UserId.fromWim64EncodedLicensePlate(win64EncodedLicensePlate)

        then:
        userId != null
        userId.id == win64EncodedLicensePlate
        userId.type == LICENSE_PLATE_WIM64
    }

    def """
        #fromWim64EncodedLicensePlate(valid string)
        is parsed correctly
        """() {
        given:
        def win64EncodedLicensePlate = "F0F1F2F3"

        when:
        def userIdString = mapper.write(UserId.fromWim64EncodedLicensePlate(win64EncodedLicensePlate))

        then:
        userIdString != null
        def parsed = reader.parseText(userIdString)
        parsed != null
        parsed.id == win64EncodedLicensePlate
        parsed.type == "licensePlateWim64"
    }

}
