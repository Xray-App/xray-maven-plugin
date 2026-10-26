package app.getxray.xray.it.import_results;

import static com.github.tomakehurst.wiremock.client.WireMock.aMultipart;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToXml;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenRepository;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.extension.SystemProperty;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

import app.getxray.xray.it.TestingUtils;

@MavenJupiterExtension
@MavenRepository
public class ErrorHandlingIT {

    static WireMockServer wm;
    static final int PORT_NUMBER = 18080;

    @BeforeAll
    static void setup () {
        wm = new WireMockServer(options().port(PORT_NUMBER));
        wm.start();
        setupStub();
    }

    @AfterAll
    static void teardown () {
        wm.stop();
    }

    public static void setupStub() {
        System.out.println("setting up stubs...");

        wm.stubFor(post(urlPathEqualTo("/rest/raven/2.0/import/execution/junit"))
            .willReturn(okJson("{ \"testExecIssue\": { \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" } }")));

        wm.stubFor(post(urlPathEqualTo("/rest/raven/2.0/import/execution/junit"))
            .withQueryParam("projectKey", equalTo("ERROR"))
            .willReturn(serverError())
            .atPriority(1));

        System.out.println("setting up stubs - done.");
    }

    @MavenTest
    @MavenGoal("xray:import-results")
    @SystemProperty(value = "xray.cloud", content = "false")
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:"+PORT_NUMBER)
    @SystemProperty(value = "xray.jiraUsername", content = "username")
    @SystemProperty(value = "xray.jiraPassword", content = "password")
    @SystemProperty(value = "xray.reportFormat", content = "junit")
    @SystemProperty(value = "xray.reportFile", content = "junit.xml")
    @SystemProperty(value = "xray.projectKey", content = "ERROR")
    void server_error(MavenExecutionResult result) {
        assertThat(result).isFailure();
        assertThat(result).out().error().anyMatch(line -> line.contains("code=500"));
    }

    // the project's pom.xml sets abortOnError=false
    @MavenTest
    @MavenGoal("xray:import-results")
    @SystemProperty(value = "xray.cloud", content = "false")
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:"+PORT_NUMBER)
    @SystemProperty(value = "xray.jiraUsername", content = "username")
    @SystemProperty(value = "xray.jiraPassword", content = "password")
    @SystemProperty(value = "xray.reportFormat", content = "junit")
    @SystemProperty(value = "xray.reportFile", content = "junit.xml")
    @SystemProperty(value = "xray.projectKey", content = "ERROR")
    void server_error_without_abort(MavenExecutionResult result) {
        assertThat(result).isSuccessful();
        assertThat(result).out().error().anyMatch(line -> line.contains("code=500"));
    }

    @MavenTest
    @MavenGoal("xray:import-results")
    @SystemProperty(value = "xray.cloud", content = "false")
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:"+PORT_NUMBER)
    @SystemProperty(value = "xray.jiraUsername", content = "username")
    @SystemProperty(value = "xray.jiraPassword", content = "password")
    @SystemProperty(value = "xray.reportFormat", content = "junit")
    @SystemProperty(value = "xray.reportFile", content = "reports")
    @SystemProperty(value = "xray.projectKey", content = "DIR")
    void report_directory(MavenExecutionResult result) throws IOException {
        // only the .xml files in the directory are imported (notes.txt is ignored)
        wm.verify(exactly(2), postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/execution/junit"))
            .withQueryParam("projectKey", equalTo("DIR")));
        for (String file : new String[] { "a.xml", "b.xml" }) {
            String report = TestingUtils.readResourceFileForImportResults("ErrorHandlingIT/report_directory/reports/" + file);
            wm.verify(exactly(1), postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/execution/junit"))
                .withQueryParam("projectKey", equalTo("DIR"))
                .withAnyRequestBodyPart(
                    aMultipart()
                        .withName("file")
                        .withBody(equalToXml(report))));
        }
        assertThat(result).isSuccessful();
    }

    @MavenTest
    @MavenGoal("xray:import-results")
    @SystemProperty(value = "xray.cloud", content = "false")
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:"+PORT_NUMBER)
    @SystemProperty(value = "xray.jiraUsername", content = "username")
    @SystemProperty(value = "xray.jiraPassword", content = "password")
    @SystemProperty(value = "xray.reportFormat", content = "junit")
    @SystemProperty(value = "xray.reportFile", content = "reports/*.xml")
    @SystemProperty(value = "xray.projectKey", content = "GLOB")
    void report_glob(MavenExecutionResult result) throws IOException {
        wm.verify(exactly(2), postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/execution/junit"))
            .withQueryParam("projectKey", equalTo("GLOB")));
        for (String file : new String[] { "a.xml", "b.xml" }) {
            String report = TestingUtils.readResourceFileForImportResults("ErrorHandlingIT/report_glob/reports/" + file);
            wm.verify(exactly(1), postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/execution/junit"))
                .withQueryParam("projectKey", equalTo("GLOB"))
                .withAnyRequestBodyPart(
                    aMultipart()
                        .withName("file")
                        .withBody(equalToXml(report))));
        }
        assertThat(result).isSuccessful();
    }

    @MavenTest
    @MavenGoal("xray:import-results")
    @SystemProperty(value = "xray.cloud", content = "false")
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:"+PORT_NUMBER)
    @SystemProperty(value = "xray.jiraUsername", content = "username")
    @SystemProperty(value = "xray.jiraPassword", content = "password")
    @SystemProperty(value = "xray.reportFormat", content = "junit")
    @SystemProperty(value = "xray.reportFile", content = "does_not_exist.xml")
    @SystemProperty(value = "xray.projectKey", content = "MISSING")
    void missing_report_file(MavenExecutionResult result) {
        wm.verify(exactly(0), postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/execution/junit"))
            .withQueryParam("projectKey", equalTo("MISSING")));
        assertThat(result).isFailure();
        assertThat(result).out().error().anyMatch(line -> line.contains("no test report file(s) found: does_not_exist.xml"));
    }

    @MavenTest
    @MavenGoal("xray:import-results")
    @SystemProperty(value = "xray.cloud", content = "false")
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:"+PORT_NUMBER)
    @SystemProperty(value = "xray.jiraUsername", content = "username")
    @SystemProperty(value = "xray.jiraPassword", content = "password")
    @SystemProperty(value = "xray.reportFormat", content = "junit")
    @SystemProperty(value = "xray.reportFile", content = "junit.xml")
    @SystemProperty(value = "xray.testInfoJson", content = "does_not_exist.json")
    void missing_test_info_json(MavenExecutionResult result) {
        assertThat(result).isFailure();
        assertThat(result).out().error().anyMatch(line -> line.contains("file doesnt exist: does_not_exist.json"));
    }

    // the project's pom.xml sets abortOnError=false
    @MavenTest
    @MavenGoal("xray:import-results")
    @SystemProperty(value = "xray.cloud", content = "false")
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:"+PORT_NUMBER)
    @SystemProperty(value = "xray.jiraUsername", content = "username")
    @SystemProperty(value = "xray.jiraPassword", content = "password")
    @SystemProperty(value = "xray.reportFormat", content = "junit")
    @SystemProperty(value = "xray.reportFile", content = "does_not_exist.xml")
    @SystemProperty(value = "xray.projectKey", content = "MISSING")
    void missing_report_file_without_abort(MavenExecutionResult result) {
        wm.verify(exactly(0), postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/execution/junit"))
            .withQueryParam("projectKey", equalTo("MISSING")));
        assertThat(result).isSuccessful();
        assertThat(result).out().error().anyMatch(line -> line.contains("no test report file(s) found: does_not_exist.xml"));
    }

    // the project's pom.xml sets abortOnError=false
    @MavenTest
    @MavenGoal("xray:import-results")
    @SystemProperty(value = "xray.cloud", content = "false")
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:"+PORT_NUMBER)
    @SystemProperty(value = "xray.jiraUsername", content = "username")
    @SystemProperty(value = "xray.jiraPassword", content = "password")
    @SystemProperty(value = "xray.reportFormat", content = "junit")
    @SystemProperty(value = "xray.reportFile", content = "junit.xml")
    @SystemProperty(value = "xray.projectKey", content = "NOINFO")
    @SystemProperty(value = "xray.testInfoJson", content = "does_not_exist.json")
    void missing_test_info_json_without_abort(MavenExecutionResult result) {
        // nothing gets imported when the test info can't be read
        wm.verify(exactly(0), postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/execution/junit/multipart")));
        wm.verify(exactly(0), postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/execution/junit"))
            .withQueryParam("projectKey", equalTo("NOINFO")));
        assertThat(result).isSuccessful();
        assertThat(result).out().error().anyMatch(line -> line.contains("file doesnt exist: does_not_exist.json"));
    }

    @MavenTest
    @MavenGoal("xray:import-results")
    @SystemProperty(value = "xray.cloud", content = "false")
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:"+PORT_NUMBER)
    @SystemProperty(value = "xray.jiraUsername", content = "username")
    @SystemProperty(value = "xray.jiraPassword", content = "password")
    @SystemProperty(value = "xray.reportFormat", content = "junit")
    @SystemProperty(value = "xray.reportFile", content = "junit.xml")
    @SystemProperty(value = "xray.testExecInfoJson", content = "testExecInfo.json")
    void invalid_test_exec_info_json(MavenExecutionResult result) {
        assertThat(result).isFailure();
        assertThat(result).out().error().anyMatch(line -> line.contains("JSONObject"));
    }

}
