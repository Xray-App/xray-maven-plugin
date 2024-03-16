package app.getxray.xray.it.export_features;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
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
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.extension.SystemProperty;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

import app.getxray.xray.it.CommonUtils;

@MavenJupiterExtension
public class XrayDatacenterIT {

    static final int PORT_NUMBER = 18080;

    static WireMockServer wm;
    @BeforeAll
    public static void setup () {
        wm = new WireMockServer(options().port(PORT_NUMBER));
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
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:"+PORT_NUMBER)
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
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:"+PORT_NUMBER)
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
