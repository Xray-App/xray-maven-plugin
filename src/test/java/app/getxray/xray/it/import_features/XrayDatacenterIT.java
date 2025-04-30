package app.getxray.xray.it.import_features;

import static app.getxray.xray.CommonUtils.unzipContentsToFolder;
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
import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.BasicCredentials;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenOption;
import com.soebes.itf.jupiter.extension.MavenRepository;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.extension.SystemProperty;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

import app.getxray.xray.DCCustomDisplayNameGenerator;
import app.getxray.xray.it.TestingUtils;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

@MavenJupiterExtension
@DisplayNameGeneration(DCCustomDisplayNameGenerator.class)
@MavenRepository
public class XrayDatacenterIT {

    static WireMockServer wm;
    static final int PORT_NUMBER = 18080;

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

        // cucumber
        wm.stubFor(post(urlPathEqualTo("/rest/raven/2.0/import/feature"))
            .willReturn(okJson("[ { \"id\":\"14400\", \"key\":\"DEV-915\", \"self\":\"http://localhost:8727/rest/api/2/issue/14400\", \"issueType\": { \"id\": \"10100\", \"name\": \"Test\" } }, { \"id\":\"14401\", \"key\":\"DEV-916\", \"self\":\"http://localhost:8727/rest/api/2/issue/14401\", \"issueType\": { \"id\": \"10103\", \"name\": \"Pre-Condition\" } } ]")));

        System.out.println("setting up stubs - done.");
    }

    @MavenTest
    @MavenGoal("xray:import-features")
    @SystemProperty(value = "xray.cloud", content = "false")
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:"+PORT_NUMBER)
    @SystemProperty(value = "xray.jiraUsername", content = "username")
    @SystemProperty(value = "xray.jiraPassword", content = "password")
    @SystemProperty(value = "xray.projectKey", content = "CALC")
    @SystemProperty(value = "xray.inputFeatures", content = "dummy.feature")
    @Requirement("XMP-125")
    void single_feature(MavenExecutionResult result) throws IOException {
       String feature = TestingUtils.readResourceFileForImportFeatures("XrayDatacenterIT/single_feature/dummy.feature");

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
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:"+PORT_NUMBER)
    @SystemProperty(value = "xray.jiraToken", content = "00112233445566778899")
    @SystemProperty(value = "xray.projectKey", content = "CALC")
    @SystemProperty(value = "xray.inputFeatures", content = "dummy.feature")
    @Requirement("XMP-125")
    void single_feature_using_personal_access_token(MavenExecutionResult result) throws IOException {
       String feature = TestingUtils.readResourceFileForImportFeatures("XrayDatacenterIT/single_feature_using_personal_access_token/dummy.feature");

        wm.verify(
            postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/feature"))
                .withHeader("Authorization", equalTo("Bearer 00112233445566778899"))
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
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:"+PORT_NUMBER)
    @SystemProperty(value = "xray.jiraUsername", content = "username")
    @SystemProperty(value = "xray.jiraPassword", content = "password")
    @SystemProperty(value = "xray.projectKey", content = "CALC")
    @SystemProperty(value = "xray.inputFeatures", content = "dummy.feature")
    @SystemProperty(value = "xray.precondInfoJson", content = "precondInfo.json")
    @SystemProperty(value = "xray.testInfoJson", content = "testInfo.json")
    @Requirement("XMP-125")
    void single_feature_custom_test_precondition_info(MavenExecutionResult result) throws IOException {
       String feature = TestingUtils.readResourceFileForImportFeatures("XrayDatacenterIT/single_feature_custom_test_precondition_info/dummy.feature");
       String precondInfo = TestingUtils.readResourceFileForImportFeatures("XrayDatacenterIT/single_feature_custom_test_precondition_info/precondInfo.json");
       String testInfo = TestingUtils.readResourceFileForImportFeatures("XrayDatacenterIT/single_feature_custom_test_precondition_info/testInfo.json");

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
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:"+PORT_NUMBER)
    @SystemProperty(value = "xray.jiraUsername", content = "username")
    @SystemProperty(value = "xray.jiraPassword", content = "password")
    @SystemProperty(value = "xray.projectKey", content = "CALC")
    @SystemProperty(value = "xray.inputFeatures", content = "features.zip")
    @Requirement("XMP-125")
    void multiple_features(MavenExecutionResult result) throws IOException {
       byte[] zippedContent = TestingUtils.readRawResourceFile("import_features/XrayDatacenterIT/multiple_features/features.zip");

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
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:"+PORT_NUMBER)
    @SystemProperty(value = "xray.jiraUsername", content = "username")
    @SystemProperty(value = "xray.jiraPassword", content = "password")
    @SystemProperty(value = "xray.projectKey", content = "CALC")
    @SystemProperty(value = "xray.inputFeatures", content = "features.zip")
    @SystemProperty(value = "xray.updateRepository", content = "true")
    @Requirement("XMP-125")
    void multiple_features_update_testrepo(MavenExecutionResult result) throws IOException {
       byte[] zippedContent = TestingUtils.readRawResourceFile("import_features/XrayDatacenterIT/multiple_features/features.zip");

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

    @MavenTest
    @MavenGoal("xray:import-features")
    @SystemProperty(value = "xray.cloud", content = "false")
    @SystemProperty(value = "xray.jiraBaseUrl", content = "http://127.0.0.1:"+PORT_NUMBER)
    @SystemProperty(value = "xray.jiraUsername", content = "username")
    @SystemProperty(value = "xray.jiraPassword", content = "password")
    @SystemProperty(value = "xray.projectKey", content = "CALC")
    @SystemProperty(value = "xray.inputFeatures", content = "./features")
    @Requirement("XMP-125")
    @SystemProperty(value = "xray.verbose", content = "true")
    @MavenOption("--debug")
    void multiple_features_from_directory(MavenExecutionResult result) throws IOException {
        wm.verify(
            postRequestedFor(urlPathEqualTo("/rest/raven/2.0/import/feature"))
                .withBasicAuth(new BasicCredentials("username", "password"))
                .withHeader("Content-Type", containing("multipart/form-data;"))
                .withQueryParam("projectKey", equalTo("CALC"))
                .withAnyRequestBodyPart(
                    aMultipart()
                        .withName("file")
                        .withHeader("Content-Type", containing("application/zip"))
                )
        );

        List<ServeEvent> allServeEvents = wm.getAllServeEvents();
        File tempDir = Files.createTempDirectory("features").toFile();
        ServeEvent request = allServeEvents.get(0);
        byte[] zippedContent = request.getRequest().getPart("file").getBody().asBytes();
        InputStream zippedContentStream = new ByteArrayInputStream(zippedContent);
        unzipContentsToFolder(zippedContentStream, tempDir.getAbsolutePath().toString());
        assertThat(tempDir.listFiles()).hasSize(2);
        assertThat(tempDir.listFiles()).extracting(File::getName).containsExactlyInAnyOrder("core", "other");
        assertThat((new File(tempDir, "core")).listFiles()).extracting(File::getName).containsExactlyInAnyOrder("positive_sum.feature");
        assertThat((new File(tempDir, "other")).listFiles()).extracting(File::getName).containsExactlyInAnyOrder("negative_sum.feature");
        assertThat(result).isSuccessful();
    }



}
