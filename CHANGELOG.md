# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.5.5] 2023-07-18

* Support for flag `subjectId`  on `/v2/access-rights`.

## [2.5.4] 2023-07-05

* Added the value `freeAccess` in the AccessRight's `reason` property.

## [2.5.3] 2023-02-20

* Support for flag `granted` and `parkingSiteId`  on `/v2/access-rights`.

## [2.5.2] 2023-02-14

* Support for flag `isVisitor`  on `/v2/access-rights`.

## [2.5.1] 2022-08-23

* Support for GraalVM native builds

## [2.5.0] 2022-08-10

* Added `forCarpoolersOnly`, `forDisabled` and `large` properties on parking spots.

## [2.4.0] 2022-15-12

* Expose and make connection timeout and request timeout configurable
* Build with JDK 17
* Upgrade `jackson` to `2.13.2`

## [2.3.2] 2021-10-21

* Support for UserIdType `LICENSE_PLATE_WIM26_AS_DECIMAL_CODE` and `LICENSE_PLATE_WIM26_AS_FACILITY_AND_ID_CODE`
* Removed `LICENSE_PLATE_WIM26`

## [2.3.1] 2021-08-30

* Support for UserIdType `ACCESS_CODE`

## [2.3.0] 2021-07-29

* Support for flags `parkingSpotId`, `parkingSpotName`, `parkingSpotDisplayName` of `includeAttributes`  on `/v2/access-rights`.
* New endpoint to list all the parking spots of a given parking site.

## [2.2.2] 2021-03-10

* Ignore empty properties in AccessRights

## [2.2.1] 2021-03-09

* Support for flag `createdAfter` and `includeAttributes` on `/access-rights`
* Serializable classes to receive and send webhook messages/errors

## [2.1.6] 2021-01-26

* Support for flag `dryRun` on `/access-rights`

## [2.1.5] - 2020-11-01

* Expose and make the retry strategy configurable

## [2.1.4] - 2020-10-12

* New `UserIdType`: commuty label

## [2.1.3] - 2020-09-07

* New `UserIdType`: commuty external id

## [2.1.2] - 2020-09-07

* `UserId` equality

## [2.1.1] - 2020-09-04

* Give access to `UserId`'s constructor

## [2.1.0] - 2020-04-02

* Support for JDK 14

## [2.0.1] - 2020-01-08

* Support for AccessLog's properties:  `granted` and `reason`
