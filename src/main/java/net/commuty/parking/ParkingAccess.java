package net.commuty.parking;

import net.commuty.parking.http.CredentialsException;
import net.commuty.parking.http.HttpClientException;
import net.commuty.parking.http.HttpRequestException;
import net.commuty.parking.model.AccessLog;
import net.commuty.parking.model.AccessRight;
import net.commuty.parking.model.UserId;

import java.time.LocalDate;
import java.util.Collection;

public interface ParkingAccess {

    String authenticate() throws CredentialsException, HttpRequestException, HttpClientException;

    boolean isGranted(String parkingSiteId, UserId user) throws CredentialsException, HttpRequestException, HttpClientException;

    Collection<AccessRight> listAccessRightsForToday() throws CredentialsException, HttpRequestException, HttpClientException;

    Collection<AccessRight> listAccessRightsForToday(boolean unreadOnly) throws CredentialsException, HttpRequestException, HttpClientException;

    Collection<AccessRight> listAccessRights(LocalDate date, Boolean unreadOnly) throws CredentialsException, HttpRequestException, HttpClientException;

    String reportAccessLog(String parkingSiteId, Collection<AccessLog> accessLogs) throws CredentialsException, HttpRequestException, HttpClientException;

    UserId reportMissingUserId(UserId user) throws CredentialsException, HttpRequestException, HttpClientException;

}
