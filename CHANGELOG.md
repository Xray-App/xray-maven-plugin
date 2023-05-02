# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

### Fixed

## [0.7.3] - 2023-05-02

### Fixed

- bump json dependency

## [0.7.2] - 2023-03-04

### Fixed

- add default values for command-line parameters (#58)

## [0.7.1] - 2023-02-02

### Fixed

- handling of multipart requests for null test/testExec info JSON (#54)

## [0.7.0] - 2022-07-01

### Added

- added "verbose" mode, to debug API requests/responses

### Fixed

- importing of results to Xray DC/server were not passing some arguments (e.g., version, revision,  testPlanKey, ...)

## [0.6.0] - 2022-06-08

### Added

- better error logging (i.e., checking file existence)

### Changed

### Fixed

- handling testInfoJson (which wasn't imlemented at all)

## [0.5.0] - 2022-06-03

### Added

- change groupid from com.xblend to app.getxray
- upload to Maven Central Repository, instead of using GitHub Packages
- update release instructions

### Changed

### Fixed

## [0.4.0] - 2022-05-17

### Added

- ability to organize the tests in Test Repository folders whenever importing tests from zipped Cucumber .feature files (Xray server/DC only)
- tests for importing/exporting Cucumber test scenarios

### Changed

### Fixed

## [0.3.0] - 2022-04-18

### Added

- support for Behave JSON endpoints on Xray Cloud

### Changed

### Fixed


## [0.2.0] - 2022-03-11

### Added

- timeout configuration
- start using this "changelog"

### Changed

## [0.1.0] - 2022-02-14

### Added

- initial version, providing ability to import results and import/export gherkin/cucumber features to/from Xray (server/DC and cloud)

### Changed

[unreleased]: https://github.com/Xray-App/xray-maven-plugin/compare/0.7.3...HEAD
[0.7.3]: https://github.com/Xray-App/xray-maven-plugin/compare/0.7.3...0.7.2
[0.7.2]: https://github.com/Xray-App/xray-maven-plugin/compare/0.7.2...0.7.1
[0.7.1]: https://github.com/Xray-App/xray-maven-plugin/compare/0.7.1...0.7.0
[0.7.0]: https://github.com/Xray-App/xray-maven-plugin/compare/0.7.0...0.7.0
[0.6.0]: https://github.com/Xray-App/xray-maven-plugin/compare/0.5.0...0.6.0
[0.5.0]: https://github.com/Xray-App/xray-maven-plugin/compare/0.4.0...0.5.0
[0.4.0]: https://github.com/Xray-App/xray-maven-plugin/compare/0.3.0...0.4.0
[0.3.0]: https://github.com/Xray-App/xray-maven-plugin/compare/0.2.0...0.3.0
[0.2.0]: https://github.com/Xray-App/xray-maven-plugin/compare/0.1.0...0.2.0
[0.1.0]: https://github.com/Xray-App/xray-maven-plugin/releases/tag/0.1.0
