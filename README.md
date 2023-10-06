## Commuty API - Parking Access

This package acts as a client to communicate with the Commuty Parking Access API. It avoids you to deal with HTTP directly and provides you proper data structures to work with the API.

Supported features are:
* Authenticate with Credentials
* Verify a user can access a parking site
* List all access rights for a day
* Report who entered/exited the parking
* Report a user that is known by you but not by Commuty
* Report the number of available (and total) spots on a parking site

## Compatibility

This library requires a minimum `Java 8` version to work.

It has a very small amount of dependencies:
* `com.fasterxml.jackson.core:jackson-databind:2.10.3`
* `com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.10.3`
* `org.slf4j:slf4j-api:1.7.29`

## Integrate the library in your project

To use this library in your project, you can do it via a maven dependency:

```xml
<dependency>
    <groupId>net.commuty</groupId>
    <artifactId>parking-access-api-client</artifactId>
    <version>2.6.0</version>
</dependency>
```

or via a gradle:

```
implementation "net.commuty:parking-access-api-client:2.6.0"
```

## Usage

To instantiate a client using the username/password Commuty provided to you, simple use the following statement:

```java
package net.commuty.parking;

import net.commuty.parking.Configuration;
import net.commuty.parking.ParkingAccess;

public class Example {

    public static void main(String[] args) {
        ParkingAccess client = Configuration.Builder.buildDefault("a-username", "a-password").toRestClient();
    }
}

```

However, if you need to configure more options (to configure a proxy for instance), there is a convenient builder present for you:

```java
import net.commuty.parking.ParkingAccess;
import net.commuty.parking.configuration.Client;

import java.net.InetSocketAddress;
import java.net.Proxy;

public class Example {
    public static void main(String[] args) {
        String username = "a-username";
        String password = "a-password";
        String host = "https://parking-access.commuty.net";
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.0.0.1", 8080));
        ParkingAccess client = Configuration.Builder
                .create()
                .withCredentials(username, password)
                .withRetryStrategy(5, 500) // 5 retries (in case of a network error , wait during 500ms between each attempt
                .withTimeout(5000, 10000) // wait 5 seconds to get a connection before failing, wait 10 seconds for a response before failing
                .withHost(host)
                .withProxy(proxy)
                .build()
                .toRestClient();
    }
}
```

## Authentication

The client will automatically handle the authentication against the server (i.e. retrieve or refresh the token if needed).

In the event of your credentials are not valid (wrong username, wrong password, revoked access), a call on any method on the client will throw a `CredentialsException`.

## Feature availability

While this client exposes all features available, you will only be able to use the ones Commuty has given you the rights.

## Examples of usage

This section will display every possible call that the client can make against the API.  
Every example will start with the initialisation of the client. You can of course re-use it instead of initializing it every time.

### verify if a user can access a parking site

```java
import net.commuty.parking.Configuration;
import net.commuty.parking.ParkingAccess;
import net.commuty.parking.http.CredentialsException;
import net.commuty.parking.http.HttpClientException;
import net.commuty.parking.http.HttpRequestException;
import net.commuty.parking.model.UserId;

public class Example {

    public static void main(String[] args){
        ParkingAccess client = Configuration.Builder.create().withCredentials("a-username", "a-password").build().toRestClient();
        try {
            boolean isAllowed = client.isGranted("a-parking-site-id", UserId.fromEmail("somemone@your-company.net"));
            if (isAllowed) {
                // do something that will open a gate...
            } else {
                // report the invalid access attempt...
            }
        } catch (CredentialsException credentialsException) {
            // Your username or password is invalid
        } catch (HttpRequestException httpRequestException) {
            // The server accepted the request but there was an issue (malformed, server error,...)
            // You might get more information via the getErrorResponse and getHttpResponseCode methods.
            System.err.println(httpRequestException.getErrorResponse());
            System.err.println(httpRequestException.getHttpResponseCode());
        } catch (HttpClientException httpClientException) {
            // There was an error in the client (request timeout, proxy issue,...)
            httpClientException.printStackTrace();
        }
    }
}
```

### List all parking access rights

