# Xray Maven Plugin

[![build workflow](https://github.com/Xray-App/xray-maven-plugin/actions/workflows/CI.yml/badge.svg)](https://github.com/Xray-App/xray-maven-plugin/actions/workflows/CI.yml)
[![license](https://img.shields.io/badge/License-BSD%203--Clause-green.svg)](https://opensource.org/licenses/BSD-3-Clause)
[![Gitter chat](https://badges.gitter.im/gitterHQ/gitter.png)](https://gitter.im/Xray-App/community)

This is an open-source maven plugin for interacting with Xray (server/datacenter and cloud), used in CI/CD for assisting in test automation flows, such as reporting test results back to Xray and, by consequence, Jira.

This plugin is a totally new implementation, made from scratch, and should not be mistaken with the [proprietary maven plugin originaly provided by Xray](https://docs.getxray.app/display/XRAY/Integration+with+Maven). It makes use of the public APIs provided by Xray (see references at bottom of this page), which we recommend for you to have a look as these may have some constraints that also apply to this plugin.

The Xray Maven Plugin is maintained by and in the open-source spirit.

The general flow for importing test automation results is as simple as submiting them back to Xray.
For Gherkin-based frameworls (e.g., Cucumber, Behave, SpecFlow), the flow is slightly different and require additional steps (more on these flows [here](https://docs.getxray.app/pages/viewpage.action?pageId=31622264#TestinginBDDwithGherkinbasedframeworks(e.g.Cucumber)-Workflows)).

This plugin provides these tasks:

- `xray:import-results`
- `xray:import-features`
- `xray:export-features`

More info about these ahead.

## How to use

To start using this plugin, configure this maven repository in your `pom.xml`:

```xml
        <repository>
            <id>github</id>
            <url>https://maven.pkg.github.com/Xray-App/*</url>
            <snapshots>
              <enabled>true</enabled>
            </snapshots>
        </repository>
```

... or within the `<pluginRepositories>` section.

In your `.m2/settings.xml`, configure the authentication for the maven repository. You'll need a [GitHub Personal Access Token](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token).

```xml
 <servers>
    <server>
      <id>github</id>
      <username>GITHUB_USERNAME</username>
      <password>GITHUB_PERSONAL_TOKEN</password>
    </server>
    ...
</servers>
```

Add the following dependency to your pom.xml, where the `<configuration>` is optional and should be adapted to your use case.

```xml
        <dependency>
            <groupId>com.xblend</groupId>
            <artifactId>xray-maven-plugin</artifactId>
            <version>0.4.0</version>
            <scope>test</scope>
            <configuration>
                <clientId>215FFD69FE46447280000000000</clientId>
                <clientSecret>1c00f8f22f56a8684d7c18cd61470000000</clientSecret>
                <cloud>true</cloud>
                <projectKey>CALC</projectKey>
                <fixVersion>1.0</fixVersion>
                <reportFormat>junit</reportFormat>
                <reportFile>target/junit.xml</reportFile>
            </configuration>
        </dependency>
```

Configuration parameters (e.g., "reportFormat") can also be specified from the command line using `-D` (e.g., "-Dxray.reportFormat=junit"). In this case, the parameters have the `xray.` prefix.

There are a set of common configurations related to the Xray details and its authentication. Besides, each task has its own configuration parameters as shown ahead.

This repository provides a bunch of usage [examples](examples), both for Xray cloud and Xray server/datacenter.

### Common configurations

For Xray server/datacenter users (i.e., using Xray on Jira server/datacenter):

| parameter | command line parameter| description | example |
| --- | --- | --- | --- |
| `cloud` | `xray.cloud` | set to false if not using Xray cloud (default: false) | false |
| `jiraBaseUrl` | `xray.jiraBaseUrl` | Jira server/DC base URL | http://10.0.0.1 |
| `jiraUsername` | `xray.jiraUsername` | username of Jira user to use on API calls | someuser |
| `jiraPassword` | `xray.jiraPassword` | password of Jira user to use on API calls | somepass |
| `jiraToken` | `xray.jiraToken` | Jira PAT (Personal Access Token) used instead of username/password |
| `ignoreSslErrors` | `xray.ignoreSslErrors` | ignore SSL errors, e.g., expired certificate (default: false) |
| `timeout` | `xray.timeout` | connection timeout in seconds (default: 50) |

For Xray cloud users (i.e., using Xray on Jira cloud):

| parameter | command line parameter| description | example |
| --- | --- | --- | --- |
| `cloud` | `xray.cloud` | set to true if using Xray cloud (default: false) | true |
| `clientId` | `xray.clientId` | client id of the API key configured on Xray Cloud | xxxx... |
| `clientSecret` | `xray.clientSecret` | client id of the API key configured on Xray Cloud | xxxx... |
| `timeout` | `xray.timeout` | connection timeout in seconds (default: 50) | 50 |

In order to obtain the API key (client id + client secret pair) please ask you Jira admin (see reference at bottom).
There are also task specific configurations. More info ahead, on the respective task section.

## Tasks

### Importing test automation results

In order to import test results, we need to use the `xray:import-results` task.

```bash
 mvn clean compile test xray:import-results
```

The `pom.xml` needs to be configured properly (see available configurations).
As an alternative to hardcode the configurations, it's also possible to pass them right from the command line as mentioned earlier, or even have some on the pom.xml and another specificed through command line parameters.

```bash
 mvn clean compile test xray:import-results -Dxray.reportFormat=junit -Dxray.reportFile=results/junit.xml
```

#### Configurations for importing test results

There are two ways of importing results.
The first one is more simple and also known as "standard" (due to how it is called in terms of REST API) where we provide all or a mix of predefined and common parameters (projectKey, version, revision, testPlanKey, testExecKey, testEnvironment) that are enough for most usage scenarios.
There is another way importing results though, that allow us to customize any field on the Test Execution issue or even on the Test issues that may be created; in this case, we need to pass the `testExecInfoJson` and/or the `testInfoJson` fields. This approach, also known has "multipart" due to the endpoint it uses, even though more flexible will require us to specify common fields such as the project key, version, and other, within the respective JSON field.

In sum, if we decide to use the first approach we can use these parameters: `projectKey`, `version`, `revision`, `testPlanKey`, `testExecKey`, `testEnvironment`. If we decide to use the second approach to either customize the target Test Execution or Test issues, then we can only use the `testInfoJson` and `testExecInfoJson` parameters.
**For this reason, it's not possible to use the parameters `projectKey`, `version`, `revision`, `testPlanKey`, `testExecKey`, `testEnvironment` together with `testInfoJson` and `testExecInfoJson` at the same time.

| setting | command line parameter| description | mandatory/optional| example |
| --- | --- | --- | --- | --- |
| `reportFormat` | `xray.reportFormat` | format of the report (junit, testng, nunit, xunit, robot, xunit, cucumber, behave) | mandatory | junit |
| `reportFile` | `xray.reportFile` | file with the test results (relative or absolute path); it can also be a directory (all .xml files will be imported in this case); finally, it can also be a regex that applies to the current working directory | mandatory | target/junit.xml |
| `projectKey` | `xray.projectKey` | key of Jira project where to import the results | mandatory (doesn't apply to "cucumber" or "behave" report formats, for legacy reasons) | CALC |
| `testExecKey` | `xray.testExecKey` | issue key of Test Execution, in case we want to update the results on it | optional | CALC-2 |
| `testPlanKey` | `xray.testPlanKey` | issue key of Test Plan to link the results to | optional |CALC-1 |
| `version` | `xray.version` | version of the SUT, that corresponds to the Jira project version/release; it will be assigned to the Test Execution issue using the "fixVersion(s)" field | optional | 1.0 |
| `revision` | `xray.revision` | source code revision or a build identifier | optional | 123 |
| `testEnvironment` | `xray.testEnvironment` | usually, a [test environment](https://docs.getxray.app/display/XRAYCLOUD/Working+with+Test+Environments) name/identifier (e.g., browser vendor, OS versio , mobile device, testing stage); multiple test environments may be specified though using ";" as delimiter | optional | chrome |
| `testInfoJson` | `xray.testInfoJson` | path to a JSON file containing attributes to apply on the Test issues that may be created, following Jira issue update syntax | optional | - |
| `testExecInfoJson` | `xray.testExecInfoJson` | path to a JSON file containing attributes to apply on the Test Execution issue that may be created, following Jira issue update syntax | optional | - |
| `abortOnError` | abort, if multiple results are being imported, and exit with error if uploading results fails | optional | - |

Xray server/DC and Xray cloud support mostly the same formats, but not exactly for legacy reasons. Besides, not all formats support the same parameters; please check the respective product documentation. The following table sums this info.

|reportFormat| supported Xray variant| notes |
| --- | --- | --- |
| xray | cloud and server/DC |  |
| junit | cloud and server/DC | |
| xunit | cloud and server/DC | |
| nunit | cloud and server/DC | |
| robot | cloud and server/DC | |
| testng | cloud and server/DC | |
| cucumber | cloud and server/DC | in this specific case, it's not possible to use the parameters `projectKey`, `version`, `revision`, `testPlanKey`, `testExecKey`, `testEnvironment` (due to the way the underlying "standard" endpoint for Cucumber works)  |
| behave | cloud and server/DC | in this specific case, it's not possible to use the parameters `projectKey`, `version`, `revision`, `testPlanKey`, `testExecKey`, `testEnvironment` (due to the way the underlying "standard" endpoint for Cucumber works) |

#### Technical info of how it works (internal)

This task makes use of different REST APIs for importing test automation results (see reference at the bottom).
Related to each report format (e.g., junit, testng, etc) there are usually 2 endpoints for submiting test results: the so called "standard" and the "multipart" one. Whenever importing results, for a given report format, the plugin will use one of these endpoints based on whether `testInfoJson` or `testExecInfoJson` are provided; if none of them are provided, the "standard" endpoint, otherwise the "multipart" endpoint will be used.

### Importing/synchronizing Cucumber .feature files to Xray

One of the possible workflows for using Gherkin-based frameworks is to use git (or other versioning control system) as the master to store the corresponding .feature files (more info [here](https://docs.getxray.app/pages/viewpage.action?pageId=31622264#TestinginBDDwithGherkinbasedframeworks(e.g.Cucumber)-PureVCSbasedworkflow)).
In order to provide visibility of test results for these tests (i.e. gherkin Scenarios), these need to exist in Xray beforehand. Therefore, we need to import/synchronize them to Xray. Note that there is no direct integration; the integration is adhoc, i.e., the following task is run on a local copy of the respository where the .features are stored in.

```bash
 mvn clean compile test xray:import-features -Dxray.featuresPath=features/
```

Note: how Xray relates the Scenarios/Background to the corresponding Test or Precondition issues is described in Xray technical documentation (e.g., [Xray cloud docs](https://docs.getxray.app/display/XRAYCLOUD/Importing+Cucumber+Tests+-+REST+v2)).

#### Configurations for importing/synchronizing Cucumber .feature files to Xray

| parameter | command line parameter| description | mandatory/optional| example |
| --- | --- | --- | --- | --- |
| `projectKey` | `xray.projectKey` | key of Jira project where to import the Cucumber Scenarios/Backgrounds as Test and Precondition issues | mandatory (if projectId not used) | CALC |
| `projectId` | `xray.projectId` | **Xray cloud only**: id of Jira project where to import the Cucumber Scenarios/Backgrounds as Test and Precondition issues | mandatory (if projectKey not used) | 1000 |
| `source` | `xray.source` | ?? | optional | ?? |
| `testInfoJson` | `xray.testInfoJson` | path to a JSON file containing attributes to apply on the Test issues that may be created, following Jira issue update syntax | optional | - |
| `precondInfoJson` | `xray.precondInfoJson` | path to a JSON file containing attributes to apply on the Precondition issues that may be created, following Jira issue update syntax | optional | - |
| `inputFeatures` | `xray.inputFeatures` | either a .feature file, a directory containing .feature files, or a zipped file containing .feature files| mandatory | features/ |
| `updateRepository` | `xray.updateRepository` | create folder structure in Test Repository based on the folder structure in the .zip file containing the .feature files; default is false. Only supported on Xray server/DC. | optional | true |

#### Technical info of how it works (internal)

This task makes use of a specific REST API avaiable for importing .feature files (for legacy reasons, also known as "importing Cucumber tests"). The .feature files are zipped and sent in one REST API call made to Xray.
Link to the REST API endpoint details at the bottom (both for server/datacenter and cloud).

### Exporting/generating Cucumber .feature files from Xray

No matter which worflow you decide to use for dealing with Gherkin-based tests (e.g., Cucumber .feature files and corresponding Scenarios), as part of that workflow comes the need to extract/generate the .feature files based on Scenarios or Backgrounds detailed in Xray using Test or Precondition issues.
This plugin provides a task for this purpose. It will download/generate .feature files to a local folder from existing information in Xray. The Feature, Scenario elements will be properly tagged with info from Xray.

```bash
 mvn xray:export-features -Dxray.issueKeys=CALC-1,CALC-2 -Dxray.outputDir=features/
```

Files on the destination folder will be overwritten; however, if this directory contains other information (including other .feature files) you may need to remove them before generating the .feature files into this directory.

Note: how Xray generates the .feature files with the Scenarios/Background from existing Test or Precondition issues is described in Xray technical documentation (e.g., [Xray cloud docs](https://docs.getxray.app/display/XRAYCLOUD/Generate+Cucumber+Features)).

#### Configurations for exporting/generating Cucumber .feature files from Xray

| parameter | command line parameter| description | mandatory/optional| example |
| --- | --- | --- | --- | --- |
| `issueKeys` | `xray.issueKeys` | issue keys of direct or indirect references to Cucumber/Gherkin tests/scenarios (e.g., Test issue keys), delimited by comma | mandatory (or optional if filterId is used instead) | CALC-1,CALC-2 |
| `filterId` | `xray.filterId` | id of the Jira filter containing direct or indirect references to Cucumber/Gherkin tests/scenarios| mandatory (or optional if issueKeys is used instead) | 12000 |
| `outputDir` | `xray.outputDir` | output directory where the .feature files should be extracted to | mandatory | features/ |

#### Technical info of how it works (internal)

This task makes use of a specific REST API avaiable for exporting/generating .feature files (for legacy reasons, also known as "exporting Cucumber tests").
Link to the REST API endpoint details at the bottom (both for Xray on Jira server/datacenter and cloud).

## FAQ

1. Is this the same maven plugin as the original one made by Xray team?

No. This is a totally new one, made from scratch and open-source.

2. If we have questions/support issues, where should those be addressed?

It's an open-source project, so it should be handled in this GitHub project and supported by the community. If you want to use the previous, proprietary plugin, you can do so and that has commercial support, if you have a valid license.

3. Are the underlying APIs the same for Xray server/datacenter and Xray Cloud? Are the available options the same? Are the supported test automation report formats the same?

Not exactly. Xray server/datacenter and Xray cloud, even though similar, are actually distinct products; besides Jira server/datacenter and Jira cloud are different between themselves and have different capabilities. This plugin makes use of the available REST APIs for Xray server/datancer and Xray cloud, so you should check them to see exactly what is supported for your environment.

## Contact

You may find me on [Twitter](https://twitter.com/darktelecom).
Any questions related with this code, please raise issues in this GitHub project. Feel free to contribute and submit PR's.
For Xray specific questions, please contact [Xray's support team](https://jira.getxray.app/servicedesk/customer/portal/2).

## Disclaimer

This project is in early stage; the setting names and other are subject to change.

## Acknowledgments

- Thanks to [Eugen](https://twitter.com/baeldung), and other authors, along with the many tutorials provided at [baeldung.com](https://www.baeldung.com).
- Thanks to Hugo Braz for all the valuable feedback and previous work done in the scope of the previous, proprietary Maven plugin for Xray.

## TO DOs

- review `pom.xml` and make it cleaner (help neeeded and appreciated!)
- clarify properly how to setup this in an existing project as some doubts remain about the proper way to do so in terms of `.m2/settings.xml` (help neeeded and appreciated!)
- add code coverage information with jacoco, as I couldnt get it working (help neeeded and appreciated!)
- add unit tests
- option to remove temporary .zip file created whenever importing Gherkin/cucumber .feature files
- ability to attach files/evidence to test runs (needs some thought)

## [Changelog](CHANGELOG.md)

## References

- [Importing test results (Xray server/datacenter)](https://docs.getxray.app/display/XRAY/Import+Execution+Results+-+REST)
- [Importing test results (Xray cloud)](https://docs.getxray.app/display/XRAYCLOUD/Import+Execution+Results+-+REST+v2)
- [Import Cucumber tests (Xray server/datacenter)](https://docs.getxray.app/display/XRAY/Importing+Cucumber+Tests+-+REST)
- [Import Cucumber tests (Xray cloud)](https://docs.getxray.app/display/XRAYCLOUD/Importing+Cucumber+Tests+-+REST+v2)
- [Export Cucumber tests (Xray server/datacenter)](https://docs.getxray.app/display/XRAY/Exporting+Cucumber+Tests+-+REST)
- [Export Cucumber tests (Xray cloud)](https://docs.getxray.app/display/XRAYCLOUD/Exporting+Cucumber+Tests+-+REST+v2)
- [Using Personal Access Tokens (Xray datacenter)](https://confluence.atlassian.com/enterprise/using-personal-access-tokens-1026032365.html)
- [API keys (client id + client secret pair) on Xray cloud](https://docs.getxray.app/display/XRAYCLOUD/Global+Settings%3A+API+Keys)

## LICENSE

[BSD 3-Clause](LICENSE)
