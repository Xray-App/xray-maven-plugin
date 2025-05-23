package app.getxray.xray.it.import_results;

import static com.github.tomakehurst.wiremock.client.WireMock.aMultipart;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToXml;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.BasicCredentials;
import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenRepository;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.extension.SystemProperty;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

import app.getxray.xray.it.TestingUtils;

@MavenJupiterExtension
@MavenRepository
public class TimeoutHandlingIT {

    private static final String CLIENT_ID = "32A27E69C0AC4E539C14010000000000";
    private static final String CLIENT_SECRET = "d62f81eb9ed859e22e54356dd8a00e4a5f0d0c2b2b52340776f6c70000000000";
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0ZW5hbnQiOiI0MjZiYzA4Yy02N2VmLTNjMjYtYWU1YS03NjczYTB1ZjIwNjkiLCJ1c2VyS2V5IjoiYW5kcmUucm9kcmlndWVzIiwiaWF0IjixNTI1ODcxODkzLCJleHAiOjE1MjU5NTgyOTMsImF1ZCI6IhMyQTI3RTY5QjBBQzRFNTM5QzE0MDE2NDM3OTlFOEU3IiwiaXNzIjoiY29tLnhwYW5kaXQueHJheSIsInN1YiI6IjMyQTI3RTY5QjBBQzRFNTM5QzE0MDE2NDM3OTlFOEU3In0.8ah2IQ9rA_zotyh_6trFgfIvhn2awdFFrOHnN2F2H7m";

    static WireMockServer wm;
    static final int PORT_NUMBER = 18080;

    @BeforeAll
    static void setup () {
        wm = new WireMockServer(
            options()
            .port(PORT_NUMBER)
            .enableBrowserProxying(true)
        );
        wm.start();
        setupStub();
    }

    @AfterAll
    static void teardown () {
        wm.stop();
    }

    public static void setupStub() {
        System.out.println("setting up stubs...");
    
        /* stubs for Xray server/DC ... */

        // delay=1s
        wm.stubFor(
            post(urlPathEqualTo("/rest/raven/2.0/import/execution/junit"))
            .withQueryParam("projectKey", equalTo("DELAY1"))
        .willReturn(
            okJson("{ \"testExecIssue\": { \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" } }")
            .withFixedDelay(1000)
        ));

