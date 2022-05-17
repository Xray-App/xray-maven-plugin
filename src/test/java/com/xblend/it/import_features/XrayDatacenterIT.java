package com.xblend.it.import_features;

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
import com.xblend.it.CommonUtils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

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

        // cucumber
        wm.stubFor(post(urlPathEqualTo("/rest/raven/2.0/import/feature"))
            .willReturn(okJson("[ { \"id\":\"14400\", \"key\":\"DEV-915\", \"self\":\"http://localhost:8727/rest/api/2/issue/14400\", \"issueType\": { \"id\": \"10100\", \"name\": \"Test\" } }, { \"id\":\"14401\", \"key\":\"DEV-916\", \"self\":\"http://localhost:8727/rest/api/2/issue/14401\", \"issueType\": { \"id\": \"10103\", \"name\": \"Pre-Condition\" } } ]")));

        System.out.println("setting up stubs - done.");
    }

    @MavenTest
    @MavenGoal("xray:import-features")
    @SystemProperty(value = "xray.cloud", content = "false")
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:18080")
    @SystemProperty(value = "xray.jiraUsername", content = "username")
    @SystemProperty(value = "xray.jiraPassword", content = "password")
    @SystemProperty(value = "xray.projectKey", content = "CALC")
    @SystemProperty(value = "xray.inputFeatures", content = "dummy.feature")
    void single_feature(MavenExecutionResult result) throws IOException {
       String feature = CommonUtils.readResourceFileForImportFeatures("XrayDatacenterIT/single_feature/dummy.feature");

        wm.verify(
            postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/feature"))
                .withBasicAuth(new BasicCredentials("username", "password"))
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
    @SystemProperty(value = "xray.cloud", content = "false")
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:18080")
    @SystemProperty(value = "xray.jiraUsername", content = "username")
    @SystemProperty(value = "xray.jiraPassword", content = "password")
    @SystemProperty(value = "xray.projectKey", content = "CALC")
    @SystemProperty(value = "xray.inputFeatures", content = "dummy.feature")
    @SystemProperty(value = "xray.precondInfoJson", content = "precondInfo.json")
    @SystemProperty(value = "xray.testInfoJson", content = "testInfo.json")
    void single_feature_custom_test_precondition_info(MavenExecutionResult result) throws IOException {
       String feature = CommonUtils.readResourceFileForImportFeatures("XrayDatacenterIT/single_feature_custom_test_precondition_info/dummy.feature");
       String precondInfo = CommonUtils.readResourceFileForImportFeatures("XrayDatacenterIT/single_feature_custom_test_precondition_info/precondInfo.json");
       String testInfo = CommonUtils.readResourceFileForImportFeatures("XrayDatacenterIT/single_feature_custom_test_precondition_info/testInfo.json");

        wm.verify(
            postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/feature"))
                .withBasicAuth(new BasicCredentials("username", "password"))
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
                        .withName("preCondInfo")
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
    @SystemProperty(value = "xray.cloud", content = "false")
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:18080")
    @SystemProperty(value = "xray.jiraUsername", content = "username")
    @SystemProperty(value = "xray.jiraPassword", content = "password")
    @SystemProperty(value = "xray.projectKey", content = "CALC")
    @SystemProperty(value = "xray.inputFeatures", content = "features.zip")
    void multiple_features(MavenExecutionResult result) throws IOException {
       byte[] zippedContent = CommonUtils.readRawResourceFile("import_features/XrayDatacenterIT/multiple_features/features.zip");

        wm.verify(
            postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/feature"))
                .withBasicAuth(new BasicCredentials("username", "password"))
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

    @MavenTest
    @MavenGoal("xray:import-features")
    @SystemProperty(value = "xray.cloud", content = "false")
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:18080")
    @SystemProperty(value = "xray.jiraUsername", content = "username")
    @SystemProperty(value = "xray.jiraPassword", content = "password")
    @SystemProperty(value = "xray.projectKey", content = "CALC")
    @SystemProperty(value = "xray.inputFeatures", content = "features.zip")
    @SystemProperty(value = "xray.updateRepository", content = "true")
    void multiple_features_update_testrepo(MavenExecutionResult result) throws IOException {
       byte[] zippedContent = CommonUtils.readRawResourceFile("import_features/XrayDatacenterIT/multiple_features/features.zip");

        wm.verify(
            postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/feature"))
                .withBasicAuth(new BasicCredentials("username", "password"))
                .withHeader("Content-Type", containing("multipart/form-data;"))
                .withQueryParam("projectKey", equalTo("CALC"))
                .withQueryParam("updateRepository", equalTo("true"))
                .withAnyRequestBodyPart(
                    aMultipart()
                        .withName("file")
                        .withHeader("Content-Type", containing("application/zip"))
                        .withBody(binaryEqualTo(zippedContent)))

        );
       assertThat(result).isSuccessful();
    }


}
