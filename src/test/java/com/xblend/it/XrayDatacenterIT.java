package com.xblend.it;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.extension.SystemProperty;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import com.github.tomakehurst.wiremock.client.BasicCredentials; 

@MavenJupiterExtension
// @WireMockTest(httpPort = 18080, httpsEnabled = false, proxyMode = false)
public class XrayDatacenterIT {


/*
    @RegisterExtension
    static WireMockExtension wm = WireMockExtension.newInstance()
            .options(WireMockConfig().dynamicPort().dynamicHttpsPort())
            .build();
*/

    static WireMockServer wm;
    @BeforeAll
    public static void setup () {
        wm = new WireMockServer(options().port(18080));//.enableBrowserProxying(true));
        wm.start();
        setupStub();
    }

    @AfterAll
    public static void teardown () {
        wm.stop();
    }

    public static void setupStub() {
        System.out.println("setting up stubs...");

        // xray
        wm.stubFor(post(urlPathEqualTo("/rest/raven/2.0/import/execution"))
            .willReturn(okJson("{ \"testExecIssue\": { \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" } }")));
        wm.stubFor(post(urlPathEqualTo("/rest/raven/2.0/import/execution/multipart"))
            .willReturn(okJson("{ \"testExecIssue\": { \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" } }")));

        // junit
        wm.stubFor(post(urlPathEqualTo("/rest/raven/2.0/import/execution/junit"))
            .willReturn(okJson("{ \"testExecIssue\": { \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" } }")));
        wm.stubFor(post(urlPathEqualTo("/rest/raven/2.0/import/execution/junit/multipart"))
            .willReturn(okJson("{ \"testExecIssue\": { \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" } }")));

        // testng
        wm.stubFor(post(urlPathEqualTo("/rest/raven/2.0/import/execution/testng"))
            .willReturn(okJson("{ \"testExecIssue\": { \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" } }")));
        wm.stubFor(post(urlPathEqualTo("/rest/raven/2.0/import/execution/testng/multipart"))
            .willReturn(okJson("{ \"testExecIssue\": { \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" } }")));

        // nunit
        wm.stubFor(post(urlPathEqualTo("/rest/raven/2.0/import/execution/nunit"))
            .willReturn(okJson("{ \"testExecIssue\": { \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" } }")));
        wm.stubFor(post(urlPathEqualTo("/rest/raven/2.0/import/execution/nunit/multipart"))
            .willReturn(okJson("{ \"testExecIssue\": { \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" } }")));

        // xunit
        wm.stubFor(post(urlPathEqualTo("/rest/raven/2.0/import/execution/xunit"))
            .willReturn(okJson("{ \"testExecIssue\": { \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" } }")));
        wm.stubFor(post(urlPathEqualTo("/rest/raven/2.0/import/execution/xunit/multipart"))
            .willReturn(okJson("{ \"testExecIssue\": { \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" } }")));

        // robot
        wm.stubFor(post(urlPathEqualTo("/rest/raven/2.0/import/execution/robot"))
            .willReturn(okJson("{ \"testExecIssue\": { \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" } }")));
        wm.stubFor(post(urlPathEqualTo("/rest/raven/2.0/import/execution/robot/multipart"))
            .willReturn(okJson("{ \"testExecIssue\": { \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" } }")));

        // cucumber
        wm.stubFor(post(urlPathEqualTo("/rest/raven/2.0/import/execution/cucumber"))
            .willReturn(okJson("{ \"testExecIssue\": { \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" } }")));
        wm.stubFor(post(urlPathEqualTo("/rest/raven/2.0/import/execution/cucumber/multipart"))
            .willReturn(okJson("{ \"testExecIssue\": { \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" } }")));

        // behave
        wm.stubFor(post(urlPathEqualTo("/rest/raven/2.0/import/execution/behave"))
            .willReturn(okJson("{ \"testExecIssue\": { \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" } }")));
        wm.stubFor(post(urlPathEqualTo("/rest/raven/2.0/import/execution/behave/multipart"))
            .willReturn(okJson("{ \"testExecIssue\": { \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" } }")));

        System.out.println("setting up stubs - done.");
    }

    @MavenTest
    @MavenGoal("xray:import-results")
    @SystemProperty(value = "xray.cloud", content = "false")
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:18080")
    @SystemProperty(value = "xray.jiraUsername", content = "username")
    @SystemProperty(value = "xray.jiraPassword", content = "password")
    @SystemProperty(value = "xray.reportFormat", content = "xray")
    @SystemProperty(value = "xray.reportFile", content = "xray.json")
    void xray_standard(MavenExecutionResult result) throws IOException {
       String report = CommonUtils.readResourceFile("XrayDatacenterIT/xray_standard/xray.json");
   
       wm.verify(
           postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/execution"))
               .withBasicAuth(new BasicCredentials("username", "password"))
               .withHeader("Content-Type", containing("application/json"))
               .withRequestBody(equalToJson(report))
       );
       assertThat(result).isSuccessful();
    }
   
    @MavenTest
    @MavenGoal("xray:import-results")
    @SystemProperty(value = "xray.cloud", content = "false")
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:18080")
    @SystemProperty(value = "xray.jiraUsername", content = "username")
    @SystemProperty(value = "xray.jiraPassword", content = "password")
    @SystemProperty(value = "xray.reportFormat", content = "xray")
    @SystemProperty(value = "xray.reportFile", content = "xray.json")
    @SystemProperty(value = "xray.testExecInfoJson", content = "testExecInfo.json")
    void xray_multipart(MavenExecutionResult result) throws IOException {
       String testExecInfo = CommonUtils.readResourceFile("XrayDatacenterIT/testng_multipart/testExecInfo.json");
       String report = CommonUtils.readResourceFile("XrayDatacenterIT/xray_multipart/xray.json");
   
       wm.verify(
           postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/execution/multipart"))
               .withBasicAuth(new BasicCredentials("username", "password"))
               .withHeader("Content-Type", containing("multipart/form-data;"))
               .withAnyRequestBodyPart(
                   aMultipart()
                       .withName("result")
                       .withHeader("Content-Type", containing("application/json"))
                       .withBody(equalToJson(report)))
               .withAnyRequestBodyPart(
                   aMultipart()
                       .withName("info")
                       .withHeader("Content-Type", containing("application/json"))
                       .withBody(equalToJson(testExecInfo))
                   )
   
       );
       assertThat(result).isSuccessful();
    }

 @MavenTest
 @MavenGoal("xray:import-results")
 @SystemProperty(value = "xray.cloud", content = "false")
 @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:18080")
 @SystemProperty(value = "xray.jiraUsername", content = "username")
 @SystemProperty(value = "xray.jiraPassword", content = "password")
 @SystemProperty(value = "xray.reportFormat", content = "junit")
 @SystemProperty(value = "xray.reportFile", content = "junit.xml")
 @SystemProperty(value = "xray.projectKey", content = "CALC")
 void junit_standard(MavenExecutionResult result) throws IOException {
    String report = CommonUtils.readResourceFile("XrayDatacenterIT/junit_standard/junit.xml");

    wm.verify(
        postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/execution/junit"))
            .withBasicAuth(new BasicCredentials("username", "password"))
            .withHeader("Content-Type", containing("multipart/form-data;"))
            .withQueryParam("projectKey", equalTo("CALC"))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("file")
                    .withHeader("Content-Type", containing("application/xml"))
                    .withBody(equalToXml(report)))
    );
    assertThat(result).isSuccessful();
 }

 @MavenTest
 @MavenGoal("xray:import-results")
 @SystemProperty(value = "xray.cloud", content = "false")
 @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:18080")
 @SystemProperty(value = "xray.jiraUsername", content = "username")
 @SystemProperty(value = "xray.jiraPassword", content = "password")
 @SystemProperty(value = "xray.reportFormat", content = "junit")
 @SystemProperty(value = "xray.reportFile", content = "junit.xml")
 @SystemProperty(value = "xray.testExecInfoJson", content = "testExecInfo.json")
 void junit_multipart(MavenExecutionResult result) throws IOException {
    String testExecInfo = CommonUtils.readResourceFile("XrayDatacenterIT/junit_multipart/testExecInfo.json");
    String report = CommonUtils.readResourceFile("XrayDatacenterIT/junit_multipart/junit.xml");

    wm.verify(
        postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/execution/junit/multipart"))
            .withBasicAuth(new BasicCredentials("username", "password"))
            .withHeader("Content-Type", containing("multipart/form-data;"))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("file")
                    .withHeader("Content-Type", containing("application/xml"))
                    .withBody(equalToXml(report)))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("info")
                    .withHeader("Content-Type", containing("application/json"))
                    .withBody(equalToJson(testExecInfo))
                )

    );
    assertThat(result).isSuccessful();
 }

 @MavenTest
 @MavenGoal("xray:import-results")
 @SystemProperty(value = "xray.cloud", content = "false")
 @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:18080")
 @SystemProperty(value = "xray.jiraUsername", content = "username")
 @SystemProperty(value = "xray.jiraPassword", content = "password")
 @SystemProperty(value = "xray.reportFormat", content = "testng")
 @SystemProperty(value = "xray.reportFile", content = "testng.xml")
 @SystemProperty(value = "xray.projectKey", content = "CALC")
 void testng_standard(MavenExecutionResult result) throws IOException {
    String report = CommonUtils.readResourceFile("XrayDatacenterIT/testng_standard/testng.xml");

    wm.verify(
        postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/execution/testng"))
            .withBasicAuth(new BasicCredentials("username", "password"))
            .withHeader("Content-Type", containing("multipart/form-data;"))
            .withQueryParam("projectKey", equalTo("CALC"))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("file")
                    .withHeader("Content-Type", containing("application/xml"))
                    .withBody(equalToXml(report)))
    );
    assertThat(result).isSuccessful();
 }

 @MavenTest
 @MavenGoal("xray:import-results")
 @SystemProperty(value = "xray.cloud", content = "false")
 @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:18080")
 @SystemProperty(value = "xray.jiraUsername", content = "username")
 @SystemProperty(value = "xray.jiraPassword", content = "password")
 @SystemProperty(value = "xray.reportFormat", content = "testng")
 @SystemProperty(value = "xray.reportFile", content = "testng.xml")
 @SystemProperty(value = "xray.testExecInfoJson", content = "testExecInfo.json")
 void testng_multipart(MavenExecutionResult result) throws IOException {
    String testExecInfo = CommonUtils.readResourceFile("XrayDatacenterIT/testng_multipart/testExecInfo.json");
    String report = CommonUtils.readResourceFile("XrayDatacenterIT/testng_multipart/testng.xml");

    wm.verify(
        postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/execution/testng/multipart"))
            .withBasicAuth(new BasicCredentials("username", "password"))
            .withHeader("Content-Type", containing("multipart/form-data;"))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("file")
                    .withHeader("Content-Type", containing("application/xml"))
                    .withBody(equalToXml(report)))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("info")
                    .withHeader("Content-Type", containing("application/json"))
                    .withBody(equalToJson(testExecInfo))
                )

    );
    assertThat(result).isSuccessful();
 }


 @MavenTest
 @MavenGoal("xray:import-results")
 @SystemProperty(value = "xray.cloud", content = "false")
 @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:18080")
 @SystemProperty(value = "xray.jiraUsername", content = "username")
 @SystemProperty(value = "xray.jiraPassword", content = "password")
 @SystemProperty(value = "xray.reportFormat", content = "nunit")
 @SystemProperty(value = "xray.reportFile", content = "nunit.xml")
 @SystemProperty(value = "xray.projectKey", content = "CALC")
 void nunit_standard(MavenExecutionResult result) throws IOException {
    String report = CommonUtils.readResourceFile("XrayDatacenterIT/nunit_standard/nunit.xml");

    wm.verify(
        postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/execution/nunit"))
            .withBasicAuth(new BasicCredentials("username", "password"))
            .withHeader("Content-Type", containing("multipart/form-data;"))
            .withQueryParam("projectKey", equalTo("CALC"))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("file")
                    .withHeader("Content-Type", containing("application/xml"))
                    .withBody(equalTo(report)))
    );
    assertThat(result).isSuccessful();
 }

 @MavenTest
 @MavenGoal("xray:import-results")
 @SystemProperty(value = "xray.cloud", content = "false")
 @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:18080")
 @SystemProperty(value = "xray.jiraUsername", content = "username")
 @SystemProperty(value = "xray.jiraPassword", content = "password")
 @SystemProperty(value = "xray.reportFormat", content = "nunit")
 @SystemProperty(value = "xray.reportFile", content = "nunit.xml")
 @SystemProperty(value = "xray.testExecInfoJson", content = "testExecInfo.json")
 void nunit_multipart(MavenExecutionResult result) throws IOException {
    String testExecInfo = CommonUtils.readResourceFile("XrayDatacenterIT/nunit_multipart/testExecInfo.json");
    String report = CommonUtils.readResourceFile("XrayDatacenterIT/nunit_multipart/nunit.xml");

    wm.verify(
        postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/execution/nunit/multipart"))
            .withBasicAuth(new BasicCredentials("username", "password"))
            .withHeader("Content-Type", containing("multipart/form-data;"))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("file")
                    .withHeader("Content-Type", containing("application/xml"))
                    .withBody(equalTo(report)))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("info")
                    .withHeader("Content-Type", containing("application/json"))
                    .withBody(equalToJson(testExecInfo))
                )

    );
    assertThat(result).isSuccessful();
 }

 @MavenTest
 @MavenGoal("xray:import-results")
 @SystemProperty(value = "xray.cloud", content = "false")
 @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:18080")
 @SystemProperty(value = "xray.jiraUsername", content = "username")
 @SystemProperty(value = "xray.jiraPassword", content = "password")
 @SystemProperty(value = "xray.reportFormat", content = "xunit")
 @SystemProperty(value = "xray.reportFile", content = "xunit.xml")
 @SystemProperty(value = "xray.projectKey", content = "CALC")
 void xunit_standard(MavenExecutionResult result) throws IOException {
    String report = CommonUtils.readResourceFile("XrayDatacenterIT/xunit_standard/xunit.xml");

    wm.verify(
        postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/execution/xunit"))
            .withBasicAuth(new BasicCredentials("username", "password"))
            .withHeader("Content-Type", containing("multipart/form-data;"))
            .withQueryParam("projectKey", equalTo("CALC"))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("file")
                    .withHeader("Content-Type", containing("application/xml"))
                    .withBody(equalTo(report)))
    );
    assertThat(result).isSuccessful();
 }

 @MavenTest
 @MavenGoal("xray:import-results")
 @SystemProperty(value = "xray.cloud", content = "false")
 @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:18080")
 @SystemProperty(value = "xray.jiraUsername", content = "username")
 @SystemProperty(value = "xray.jiraPassword", content = "password")
 @SystemProperty(value = "xray.reportFormat", content = "xunit")
 @SystemProperty(value = "xray.reportFile", content = "xunit.xml")
 @SystemProperty(value = "xray.testExecInfoJson", content = "testExecInfo.json")
 void xunit_multipart(MavenExecutionResult result) throws IOException {
    String testExecInfo = CommonUtils.readResourceFile("XrayDatacenterIT/xunit_multipart/testExecInfo.json");
    String report = CommonUtils.readResourceFile("XrayDatacenterIT/xunit_multipart/xunit.xml");

    wm.verify(
        postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/execution/xunit/multipart"))
            .withBasicAuth(new BasicCredentials("username", "password"))
            .withHeader("Content-Type", containing("multipart/form-data;"))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("file")
                    .withHeader("Content-Type", containing("application/xml"))
                    .withBody(equalTo(report)))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("info")
                    .withHeader("Content-Type", containing("application/json"))
                    .withBody(equalToJson(testExecInfo))
                )

    );
    assertThat(result).isSuccessful();
 }

 @MavenTest
 @MavenGoal("xray:import-results")
 @SystemProperty(value = "xray.cloud", content = "false")
 @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:18080")
 @SystemProperty(value = "xray.jiraUsername", content = "username")
 @SystemProperty(value = "xray.jiraPassword", content = "password")
 @SystemProperty(value = "xray.reportFormat", content = "robot")
 @SystemProperty(value = "xray.reportFile", content = "robot.xml")
 @SystemProperty(value = "xray.projectKey", content = "CALC")
 void robot_standard(MavenExecutionResult result) throws IOException {
    String report = CommonUtils.readResourceFile("XrayDatacenterIT/robot_standard/robot.xml");

    wm.verify(
        postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/execution/robot"))
            .withBasicAuth(new BasicCredentials("username", "password"))
            .withHeader("Content-Type", containing("multipart/form-data;"))
            .withQueryParam("projectKey", equalTo("CALC"))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("file")
                    .withHeader("Content-Type", containing("application/xml"))
                    .withBody(equalToXml(report)))
    );
    assertThat(result).isSuccessful();
 }

 @MavenTest
 @MavenGoal("xray:import-results")
 @SystemProperty(value = "xray.cloud", content = "false")
 @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:18080")
 @SystemProperty(value = "xray.jiraUsername", content = "username")
 @SystemProperty(value = "xray.jiraPassword", content = "password")
 @SystemProperty(value = "xray.reportFormat", content = "robot")
 @SystemProperty(value = "xray.reportFile", content = "robot.xml")
 @SystemProperty(value = "xray.testExecInfoJson", content = "testExecInfo.json")
 void robot_multipart(MavenExecutionResult result) throws IOException {
    String testExecInfo = CommonUtils.readResourceFile("XrayDatacenterIT/robot_multipart/testExecInfo.json");
    String report = CommonUtils.readResourceFile("XrayDatacenterIT/robot_multipart/robot.xml");

    wm.verify(
        postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/execution/robot/multipart"))
            .withBasicAuth(new BasicCredentials("username", "password"))
            .withHeader("Content-Type", containing("multipart/form-data;"))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("file")
                    .withHeader("Content-Type", containing("application/xml"))
                    .withBody(equalToXml(report)))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("info")
                    .withHeader("Content-Type", containing("application/json"))
                    .withBody(equalToJson(testExecInfo))
                )

    );
    assertThat(result).isSuccessful();
 }

 @MavenTest
 @MavenGoal("xray:import-results")
 @SystemProperty(value = "xray.cloud", content = "false")
 @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:18080")
 @SystemProperty(value = "xray.jiraUsername", content = "username")
 @SystemProperty(value = "xray.jiraPassword", content = "password")
 @SystemProperty(value = "xray.reportFormat", content = "cucumber")
 @SystemProperty(value = "xray.reportFile", content = "cucumber.json")
 void cucumber_standard(MavenExecutionResult result) throws IOException {
    String report = CommonUtils.readResourceFile("XrayDatacenterIT/cucumber_standard/cucumber.json");

    wm.verify(
        postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/execution/cucumber"))
            .withBasicAuth(new BasicCredentials("username", "password"))
            .withHeader("Content-Type", containing("application/json"))
            .withRequestBody(equalToJson(report))
    );
    assertThat(result).isSuccessful();
 }

 @MavenTest
 @MavenGoal("xray:import-results")
 @SystemProperty(value = "xray.cloud", content = "false")
 @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:18080")
 @SystemProperty(value = "xray.jiraUsername", content = "username")
 @SystemProperty(value = "xray.jiraPassword", content = "password")
 @SystemProperty(value = "xray.reportFormat", content = "cucumber")
 @SystemProperty(value = "xray.reportFile", content = "cucumber.json")
 @SystemProperty(value = "xray.testExecInfoJson", content = "testExecInfo.json")
 void cucumber_multipart(MavenExecutionResult result) throws IOException {
    String testExecInfo = CommonUtils.readResourceFile("XrayDatacenterIT/cucumber_multipart/testExecInfo.json");
    String report = CommonUtils.readResourceFile("XrayDatacenterIT/cucumber_multipart/cucumber.json");

    wm.verify(
        postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/execution/cucumber/multipart"))
            .withBasicAuth(new BasicCredentials("username", "password"))
            .withHeader("Content-Type", containing("multipart/form-data;"))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("result")
                    .withHeader("Content-Type", containing("application/json"))
                    .withBody(equalToJson(report)))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("info")
                    .withHeader("Content-Type", containing("application/json"))
                    .withBody(equalToJson(testExecInfo))
                )

    );
    assertThat(result).isSuccessful();
 }

 @MavenTest
 @MavenGoal("xray:import-results")
 @SystemProperty(value = "xray.cloud", content = "false")
 @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:18080")
 @SystemProperty(value = "xray.jiraUsername", content = "username")
 @SystemProperty(value = "xray.jiraPassword", content = "password")
 @SystemProperty(value = "xray.reportFormat", content = "behave")
 @SystemProperty(value = "xray.reportFile", content = "behave.json")
 void behave_standard(MavenExecutionResult result) throws IOException {
    String report = CommonUtils.readResourceFile("XrayDatacenterIT/behave_standard/behave.json");

    wm.verify(
        postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/execution/behave"))
            .withBasicAuth(new BasicCredentials("username", "password"))
            .withHeader("Content-Type", containing("application/json"))
            .withRequestBody(equalToJson(report))
    );
    assertThat(result).isSuccessful();
 }

 @MavenTest
 @MavenGoal("xray:import-results")
 @SystemProperty(value = "xray.cloud", content = "false")
 @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:18080")
 @SystemProperty(value = "xray.jiraUsername", content = "username")
 @SystemProperty(value = "xray.jiraPassword", content = "password")
 @SystemProperty(value = "xray.reportFormat", content = "behave")
 @SystemProperty(value = "xray.reportFile", content = "behave.json")
 @SystemProperty(value = "xray.testExecInfoJson", content = "testExecInfo.json")
 void behave_multipart(MavenExecutionResult result) throws IOException {
    String testExecInfo = CommonUtils.readResourceFile("XrayDatacenterIT/behave_multipart/testExecInfo.json");
    String report = CommonUtils.readResourceFile("XrayDatacenterIT/behave_multipart/behave.json");

    wm.verify(
        postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/execution/behave/multipart"))
            .withBasicAuth(new BasicCredentials("username", "password"))
            .withHeader("Content-Type", containing("multipart/form-data;"))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("result")
                    .withHeader("Content-Type", containing("application/json"))
                    .withBody(equalToJson(report)))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("info")
                    .withHeader("Content-Type", containing("application/json"))
                    .withBody(equalToJson(testExecInfo))
                )

    );
    assertThat(result).isSuccessful();
 }

}