```java
import net.commuty.parking.Configuration;
import net.commuty.parking.ParkingAccess;
import net.commuty.parking.http.ApiException;
import net.commuty.parking.http.CredentialsException;
import net.commuty.parking.model.AccessRight;

import java.time.LocalDate;
import java.util.Collection;

public class Example {

    public static void main(String[] args){
        ParkingAccess client = Configuration.Builder.create().withCredentials("a-username", "a-password").build().toRestClient();
        try {
            // retrieve all accesses for today (unread and already read)
            Collection<AccessRight> accessesOfTheDay = client.listAccessRightsForToday();

            // later today, retrieve newly created or updated accesses
            boolean unreadOnly = true;
            Collection<AccessRight> newAccessesOfTheDay = client.listAccessRightsForToday(unreadOnly);

            // or you can get accesses (read or unread only) for a specific day:
            Collection<AccessRight> accessRights = client.listAccessRights(LocalDate.of(2019, 10, 10), false);

        } catch (CredentialsException credentialsException) {
            // Your username or password is invalid
        } catch (ApiException apiException) {
            // Instead of catching HttpRequestException and HttpClientException,
            // you can catch the superclass ApiException and handle only one issue.
            System.err.println(apiException.getMessage());
            apiException.printStackTrace();
        }
    }
}

```

### Report who entered/exited the parking

```java
import net.commuty.parking.Configuration;
import net.commuty.parking.ParkingAccess;
import net.commuty.parking.http.ApiException;
import net.commuty.parking.http.CredentialsException;
import net.commuty.parking.model.AccessLog;
import net.commuty.parking.model.UserId;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

public class Example {

    public static void main(String[] args){
        ParkingAccess client = Configuration.Builder.create().withCredentials("a-username", "a-password").build().toRestClient();
        try {
            // create one (or more) access log to report to Commuty
            Collection<AccessLog> accessLogs = Arrays.asList(
                    AccessLog.createInAccessLog(UserId.fromPinCode("1111"), LocalDateTime.of(2019, 10, 10, 13, 37, 14)),
                    AccessLog.createOutAccessLog(UserId.fromBadgeNumber("11223344"), LocalDateTime.of(2019, 10, 10, 21, 23, 12))
                    // etc 
            );

            // report them to Commuty
            String logId = client.reportAccessLog("a-parking-site-id", accessLogs);

            // you can save the logId for future reference or simply discard it

        } catch (CredentialsException credentialsException) {
            // Your username or password is invalid
        } catch (ApiException apiException) {
            // Instead of catching HttpRequestException and HttpClientException,
            // you can catch the superclass ApiException and handle only one issue.
            System.err.println(apiException.getMessage());
            apiException.printStackTrace();
        }
    }
}
```

### Report a user that is known by you but not by Commuty

```java
import net.commuty.parking.Configuration;
import net.commuty.parking.ParkingAccess;
import net.commuty.parking.http.ApiException;
import net.commuty.parking.http.CredentialsException;
import net.commuty.parking.model.UserId;

public class Example {

    public static void main(String[] args){
        ParkingAccess client = Configuration.Builder.create().withCredentials("a-username", "a-password").build().toRestClient();
        try {
            // send every user that is known by you, one user at a time
            client.reportMissingUserId(UserId.fromLicensePlate("1-ABC-123"));

        } catch (CredentialsException credentialsException) {
            // Your username or password is invalid
        } catch (ApiException apiException) {
            // Instead of catching HttpRequestException and HttpClientException,
            // you can catch the superclass ApiException and handle only one issue.
            System.err.println(apiException.getMessage());
            apiException.printStackTrace();
        }
    }
}
```

### Report the number of available (and total) spots to Commuty 

```java
import net.commuty.parking.Configuration;
import net.commuty.parking.ParkingAccess;
import net.commuty.parking.http.ApiException;
import net.commuty.parking.http.CredentialsException;

public class Example {

    public static void main(String[] args){
        ParkingAccess client = Configuration.Builder.create().withCredentials("a-username", "a-password").build().toRestClient();
        try {
            int availableSpots = 13;
            Integer totalSpots = 40; // can be null
            client.reportAvailableSpotCount("a-parking-site-id", availableSpots, totalSpots);

        } catch (CredentialsException credentialsException) {
            // Your username or password is invalid
        } catch (ApiException apiException) {
            // Instead of catching HttpRequestException and HttpClientException,
            // you can catch the superclass ApiException and handle only one issue.
            System.err.println(apiException.getMessage());
            apiException.printStackTrace();
        }
    }
}
```


## Development

Build with JDK Temurin 17

### Release

```shell
./gradlew clean build
./gradlew publish
```
