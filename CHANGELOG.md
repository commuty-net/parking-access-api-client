# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
