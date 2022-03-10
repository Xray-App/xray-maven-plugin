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
public class TimeoutHandlingIT {

    static WireMockServer wm;

    @BeforeAll
    public static void setup () {
        wm = new WireMockServer(options().port(18080));
        wm.start();
        setupStub();
    }

    @AfterAll
    public static void teardown () {
        wm.stop();
    }

    public static void setupStub() {
        System.out.println("setting up stubs...");
    
        // timeout=1s
        wm.stubFor(
            post(urlPathEqualTo("/rest/raven/2.0/import/execution/junit"))
            .withQueryParam("projectKey", equalTo("DELAY1"))
        .willReturn(
            okJson("{ \"testExecIssue\": { \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" } }")
            .withFixedDelay(1000)
        ));

        // timeout=3s
        wm.stubFor(
            post(urlPathEqualTo("/rest/raven/2.0/import/execution/junit"))
            .withQueryParam("projectKey", equalTo("DELAY3"))
        .willReturn(
            okJson("{ \"testExecIssue\": { \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" } }")
            .withFixedDelay(3000)
        ));

        // timeout=49s
        wm.stubFor(
            post(urlPathEqualTo("/rest/raven/2.0/import/execution/junit"))
            .withQueryParam("projectKey", equalTo("DELAY49"))
        .willReturn(
            okJson("{ \"testExecIssue\": { \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" } }")
            .withFixedDelay(49000)
        ));

        // timeout=51s
        wm.stubFor(
            post(urlPathEqualTo("/rest/raven/2.0/import/execution/junit"))
            .withQueryParam("projectKey", equalTo("DELAY51"))
        .willReturn(
            okJson("{ \"testExecIssue\": { \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" } }")
            .withFixedDelay(51000)
        ));
    }

    @MavenTest
    @MavenGoal("xray:import-results")
    @SystemProperty(value = "xray.cloud", content = "false")
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:18080")
    @SystemProperty(value = "xray.jiraUsername", content = "username")
    @SystemProperty(value = "xray.jiraPassword", content = "password")
    @SystemProperty(value = "xray.reportFormat", content = "junit")
    @SystemProperty(value = "xray.reportFile", content = "junit.xml")
    @SystemProperty(value = "xray.projectKey", content = "DELAY1")
    @SystemProperty(value = "xray.timeout", content = "2")
    void below_configured_timeout(MavenExecutionResult result) throws IOException {
        String report = CommonUtils.readResourceFile("TimeoutHandlingIT/below_configured_timeout/junit.xml");

        wm.verify(
           postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/execution/junit"))
               .withBasicAuth(new BasicCredentials("username", "password"))
               .withHeader("Content-Type", containing("multipart/form-data;"))
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
    @SystemProperty(value = "xray.projectKey", content = "DELAY3")
    @SystemProperty(value = "xray.timeout", content = "2")
    void exceed_configured_timeout(MavenExecutionResult result) throws IOException {
        assertThat(result.getMavenLog().getStdout().toString()).contains("timeout");
        assertThat(result).isFailure();
    }

    @MavenTest
    @MavenGoal("xray:import-results")
    @SystemProperty(value = "xray.cloud", content = "false")
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:18080")
    @SystemProperty(value = "xray.jiraUsername", content = "username")
    @SystemProperty(value = "xray.jiraPassword", content = "password")
    @SystemProperty(value = "xray.reportFormat", content = "junit")
    @SystemProperty(value = "xray.reportFile", content = "junit.xml")
    @SystemProperty(value = "xray.projectKey", content = "DELAY49")
    void below_default_timeout(MavenExecutionResult result) throws IOException {
        String report = CommonUtils.readResourceFile("TimeoutHandlingIT/below_default_timeout/junit.xml");

        wm.verify(
            postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/execution/junit"))
                .withBasicAuth(new BasicCredentials("username", "password"))
                .withHeader("Content-Type", containing("multipart/form-data;"))
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
    @SystemProperty(value = "xray.projectKey", content = "DELAY51")
    void exceed_default_timeout(MavenExecutionResult result) throws IOException {
        assertThat(result.getMavenLog().getStdout().toString()).contains("timeout");
        assertThat(result).isFailure();
    }

}
