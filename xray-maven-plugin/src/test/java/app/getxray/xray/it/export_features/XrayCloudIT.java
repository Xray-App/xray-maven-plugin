package app.getxray.xray.it.export_features;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
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
import org.junit.jupiter.api.DisplayNameGeneration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenRepository;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.extension.SystemProperty;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

import app.getxray.xray.CloudCustomDisplayNameGenerator;
import app.getxray.xray.it.TestingUtils;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

@MavenJupiterExtension
@DisplayNameGeneration(CloudCustomDisplayNameGenerator.class)
@MavenRepository
public class XrayCloudIT {

    private static final String CLIENT_ID = "32A27E69C0AC4E539C14010000000000";
    private static final String CLIENT_SECRET = "d62f81eb9ed859e22e54356dd8a00e4a5f0d0c2b2b52340776f6c70000000000";
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0ZW5hbnQiOiI0MjZiYzA4Yy02N2VmLTNjMjYtYWU1YS03NjczYTB1ZjIwNjkiLCJ1c2VyS2V5IjoiYW5kcmUucm9kcmlndWVzIiwiaWF0IjixNTI1ODcxODkzLCJleHAiOjE1MjU5NTgyOTMsImF1ZCI6IhMyQTI3RTY5QjBBQzRFNTM5QzE0MDE2NDM3OTlFOEU3IiwiaXNzIjoiY29tLnhwYW5kaXQueHJheSIsInN1YiI6IjMyQTI3RTY5QjBBQzRFNTM5QzE0MDE2NDM3OTlFOEU3In0.8ah2IQ9rA_zotyh_6trFgfIvhn2awdFFrOHnN2F2H7m";
    static final int PORT_NUMBER = 18080;

    static WireMockServer wm;
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

    private static void setupStub() {
        System.out.println("setting up stubs...");

        byte[] zippedFeature = null;
        try {
            zippedFeature = TestingUtils.readRawResourceFile("export_features/XrayCloudIT/single_feature_by_issueKeys/dummy.zip");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        wm.stubFor(post(urlPathEqualTo("/api/v2/authenticate"))
        .withHost(equalTo("xray.cloud.getxray.app"))
        .willReturn(okJson(TOKEN))
        .atPriority(1)
        );

        // cucumber export
        wm.stubFor(get(urlPathEqualTo("/api/v2/export/cucumber"))
            .willReturn(aResponse()
            .withHeader("Content-Type", "application/octet-stream")
            .withBody(zippedFeature)
        ));

        System.out.println("setting up stubs for custom, region-based, Xray Cloud REST API endpoint...");
        wm.stubFor(post(urlPathEqualTo("/api/v2/authenticate"))
            .withHost(equalTo("eu.xray.cloud.getxray.app"))
            .willReturn(okJson(TOKEN))
            .atPriority(1)
        );
        wm.stubFor(get(urlPathEqualTo("/api/v2/export/cucumber"))
            .withHost(equalTo("eu.xray.cloud.getxray.app"))
            .willReturn(aResponse()
            .withHeader("Content-Type", "application/octet-stream")
            .withBody(zippedFeature)
        ));
    }

    @MavenTest
    @MavenGoal("xray:export-features")
    @SystemProperty(value = "xray.cloud", content = "true")
    @SystemProperty(value = "xray.clientId", content = CLIENT_ID)
    @SystemProperty(value = "xray.clientSecret", content = CLIENT_SECRET)
    @SystemProperty(value = "xray.cloudApiBaseUrl", content = "https://eu.xray.cloud.getxray.app/api/v2")
    @SystemProperty(value = "xray.useInternalTestProxy", content = "true")
    @SystemProperty(value = "xray.issueKeys", content = "CALC-1")
    @SystemProperty(value = "xray.outputDir", content = "./features")
    @Requirement("XMP-126")
    void single_feature_by_issueKeys_eu_region(MavenExecutionResult result) throws IOException {
       String feature = TestingUtils.readResourceFileForExportFeatures("XrayCloudIT/single_feature_by_issueKeys_eu_region/dummy.feature");

       wm.verify(
        postRequestedFor(null)
            .withHost(equalTo("eu.xray.cloud.getxray.app"))
            .withUrl("/api/v2/authenticate")
            .withHeader("Content-Type", containing("application/json"))
        );

        wm.verify(
            getRequestedFor(urlPathEqualTo("/api/v2/export/cucumber"))
                .withHost(equalTo("eu.xray.cloud.getxray.app"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withQueryParam("keys", equalTo("CALC-1"))
        );
       assertThat(result).isSuccessful();
       assertThat(TestingUtils.readFile(result.getMavenProjectResult().getTargetProjectDirectory()+"/features/dummy.feature")).isEqualTo(feature);
    }

    @MavenTest
    @MavenGoal("xray:export-features")
    @SystemProperty(value = "xray.cloud", content = "true")
    @SystemProperty(value = "xray.clientId", content = CLIENT_ID)
    @SystemProperty(value = "xray.clientSecret", content = CLIENT_SECRET)
    @SystemProperty(value = "xray.useInternalTestProxy", content = "true")
    @SystemProperty(value = "xray.issueKeys", content = "CALC-1")
    @SystemProperty(value = "xray.outputDir", content = "./features")
    @Requirement("XMP-126")
    void single_feature_by_issueKeys(MavenExecutionResult result) throws IOException {
       String feature = TestingUtils.readResourceFileForExportFeatures("XrayCloudIT/single_feature_by_issueKeys/dummy.feature");

        wm.verify(
            getRequestedFor(urlPathEqualTo("/api/v2/export/cucumber"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withQueryParam("keys", equalTo("CALC-1"))
        );
       assertThat(result).isSuccessful();
       assertThat(TestingUtils.readFile(result.getMavenProjectResult().getTargetProjectDirectory()+"/features/dummy.feature")).isEqualTo(feature);
    }

    @MavenTest
    @MavenGoal("xray:export-features")
    @SystemProperty(value = "xray.cloud", content = "true")
    @SystemProperty(value = "xray.clientId", content = CLIENT_ID)
    @SystemProperty(value = "xray.clientSecret", content = CLIENT_SECRET)
    @SystemProperty(value = "xray.useInternalTestProxy", content = "true")
    @SystemProperty(value = "xray.filterId", content = "12345")
    @SystemProperty(value = "xray.outputDir", content = "./features")
    @Requirement("XMP-126")
    void single_feature_by_filterId(MavenExecutionResult result) throws IOException {
       String feature = TestingUtils.readResourceFileForExportFeatures("XrayCloudIT/single_feature_by_filterId/dummy.feature");

        wm.verify(
            getRequestedFor(urlPathEqualTo("/api/v2/export/cucumber"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withQueryParam("filter", equalTo("12345"))
        );
       assertThat(result).isSuccessful();
       assertThat(TestingUtils.readFile(result.getMavenProjectResult().getTargetProjectDirectory()+"/features/dummy.feature")).isEqualTo(feature);
    }

}