        // delay=3s
        wm.stubFor(
            post(urlPathEqualTo("/rest/raven/2.0/import/execution/junit"))
            .withQueryParam("projectKey", equalTo("DELAY3"))
        .willReturn(
            okJson("{ \"testExecIssue\": { \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" } }")
            .withFixedDelay(3000)
        ));

        // delay=49s
        wm.stubFor(
            post(urlPathEqualTo("/rest/raven/2.0/import/execution/junit"))
            .withQueryParam("projectKey", equalTo("DELAY49"))
        .willReturn(
            okJson("{ \"testExecIssue\": { \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" } }")
            .withFixedDelay(49000)
        ));

        // delay=51s
        wm.stubFor(
            post(urlPathEqualTo("/rest/raven/2.0/import/execution/junit"))
            .withQueryParam("projectKey", equalTo("DELAY51"))
        .willReturn(
            okJson("{ \"testExecIssue\": { \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" } }")
            .withFixedDelay(51000)
        ));


        /* stubs for Xray cloud ... */

        wm.stubFor(post(urlPathEqualTo("/api/v2/authenticate"))
            .withHost(equalTo("xray.cloud.getxray.app"))
            .willReturn(okJson(TOKEN))
            .atPriority(1)
        );

        // delay=1s
        wm.stubFor(post(urlPathEqualTo("/api/v2/import/execution/junit"))
            .withHost(equalTo("xray.cloud.getxray.app"))
            .withQueryParam("projectKey", equalTo("DELAY1"))
        .willReturn(
            okJson("{ \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" }")
            .withFixedDelay(1000)
        ));

        // delay=3s
        wm.stubFor(post(urlPathEqualTo("/api/v2/import/execution/junit"))
            .withHost(equalTo("xray.cloud.getxray.app"))
            .withQueryParam("projectKey", equalTo("DELAY3"))
        .willReturn(
            okJson("{ \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" }")
            .withFixedDelay(3000)
        ));

        // delay=49s
        wm.stubFor(post(urlPathEqualTo("/api/v2/import/execution/junit"))
            .withHost(equalTo("xray.cloud.getxray.app"))
            .withQueryParam("projectKey", equalTo("DELAY49"))
        .willReturn(
            okJson("{ \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" }")
            .withFixedDelay(49000)
        ));

        // delay=51s
        wm.stubFor(post(urlPathEqualTo("/api/v2/import/execution/junit"))
            .withHost(equalTo("xray.cloud.getxray.app"))
            .withQueryParam("projectKey", equalTo("DELAY51"))
        .willReturn(
            okJson("{ \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" }")
            .withFixedDelay(51000)
        ));
    }

    /* Xray server/DC tests ... */

    @MavenTest
    @MavenGoal("xray:import-results")
    @SystemProperty(value = "xray.cloud", content = "false")
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:"+PORT_NUMBER)
    @SystemProperty(value = "xray.jiraUsername", content = "username")
    @SystemProperty(value = "xray.jiraPassword", content = "password")
    @SystemProperty(value = "xray.reportFormat", content = "junit")
    @SystemProperty(value = "xray.reportFile", content = "junit.xml")
    @SystemProperty(value = "xray.projectKey", content = "DELAY1")
    @SystemProperty(value = "xray.timeout", content = "2")
    void below_configured_timeout(MavenExecutionResult result) throws IOException {
        String report = TestingUtils.readResourceFileForImportResults("TimeoutHandlingIT/below_configured_timeout/junit.xml");

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
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:"+PORT_NUMBER)
    @SystemProperty(value = "xray.jiraUsername", content = "username")
    @SystemProperty(value = "xray.jiraPassword", content = "password")
    @SystemProperty(value = "xray.reportFormat", content = "junit")
    @SystemProperty(value = "xray.reportFile", content = "junit.xml")
    @SystemProperty(value = "xray.projectKey", content = "DELAY3")
    @SystemProperty(value = "xray.timeout", content = "2")
    void exceed_configured_timeout(MavenExecutionResult result) {
        assertThat(result.getMavenLog().getStdout().toString()).contains("timeout");
        assertThat(result).isFailure();
    }

    @MavenTest
    @MavenGoal("xray:import-results")
    @SystemProperty(value = "xray.cloud", content = "false")
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:"+PORT_NUMBER)
    @SystemProperty(value = "xray.jiraUsername", content = "username")
    @SystemProperty(value = "xray.jiraPassword", content = "password")
    @SystemProperty(value = "xray.reportFormat", content = "junit")
    @SystemProperty(value = "xray.reportFile", content = "junit.xml")
    @SystemProperty(value = "xray.projectKey", content = "DELAY49")
    void below_default_timeout(MavenExecutionResult result) throws IOException {
        String report = TestingUtils.readResourceFileForImportResults("TimeoutHandlingIT/below_default_timeout/junit.xml");

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
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:"+PORT_NUMBER)
    @SystemProperty(value = "xray.jiraUsername", content = "username")
    @SystemProperty(value = "xray.jiraPassword", content = "password")
    @SystemProperty(value = "xray.reportFormat", content = "junit")
    @SystemProperty(value = "xray.reportFile", content = "junit.xml")
    @SystemProperty(value = "xray.projectKey", content = "DELAY51")
    void exceed_default_timeout(MavenExecutionResult result) {
        assertThat(result.getMavenLog().getStdout().toString()).contains("timeout");
        assertThat(result).isFailure();
    }


    /* Xray cloud tests ... */

    @MavenTest
    @MavenGoal("xray:import-results")
    @SystemProperty(value = "xray.cloud", content = "true")
    @SystemProperty(value = "xray.clientId", content = CLIENT_ID)
    @SystemProperty(value = "xray.clientSecret", content = CLIENT_SECRET)
    @SystemProperty(value = "xray.reportFormat", content = "junit")
    @SystemProperty(value = "xray.reportFile", content = "junit.xml")
    @SystemProperty(value = "xray.projectKey", content = "DELAY1")
    @SystemProperty(value = "xray.timeout", content = "2")
    @SystemProperty(value = "xray.useInternalTestProxy", content = "true")
    void below_configured_timeout_cloud(MavenExecutionResult result) throws IOException {
        String report = TestingUtils.readResourceFileForImportResults("TimeoutHandlingIT/below_configured_timeout_cloud/junit.xml");

        wm.verify(
            postRequestedFor(urlPathEqualTo("/api/v2/import/execution/junit"))
                .withHeader("Content-Type", containing("application/xml"))
                .withRequestBody(equalToXml(report))
        );
        assertThat(result).isSuccessful();
    }

    @MavenTest
    @MavenGoal("xray:import-results")
    @SystemProperty(value = "xray.cloud", content = "true")
    @SystemProperty(value = "xray.clientId", content = CLIENT_ID)
    @SystemProperty(value = "xray.clientSecret", content = CLIENT_SECRET)
    @SystemProperty(value = "xray.reportFormat", content = "junit")
    @SystemProperty(value = "xray.reportFile", content = "junit.xml")
    @SystemProperty(value = "xray.projectKey", content = "DELAY3")
    @SystemProperty(value = "xray.timeout", content = "2")
    @SystemProperty(value = "xray.useInternalTestProxy", content = "true")
    void exceed_configured_timeout_cloud(MavenExecutionResult result) {
        assertThat(result.getMavenLog().getStdout().toString()).contains("timeout");
        assertThat(result).isFailure();
    }

    // The following two tests should use the 50s threshold, however the wiremock proxy
    // mechanism used by the cloud tests, has a builtin timeout of 30s which AFAIK we
    // can't configure. Therefore, the tests were slightly adapted and should be changed
    // as soon as WM supports it, to make these tusts more accurate

    @MavenTest
    @MavenGoal("xray:import-results")
    @SystemProperty(value = "xray.cloud", content = "true")
    @SystemProperty(value = "xray.clientId", content = CLIENT_ID)
    @SystemProperty(value = "xray.clientSecret", content = CLIENT_SECRET)
    @SystemProperty(value = "xray.reportFormat", content = "junit")
    @SystemProperty(value = "xray.reportFile", content = "junit.xml")
    @SystemProperty(value = "xray.projectKey", content = "DELAY3") // should be DELAY49
    @SystemProperty(value = "xray.useInternalTestProxy", content = "true")
    void below_default_timeout_cloud(MavenExecutionResult result) throws IOException {
        String report = TestingUtils.readResourceFileForImportResults("TimeoutHandlingIT/below_default_timeout_cloud/junit.xml");

        wm.verify(
            postRequestedFor(urlPathEqualTo("/api/v2/import/execution/junit"))
                .withHeader("Content-Type", containing("application/xml"))
                .withRequestBody(equalToXml(report))
        );
        assertThat(result).isSuccessful();
    }

    // the following request will timeout after 30s, due to WM builtin proxy though
    @MavenTest
    @MavenGoal("xray:import-results")
    @SystemProperty(value = "xray.cloud", content = "true")
    @SystemProperty(value = "xray.clientId", content = CLIENT_ID)
    @SystemProperty(value = "xray.clientSecret", content = CLIENT_SECRET)
    @SystemProperty(value = "xray.reportFormat", content = "junit")
    @SystemProperty(value = "xray.reportFile", content = "junit.xml")
    @SystemProperty(value = "xray.projectKey", content = "DELAY51")
    @SystemProperty(value = "xray.useInternalTestProxy", content = "true")
    void exceed_default_timeout_cloud(MavenExecutionResult result) {
        assertThat(result.getMavenLog().getStdout().toString()).contains("timeout");
        assertThat(result).isFailure();
    }

}
