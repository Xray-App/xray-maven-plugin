package app.getxray.xray.it.export_features;

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

import app.getxray.xray.it.CommonUtils;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import com.github.tomakehurst.wiremock.client.BasicCredentials;

@MavenJupiterExtension
public class XrayDatacenterIT {

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

        byte[] zippedFeature = null;
        try {
            zippedFeature = CommonUtils.readRawResourceFile("export_features/XrayDatacenterIT/single_feature_by_issueKeys/dummy.zip");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // cucumber export
        wm.stubFor(get(urlPathEqualTo("/rest/raven/2.0/export/test"))
            .willReturn(aResponse()
            .withHeader("Content-Type", "application/octet-stream")
            .withBody(zippedFeature)

        ));

        System.out.println("setting up stubs - done.");
    }

    @MavenTest
    @MavenGoal("xray:export-features")
    @SystemProperty(value = "xray.cloud", content = "false")
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:18080")
    @SystemProperty(value = "xray.jiraUsername", content = "username")
    @SystemProperty(value = "xray.jiraPassword", content = "password")
    @SystemProperty(value = "xray.issueKeys", content = "CALC-1")
    @SystemProperty(value = "xray.outputDir", content = "./features")
    void single_feature_by_issueKeys(MavenExecutionResult result) throws IOException {
       String feature = CommonUtils.readResourceFileForExportFeatures("XrayDatacenterIT/single_feature_by_issueKeys/dummy.feature");

        wm.verify(
            getRequestedFor(urlPathEqualTo("/rest/raven/2.0/export/test"))
                .withBasicAuth(new BasicCredentials("username", "password"))
                .withQueryParam("keys", equalTo("CALC-1"))
                .withQueryParam("fz", equalTo("true"))
        );
       assertThat(result).isSuccessful();
       assertThat(CommonUtils.readFile(result.getMavenProjectResult().getTargetProjectDirectory()+"/features/dummy.feature")).isEqualTo(feature);
    }

    @MavenTest
    @MavenGoal("xray:export-features")
    @SystemProperty(value = "xray.cloud", content = "false")
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:18080")
    @SystemProperty(value = "xray.jiraUsername", content = "username")
    @SystemProperty(value = "xray.jiraPassword", content = "password")
    @SystemProperty(value = "xray.filterId", content = "12345")
    @SystemProperty(value = "xray.outputDir", content = "./features")
    void single_feature_by_filterId(MavenExecutionResult result) throws IOException {
       String feature = CommonUtils.readResourceFileForExportFeatures("XrayDatacenterIT/single_feature_by_filterId/dummy.feature");

        wm.verify(
            getRequestedFor(urlPathEqualTo("/rest/raven/2.0/export/test"))
                .withBasicAuth(new BasicCredentials("username", "password"))
                .withQueryParam("filter", equalTo("12345"))
                .withQueryParam("fz", equalTo("true"))
        );
       assertThat(result).isSuccessful();
       assertThat(CommonUtils.readFile(result.getMavenProjectResult().getTargetProjectDirectory()+"/features/dummy.feature")).isEqualTo(feature);
    }
}
