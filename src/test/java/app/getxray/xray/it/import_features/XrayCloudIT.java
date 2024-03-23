package app.getxray.xray.it.import_features;

import static com.github.tomakehurst.wiremock.client.WireMock.aMultipart;
import static com.github.tomakehurst.wiremock.client.WireMock.binaryEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.extension.SystemProperty;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

import app.getxray.xray.it.TestingUtils;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

@MavenJupiterExtension
public class XrayCloudIT {

    private static final String CLIENT_ID = "32A27E69C0AC4E539C14010000000000";
    private static final String CLIENT_SECRET = "d62f81eb9ed859e22e54356dd8a00e4a5f0d0c2b2b52340776f6c70000000000";
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0ZW5hbnQiOiI0MjZiYzA4Yy02N2VmLTNjMjYtYWU1YS03NjczYTB1ZjIwNjkiLCJ1c2VyS2V5IjoiYW5kcmUucm9kcmlndWVzIiwiaWF0IjixNTI1ODcxODkzLCJleHAiOjE1MjU5NTgyOTMsImF1ZCI6IhMyQTI3RTY5QjBBQzRFNTM5QzE0MDE2NDM3OTlFOEU3IiwiaXNzIjoiY29tLnhwYW5kaXQueHJheSIsInN1YiI6IjMyQTI3RTY5QjBBQzRFNTM5QzE0MDE2NDM3OTlFOEU3In0.8ah2IQ9rA_zotyh_6trFgfIvhn2awdFFrOHnN2F2H7m";
    static final int PORT_NUMBER = 18080;

    static WireMockServer wm;
    @BeforeAll
    public static void setup () {
        wm = new WireMockServer(
            options()
            .port(PORT_NUMBER)
            .enableBrowserProxying(true)
        );
        wm.start();
        setupStub();
    }
    @AfterAll
    public static void teardown () {
        wm.stop();
    }

    private static void setupStub() {
        System.out.println("setting up stubs...");

        wm.stubFor(post(urlPathEqualTo("/api/v2/authenticate"))
        .withHost(equalTo("xray.cloud.getxray.app"))
        .willReturn(okJson(TOKEN))
        .atPriority(1)
        );

        // cucumber
        wm.stubFor(post(urlPathEqualTo("/api/v2/import/feature"))
            .withHost(equalTo("xray.cloud.getxray.app"))
            .willReturn(okJson("{ \"updatedOrCreatedTests\": [ { \"id\": \"32495\", \"key\": \"DEMO-15119\", \"self\": \"https://devxray3.atlassian.net/rest/api/2/issue/32495\" }, { \"id\": \"32493\", \"key\": \"DEMO-15117\", \"self\": \"https://devxray3.atlassian.net/rest/api/2/issue/32493\" }], \"updatedOrCreatedPreconditions\": [ { \"id\": \"12345\", \"key\": \"DEMO-12345\", \"self\": \"https://devxray3.atlassian.net/rest/api/2/issue/12345\"} ] }")));
    }
 
    @MavenTest
    @MavenGoal("xray:import-features")
    @SystemProperty(value = "xray.cloud", content = "true")
    @SystemProperty(value = "xray.clientId", content = CLIENT_ID)
    @SystemProperty(value = "xray.clientSecret", content = CLIENT_SECRET)
    @SystemProperty(value = "xray.useInternalTestProxy", content = "true")
    @SystemProperty(value = "xray.projectKey", content = "CALC")
    @SystemProperty(value = "xray.inputFeatures", content = "dummy.feature")
    @Requirement("XMP-125")
    void single_feature(MavenExecutionResult result) throws IOException {
       String feature = TestingUtils.readResourceFileForImportFeatures("XrayCloudIT/single_feature/dummy.feature");

        wm.verify(
            postRequestedFor(urlPathEqualTo("/api/v2/import/feature"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withHeader("Content-Type", containing("multipart/form-data;"))
                .withQueryParam("projectKey", equalTo("CALC"))
                .withAnyRequestBodyPart(
                    aMultipart()
                        .withName("file")
                        .withHeader("Content-Type", containing("text/plain"))
                        .withBody(equalTo(feature)))

        );
       assertThat(result).isSuccessful();
    }

    @MavenTest
    @MavenGoal("xray:import-features")
    @SystemProperty(value = "xray.cloud", content = "true")
    @SystemProperty(value = "xray.clientId", content = CLIENT_ID)
    @SystemProperty(value = "xray.clientSecret", content = CLIENT_SECRET)
    @SystemProperty(value = "xray.useInternalTestProxy", content = "true")
    @SystemProperty(value = "xray.projectKey", content = "CALC")
    @SystemProperty(value = "xray.inputFeatures", content = "dummy.feature")
    @SystemProperty(value = "xray.precondInfoJson", content = "precondInfo.json")
    @SystemProperty(value = "xray.testInfoJson", content = "testInfo.json")
    @Requirement("XMP-125")
    void single_feature_custom_test_precondition_info(MavenExecutionResult result) throws IOException {
       String feature = TestingUtils.readResourceFileForImportFeatures("XrayCloudIT/single_feature_custom_test_precondition_info/dummy.feature");
       String precondInfo = TestingUtils.readResourceFileForImportFeatures("XrayCloudIT/single_feature_custom_test_precondition_info/precondInfo.json");
       String testInfo = TestingUtils.readResourceFileForImportFeatures("XrayCloudIT/single_feature_custom_test_precondition_info/testInfo.json");

        wm.verify(
            postRequestedFor(urlPathEqualTo("/api/v2/import/feature"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withHeader("Content-Type", containing("multipart/form-data;"))
                .withQueryParam("projectKey", equalTo("CALC"))
                .withAnyRequestBodyPart(
                    aMultipart()
                        .withName("file")
                        .withHeader("Content-Type", containing("text/plain"))
                        .withBody(equalTo(feature))
                    )
                .withAnyRequestBodyPart(
                    aMultipart()
                        .withName("precondInfo")
                        .withHeader("Content-Type", containing("application/json"))
                        .withBody(equalToJson(precondInfo))
                    )
                .withAnyRequestBodyPart(
                    aMultipart()
                        .withName("testInfo")
                        .withHeader("Content-Type", containing("application/json"))
                        .withBody(equalToJson(testInfo))
                    )

        );
       assertThat(result).isSuccessful();
    }

    @MavenTest
    @MavenGoal("xray:import-features")
    @SystemProperty(value = "xray.cloud", content = "true")
    @SystemProperty(value = "xray.clientId", content = CLIENT_ID)
    @SystemProperty(value = "xray.clientSecret", content = CLIENT_SECRET)
    @SystemProperty(value = "xray.useInternalTestProxy", content = "true")
    @SystemProperty(value = "xray.projectKey", content = "CALC")
    @SystemProperty(value = "xray.inputFeatures", content = "features.zip")
    @Requirement("XMP-125")
    void multiple_features(MavenExecutionResult result) throws IOException {
       byte[] zippedContent = TestingUtils.readRawResourceFile("import_features/XrayCloudIT/multiple_features/features.zip");

        wm.verify(
            postRequestedFor(urlPathEqualTo("/api/v2/import/feature"))
                .withHeader("Authorization", equalTo("Bearer " + TOKEN))
                .withHeader("Content-Type", containing("multipart/form-data;"))
                .withQueryParam("projectKey", equalTo("CALC"))
                .withAnyRequestBodyPart(
                    aMultipart()
                        .withName("file")
                        .withHeader("Content-Type", containing("application/zip"))
                        .withBody(binaryEqualTo(zippedContent)))

        );
       assertThat(result).isSuccessful();
    }

}
