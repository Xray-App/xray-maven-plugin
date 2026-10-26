package app.getxray.xray.it.export_features;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenRepository;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.extension.SystemProperty;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

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

        wm.stubFor(get(urlPathEqualTo("/rest/raven/2.0/export/test"))
            .withQueryParam("keys", equalTo("ERROR-1"))
            .willReturn(serverError()));

        System.out.println("setting up stubs - done.");
    }

    @MavenTest
    @MavenGoal("xray:export-features")
    @SystemProperty(value = "xray.cloud", content = "false")
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:"+PORT_NUMBER)
    @SystemProperty(value = "xray.jiraUsername", content = "username")
    @SystemProperty(value = "xray.jiraPassword", content = "password")
    @SystemProperty(value = "xray.issueKeys", content = "ERROR-1")
    @SystemProperty(value = "xray.outputDir", content = "./features")
    void server_error(MavenExecutionResult result) {
        assertThat(result).isFailure();
        assertThat(result).out().error().anyMatch(line -> line.contains("code=500"));
    }

    // the project's pom.xml sets abortOnError=false
    @MavenTest
    @MavenGoal("xray:export-features")
    @SystemProperty(value = "xray.cloud", content = "false")
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:"+PORT_NUMBER)
    @SystemProperty(value = "xray.jiraUsername", content = "username")
    @SystemProperty(value = "xray.jiraPassword", content = "password")
    @SystemProperty(value = "xray.issueKeys", content = "ERROR-1")
    @SystemProperty(value = "xray.outputDir", content = "./features")
    void server_error_without_abort(MavenExecutionResult result) {
        assertThat(result).isSuccessful();
        assertThat(result).out().error().anyMatch(line -> line.contains("code=500"));
    }

}
