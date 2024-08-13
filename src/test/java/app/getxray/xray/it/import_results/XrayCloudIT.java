package app.getxray.xray.it.import_results;

import static com.github.tomakehurst.wiremock.client.WireMock.aMultipart;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToXml;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenOption;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.extension.SystemProperty;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

import app.getxray.xray.CloudCustomDisplayNameGenerator;
import app.getxray.xray.it.TestingUtils;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;


@MavenJupiterExtension
@DisplayNameGeneration(CloudCustomDisplayNameGenerator.class)
public class XrayCloudIT {

    private static final String CLIENT_ID = "32A27E69C0AC4E539C14010000000000";
    private static final String CLIENT_SECRET = "d62f81eb9ed859e22e54356dd8a00e4a5f0d0c2b2b52340776f6c70000000000";
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0ZW5hbnQiOiI0MjZiYzA4Yy02N2VmLTNjMjYtYWU1YS03NjczYTB1ZjIwNjkiLCJ1c2VyS2V5IjoiYW5kcmUucm9kcmlndWVzIiwiaWF0IjixNTI1ODcxODkzLCJleHAiOjE1MjU5NTgyOTMsImF1ZCI6IhMyQTI3RTY5QjBBQzRFNTM5QzE0MDE2NDM3OTlFOEU3IiwiaXNzIjoiY29tLnhwYW5kaXQueHJheSIsInN1YiI6IjMyQTI3RTY5QjBBQzRFNTM5QzE0MDE2NDM3OTlFOEU3In0.8ah2IQ9rA_zotyh_6trFgfIvhn2awdFFrOHnN2F2H7m";

    static WireMockServer wm;
    static final int PORT_NUMBER = 18080;

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

        // xray
        wm.stubFor(post(urlPathEqualTo("/api/v2/import/execution"))
            .withHost(equalTo("xray.cloud.getxray.app"))
            .willReturn(okJson("{ \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" }")));
        wm.stubFor(post(urlPathEqualTo("/api/v2/import/execution/multipart"))
            .withHost(equalTo("xray.cloud.getxray.app"))
            .willReturn(okJson("{ \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" }")));

        // junit
        wm.stubFor(post(urlPathEqualTo("/api/v2/import/execution/junit"))
            .withHost(equalTo("xray.cloud.getxray.app"))
            .willReturn(okJson("{ \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" }")));
        wm.stubFor(post(urlPathEqualTo("/api/v2/import/execution/junit/multipart"))
            .withHost(equalTo("xray.cloud.getxray.app"))
            .willReturn(okJson("{ \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" }")));

        // testng
        wm.stubFor(post(urlPathEqualTo("/api/v2/import/execution/testng"))
            .withHost(equalTo("xray.cloud.getxray.app"))
            .willReturn(okJson("{ \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" }")));
        wm.stubFor(post(urlPathEqualTo("/api/v2/import/execution/testng/multipart"))
            .withHost(equalTo("xray.cloud.getxray.app"))
            .willReturn(okJson("{ \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" }")));
        System.out.println("setting up stubs... done");

        // nunit
        wm.stubFor(post(urlPathEqualTo("/api/v2/import/execution/nunit"))
            .withHost(equalTo("xray.cloud.getxray.app"))
            .willReturn(okJson("{ \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" }")));
        wm.stubFor(post(urlPathEqualTo("/api/v2/import/execution/nunit/multipart"))
            .withHost(equalTo("xray.cloud.getxray.app"))
            .willReturn(okJson("{ \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" }")));

        // xunit
        wm.stubFor(post(urlPathEqualTo("/api/v2/import/execution/xunit"))
            .withHost(equalTo("xray.cloud.getxray.app"))
            .willReturn(okJson("{ \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" }")));
        wm.stubFor(post(urlPathEqualTo("/api/v2/import/execution/xunit/multipart"))
            .withHost(equalTo("xray.cloud.getxray.app"))
            .willReturn(okJson("{ \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" }")));

        // robot
        wm.stubFor(post(urlPathEqualTo("/api/v2/import/execution/robot"))
            .withHost(equalTo("xray.cloud.getxray.app"))
            .willReturn(okJson("{ \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" }")));
        wm.stubFor(post(urlPathEqualTo("/api/v2/import/execution/robot/multipart"))
            .withHost(equalTo("xray.cloud.getxray.app"))
            .willReturn(okJson("{ \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" }")));

        // cucumber
        wm.stubFor(post(urlPathEqualTo("/api/v2/import/execution/cucumber"))
            .withHost(equalTo("xray.cloud.getxray.app"))
            .willReturn(okJson("{ \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" }")));
        wm.stubFor(post(urlPathEqualTo("/api/v2/import/execution/cucumber/multipart"))
            .withHost(equalTo("xray.cloud.getxray.app"))
            .willReturn(okJson("{ \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" }")));

        // behave
        wm.stubFor(post(urlPathEqualTo("/api/v2/import/execution/behave"))
            .withHost(equalTo("xray.cloud.getxray.app"))
            .willReturn(okJson("{ \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" }")));
        wm.stubFor(post(urlPathEqualTo("/api/v2/import/execution/behave/multipart"))
            .withHost(equalTo("xray.cloud.getxray.app"))
            .willReturn(okJson("{ \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" }")));
    

        System.out.println("setting up stubs for custom, region-based, Xray Cloud REST API endpoint...");
        wm.stubFor(post(urlPathEqualTo("/api/v2/authenticate"))
            .withHost(equalTo("eu.xray.cloud.getxray.app"))
            .willReturn(okJson(TOKEN))
            .atPriority(1)
        );
        wm.stubFor(post(urlPathEqualTo("/api/v2/import/execution"))
            .withHost(equalTo("eu.xray.cloud.getxray.app"))
            .willReturn(okJson("{ \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" }")));
        wm.stubFor(post(urlPathEqualTo("/api/v2/import/execution/multipart"))
            .withHost(equalTo("eu.xray.cloud.getxray.app"))
            .willReturn(okJson("{ \"id\": \"10200\", \"key\": \"CALC-1\", \"self\": \"http://127.0.0.1/jira/rest/api/2/issue/10200\" }")));
    }

    @MavenTest
    @MavenGoal("xray:import-results")
    @SystemProperty(value = "xray.cloud", content = "true")
    @SystemProperty(value = "xray.clientId", content = CLIENT_ID)
    @SystemProperty(value = "xray.clientSecret", content = CLIENT_SECRET)
    @SystemProperty(value = "xray.reportFormat", content = "xray")
    @SystemProperty(value = "xray.reportFile", content = "xray.json")
    @SystemProperty(value = "xray.useInternalTestProxy", content = "true")
    @SystemProperty(value = "xray.verbose", content = "true")
    @MavenOption("--debug")
    @Requirement("XMP-132")
    void xray_standard_with_verbose_mode(MavenExecutionResult result) throws IOException {
        assertThat(result).isSuccessful();
        assertThat(result)
            .out()
            .debug()
            .containsOnlyOnce("REQUEST_URL: https://xray.cloud.getxray.app/api/v2/authenticate");
        assertThat(result)
            .out()
            .debug()
            .containsOnlyOnce("REQUEST_URL: https://xray.cloud.getxray.app/api/v2/import/execution");
        assertThat(result)
            .out()
            .debug()
            .contains("REQUEST_METHOD: POST");
        assertThat(result)
            .out()
            .debug()
            .contains("REQUEST_CONTENT_TYPE: application/json; charset=utf-8");
    }


    @MavenTest
    @MavenGoal("xray:import-results")
    @SystemProperty(value = "xray.cloud", content = "true")
    @SystemProperty(value = "xray.clientId", content = CLIENT_ID)
    @SystemProperty(value = "xray.clientSecret", content = CLIENT_SECRET)
    @SystemProperty(value = "xray.cloudApiBaseUrl", content = "https://eu.xray.cloud.getxray.app/api/v2")
    @SystemProperty(value = "xray.reportFormat", content = "xray")
    @SystemProperty(value = "xray.reportFile", content = "xray.json")
    @SystemProperty(value = "xray.useInternalTestProxy", content = "true")
    @Requirement("XMP-132")
    void xray_standard_eu_region(MavenExecutionResult result) throws IOException {
       String report = TestingUtils.readResourceFileForImportResults("XrayCloudIT/xray_standard_eu_region/xray.json");

        wm.verify(
            postRequestedFor(null)
                .withHost(equalTo("eu.xray.cloud.getxray.app"))
                .withUrl("/api/v2/authenticate")
                .withHeader("Content-Type", containing("application/json"))
        );
        wm.verify(
            postRequestedFor(urlPathEqualTo("/api/v2/import/execution"))
                .withHost(equalTo("eu.xray.cloud.getxray.app"))
                .withUrl("/api/v2/import/execution")
                .withHeader("Content-Type", containing("application/json"))
                .withRequestBody(equalToJson(report))
        );

       assertThat(result).isSuccessful();
    }

    @MavenTest
    @MavenGoal("xray:import-results")
    @SystemProperty(value = "xray.cloud", content = "true")
    @SystemProperty(value = "xray.clientId", content = CLIENT_ID)
    @SystemProperty(value = "xray.clientSecret", content = CLIENT_SECRET)
    @SystemProperty(value = "xray.reportFormat", content = "xray")
    @SystemProperty(value = "xray.reportFile", content = "xray.json")
    @SystemProperty(value = "xray.useInternalTestProxy", content = "true")
    @Requirement("XMP-132")
    void xray_standard(MavenExecutionResult result) throws IOException {
       String report = TestingUtils.readResourceFileForImportResults("XrayCloudIT/xray_standard/xray.json");

       wm.verify(
           postRequestedFor(urlPathEqualTo("/api/v2/import/execution"))
               .withHeader("Content-Type", containing("application/json"))
               .withRequestBody(equalToJson(report))
       );
       assertThat(result).isSuccessful();
    }

    @MavenTest
    @MavenGoal("xray:import-results")
    @SystemProperty(value = "xray.cloud", content = "true")
    @SystemProperty(value = "xray.clientId", content = CLIENT_ID)
    @SystemProperty(value = "xray.clientSecret", content = CLIENT_SECRET)
    @SystemProperty(value = "xray.reportFormat", content = "xray")
    @SystemProperty(value = "xray.reportFile", content = "xray.json")
    @SystemProperty(value = "xray.testExecInfoJson", content = "testExecInfo.json")
    @SystemProperty(value = "xray.useInternalTestProxy", content = "true")
    @Requirement("XMP-140")
    void xray_multipart(MavenExecutionResult result) throws IOException {
       String testExecInfo = TestingUtils.readResourceFileForImportResults("XrayCloudIT/xray_multipart/testExecInfo.json");
       String report = TestingUtils.readResourceFileForImportResults("XrayCloudIT/xray_multipart/xray.json");

       wm.verify(
           postRequestedFor(urlPathEqualTo("/api/v2/import/execution/multipart"))
               .withHeader("Authorization", equalTo("Bearer " + TOKEN))
               .withHeader("Content-Type", containing("multipart/form-data;"))
               .withAnyRequestBodyPart(
                   aMultipart()
                       .withName("results")
                       .withHeader("Content-Type", containing("application/json"))
                       .withBody(equalToJson(report)))
               .withAnyRequestBodyPart(
                   aMultipart()
                       .withName("info")
                       .withHeader("Content-Type", containing("application/json"))
                       .withBody(equalToJson(testExecInfo))
                   )

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
 @SystemProperty(value = "xray.projectKey", content = "CALC")
 @SystemProperty(value = "xray.testExecKey", content = "CALC-2")
 @SystemProperty(value = "xray.testPlanKey", content = "CALC-3")
 @SystemProperty(value = "xray.version", content = "1.0")
 @SystemProperty(value = "xray.revision", content = "123")
 @SystemProperty(value = "xray.testEnvironment", content = "chrome")
 @SystemProperty(value = "xray.useInternalTestProxy", content = "true")
 @Requirement("XMP-2")
 void junit_standard(MavenExecutionResult result) throws IOException {
    String report = TestingUtils.readResourceFileForImportResults("XrayCloudIT/junit_standard/junit.xml");

    wm.verify(
        postRequestedFor(urlPathEqualTo("/api/v2/import/execution/junit"))
            .withHeader("Content-Type", containing("application/xml"))
            .withQueryParam("projectKey", equalTo("CALC"))
            .withQueryParam("testExecKey", equalTo("CALC-2"))
            .withQueryParam("testPlanKey", equalTo("CALC-3"))
            .withQueryParam("fixVersion", equalTo("1.0"))
            .withQueryParam("revision", equalTo("123"))
            .withQueryParam("testEnvironments", equalTo("chrome"))
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
 @SystemProperty(value = "xray.testExecInfoJson", content = "testExecInfo.json")
 @SystemProperty(value = "xray.useInternalTestProxy", content = "true")
 @Requirement("XMP-3")
 void junit_multipart(MavenExecutionResult result) throws IOException {
    String testExecInfo = TestingUtils.readResourceFileForImportResults("XrayCloudIT/junit_multipart/testExecInfo.json");
    String report = TestingUtils.readResourceFileForImportResults("XrayCloudIT/junit_multipart/junit.xml");

    wm.verify(
        postRequestedFor(urlPathEqualTo("/api/v2/import/execution/junit/multipart"))
            .withHeader("Authorization", equalTo("Bearer " + TOKEN))
            .withHeader("Content-Type", containing("multipart/form-data;"))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("results")
                    .withHeader("Content-Type", containing("application/xml"))
                    .withBody(equalToXml(report)))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("info")
                    .withHeader("Content-Type", containing("application/json"))
                    .withBody(equalToJson(testExecInfo)))

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
 @SystemProperty(value = "xray.testExecInfoJson", content = "testExecInfo.json")
 @SystemProperty(value = "xray.testInfoJson", content = "testInfo.json")
 @SystemProperty(value = "xray.useInternalTestProxy", content = "true")
 @Requirement("XMP-3")
 void junit_multipart_customize_test_issues(MavenExecutionResult result) throws IOException {
    String testExecInfo = TestingUtils.readResourceFileForImportResults("XrayCloudIT/junit_multipart_customize_test_issues/testExecInfo.json");
    String testInfo = TestingUtils.readResourceFileForImportResults("XrayCloudIT/junit_multipart_customize_test_issues/testInfo.json");
    String report = TestingUtils.readResourceFileForImportResults("XrayCloudIT/junit_multipart_customize_test_issues/junit.xml");

    wm.verify(
        postRequestedFor(urlPathEqualTo("/api/v2/import/execution/junit/multipart"))
            .withHeader("Authorization", equalTo("Bearer " + TOKEN))
            .withHeader("Content-Type", containing("multipart/form-data;"))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("results")
                    .withHeader("Content-Type", containing("application/xml"))
                    .withBody(equalToXml(report)))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("info")
                    .withHeader("Content-Type", containing("application/json"))
                    .withBody(equalToJson(testExecInfo))
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
 @MavenGoal("xray:import-results")
 @SystemProperty(value = "xray.cloud", content = "true")
 @SystemProperty(value = "xray.clientId", content = CLIENT_ID)
 @SystemProperty(value = "xray.clientSecret", content = CLIENT_SECRET)
 @SystemProperty(value = "xray.reportFormat", content = "testng")
 @SystemProperty(value = "xray.reportFile", content = "testng.xml")
 @SystemProperty(value = "xray.projectKey", content = "CALC")
 @SystemProperty(value = "xray.useInternalTestProxy", content = "true")
 @Requirement("XMP-4")
 void testng_standard(MavenExecutionResult result) throws IOException {
    String report = TestingUtils.readResourceFileForImportResults("XrayCloudIT/testng_standard/testng.xml");

    wm.verify(
        postRequestedFor(urlPathEqualTo("/api/v2/import/execution/testng"))
            .withHeader("Content-Type", containing("application/xml"))
            .withQueryParam("projectKey", equalTo("CALC"))
            .withRequestBody(equalToXml(report))
    );
    assertThat(result).isSuccessful();
 }

 @MavenTest
 @MavenGoal("xray:import-results")
 @SystemProperty(value = "xray.cloud", content = "true")
 @SystemProperty(value = "xray.clientId", content = CLIENT_ID)
 @SystemProperty(value = "xray.clientSecret", content = CLIENT_SECRET)
 @SystemProperty(value = "xray.reportFormat", content = "testng")
 @SystemProperty(value = "xray.reportFile", content = "testng.xml")
 @SystemProperty(value = "xray.testExecInfoJson", content = "testExecInfo.json")
 @SystemProperty(value = "xray.useInternalTestProxy", content = "true")
 @Requirement("XMP-139")
 void testng_multipart(MavenExecutionResult result) throws IOException {
    String testExecInfo = TestingUtils.readResourceFileForImportResults("XrayCloudIT/testng_multipart/testExecInfo.json");
    String report = TestingUtils.readResourceFileForImportResults("XrayCloudIT/testng_multipart/testng.xml");

    wm.verify(
        postRequestedFor(urlPathEqualTo("/api/v2/import/execution/testng/multipart"))
            .withHeader("Authorization", equalTo("Bearer " + TOKEN))
            .withHeader("Content-Type", containing("multipart/form-data;"))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("results")
                    .withHeader("Content-Type", containing("application/xml"))
                    .withBody(equalToXml(report)))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("info")
                    .withHeader("Content-Type", containing("application/json"))
                    .withBody(equalToJson(testExecInfo))
                )

    );
    assertThat(result).isSuccessful();
 }


 @MavenTest
 @MavenGoal("xray:import-results")
 @SystemProperty(value = "xray.cloud", content = "true")
 @SystemProperty(value = "xray.clientId", content = CLIENT_ID)
 @SystemProperty(value = "xray.clientSecret", content = CLIENT_SECRET)
 @SystemProperty(value = "xray.reportFormat", content = "nunit")
 @SystemProperty(value = "xray.reportFile", content = "nunit.xml")
 @SystemProperty(value = "xray.projectKey", content = "CALC")
 @SystemProperty(value = "xray.useInternalTestProxy", content = "true")
 @Requirement("XMP-133")
 void nunit_standard(MavenExecutionResult result) throws IOException {
    String report = TestingUtils.readResourceFileForImportResults("XrayCloudIT/nunit_standard/nunit.xml");

    wm.verify(
        postRequestedFor(urlPathEqualTo("/api/v2/import/execution/nunit"))
            .withHeader("Content-Type", containing("application/xml"))
            .withQueryParam("projectKey", equalTo("CALC"))
            .withRequestBody(equalTo(report))
    );
    assertThat(result).isSuccessful();
 }

 @MavenTest
 @MavenGoal("xray:import-results")
 @SystemProperty(value = "xray.cloud", content = "true")
 @SystemProperty(value = "xray.clientId", content = CLIENT_ID)
 @SystemProperty(value = "xray.clientSecret", content = CLIENT_SECRET)
 @SystemProperty(value = "xray.reportFormat", content = "nunit")
 @SystemProperty(value = "xray.reportFile", content = "nunit.xml")
 @SystemProperty(value = "xray.testExecInfoJson", content = "testExecInfo.json")
 @SystemProperty(value = "xray.useInternalTestProxy", content = "true")
 @Requirement("XMP-134")
 void nunit_multipart(MavenExecutionResult result) throws IOException {
    String testExecInfo = TestingUtils.readResourceFileForImportResults("XrayCloudIT/nunit_multipart/testExecInfo.json");
    String report = TestingUtils.readResourceFileForImportResults("XrayCloudIT/nunit_multipart/nunit.xml");

    wm.verify(
        postRequestedFor(urlPathEqualTo("/api/v2/import/execution/nunit/multipart"))
            .withHeader("Authorization", equalTo("Bearer " + TOKEN))
            .withHeader("Content-Type", containing("multipart/form-data;"))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("results")
                    .withHeader("Content-Type", containing("application/xml"))
                    .withBody(equalTo(report)))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("info")
                    .withHeader("Content-Type", containing("application/json"))
                    .withBody(equalToJson(testExecInfo))
                )

    );
    assertThat(result).isSuccessful();
 }


 @MavenTest
 @MavenGoal("xray:import-results")
 @SystemProperty(value = "xray.cloud", content = "true")
 @SystemProperty(value = "xray.clientId", content = CLIENT_ID)
 @SystemProperty(value = "xray.clientSecret", content = CLIENT_SECRET)
 @SystemProperty(value = "xray.reportFormat", content = "xunit")
 @SystemProperty(value = "xray.reportFile", content = "xunit.xml")
 @SystemProperty(value = "xray.projectKey", content = "CALC")
 @SystemProperty(value = "xray.useInternalTestProxy", content = "true")
 @Requirement("XMP-128")
 void xunit_standard(MavenExecutionResult result) throws IOException {
    String report = TestingUtils.readResourceFileForImportResults("XrayCloudIT/xunit_standard/xunit.xml");

    wm.verify(
        postRequestedFor(urlPathEqualTo("/api/v2/import/execution/xunit"))
            .withHeader("Content-Type", containing("application/xml"))
            .withQueryParam("projectKey", equalTo("CALC"))
            .withRequestBody(equalTo(report))
    );
    assertThat(result).isSuccessful();
 }

 @MavenTest
 @MavenGoal("xray:import-results")
 @SystemProperty(value = "xray.cloud", content = "true")
 @SystemProperty(value = "xray.clientId", content = CLIENT_ID)
 @SystemProperty(value = "xray.clientSecret", content = CLIENT_SECRET)
 @SystemProperty(value = "xray.reportFormat", content = "xunit")
 @SystemProperty(value = "xray.reportFile", content = "xunit.xml")
 @SystemProperty(value = "xray.testExecInfoJson", content = "testExecInfo.json")
 @SystemProperty(value = "xray.useInternalTestProxy", content = "true")
 @Requirement("XMP-129")
 void xunit_multipart(MavenExecutionResult result) throws IOException {
    String testExecInfo = TestingUtils.readResourceFileForImportResults("XrayCloudIT/xunit_multipart/testExecInfo.json");
    String report = TestingUtils.readResourceFileForImportResults("XrayCloudIT/xunit_multipart/xunit.xml");

    wm.verify(
        postRequestedFor(urlPathEqualTo("/api/v2/import/execution/xunit/multipart"))
            .withHeader("Authorization", equalTo("Bearer " + TOKEN))
            .withHeader("Content-Type", containing("multipart/form-data;"))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("results")
                    .withHeader("Content-Type", containing("application/xml"))
                    .withBody(equalTo(report)))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("info")
                    .withHeader("Content-Type", containing("application/json"))
                    .withBody(equalToJson(testExecInfo))
                )

    );
    assertThat(result).isSuccessful();
 }


 @MavenTest
 @MavenGoal("xray:import-results")
 @SystemProperty(value = "xray.cloud", content = "true")
 @SystemProperty(value = "xray.clientId", content = CLIENT_ID)
 @SystemProperty(value = "xray.clientSecret", content = CLIENT_SECRET)
 @SystemProperty(value = "xray.reportFormat", content = "robot")
 @SystemProperty(value = "xray.reportFile", content = "robot.xml")
 @SystemProperty(value = "xray.projectKey", content = "CALC")
 @SystemProperty(value = "xray.useInternalTestProxy", content = "true")
 @Requirement("XMP-137")
 void robot_standard(MavenExecutionResult result) throws IOException {
    String report = TestingUtils.readResourceFileForImportResults("XrayCloudIT/robot_standard/robot.xml");

    wm.verify(
        postRequestedFor(urlPathEqualTo("/api/v2/import/execution/robot"))
            .withHeader("Content-Type", containing("application/xml"))
            .withQueryParam("projectKey", equalTo("CALC"))
            .withRequestBody(equalToXml(report))
    );
    assertThat(result).isSuccessful();
 }

 @MavenTest
 @MavenGoal("xray:import-results")
 @SystemProperty(value = "xray.cloud", content = "true")
 @SystemProperty(value = "xray.clientId", content = CLIENT_ID)
 @SystemProperty(value = "xray.clientSecret", content = CLIENT_SECRET)
 @SystemProperty(value = "xray.reportFormat", content = "robot")
 @SystemProperty(value = "xray.reportFile", content = "robot.xml")
 @SystemProperty(value = "xray.testExecInfoJson", content = "testExecInfo.json")
 @SystemProperty(value = "xray.useInternalTestProxy", content = "true")
 @Requirement("XMP-138")
 void robot_multipart(MavenExecutionResult result) throws IOException {
    String testExecInfo = TestingUtils.readResourceFileForImportResults("XrayCloudIT/robot_multipart/testExecInfo.json");
    String report = TestingUtils.readResourceFileForImportResults("XrayCloudIT/robot_multipart/robot.xml");

    wm.verify(
        postRequestedFor(urlPathEqualTo("/api/v2/import/execution/robot/multipart"))
            .withHeader("Authorization", equalTo("Bearer " + TOKEN))
            .withHeader("Content-Type", containing("multipart/form-data;"))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("results")
                    .withHeader("Content-Type", containing("application/xml"))
                    .withBody(equalToXml(report)))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("info")
                    .withHeader("Content-Type", containing("application/json"))
                    .withBody(equalToJson(testExecInfo))
                )

    );
    assertThat(result).isSuccessful();
 }

 @MavenTest
 @MavenGoal("xray:import-results")
 @SystemProperty(value = "xray.cloud", content = "true")
 @SystemProperty(value = "xray.clientId", content = CLIENT_ID)
 @SystemProperty(value = "xray.clientSecret", content = CLIENT_SECRET)
 @SystemProperty(value = "xray.reportFormat", content = "cucumber")
 @SystemProperty(value = "xray.reportFile", content = "cucumber.json")
 @SystemProperty(value = "xray.useInternalTestProxy", content = "true")
 @Requirement("XMP-130")
 void cucumber_standard(MavenExecutionResult result) throws IOException {
    String report = TestingUtils.readResourceFileForImportResults("XrayCloudIT/cucumber_standard/cucumber.json");

    wm.verify(
        postRequestedFor(urlPathEqualTo("/api/v2/import/execution/cucumber"))
            .withHeader("Content-Type", containing("application/json"))
            .withRequestBody(equalToJson(report))
    );
    assertThat(result).isSuccessful();
 }

 @MavenTest
 @MavenGoal("xray:import-results")
 @SystemProperty(value = "xray.cloud", content = "true")
 @SystemProperty(value = "xray.clientId", content = CLIENT_ID)
 @SystemProperty(value = "xray.clientSecret", content = CLIENT_SECRET)
 @SystemProperty(value = "xray.reportFormat", content = "cucumber")
 @SystemProperty(value = "xray.reportFile", content = "cucumber.json")
 @SystemProperty(value = "xray.testExecInfoJson", content = "testExecInfo.json")
 @SystemProperty(value = "xray.useInternalTestProxy", content = "true")
 @Requirement("XMP-131")
 void cucumber_multipart(MavenExecutionResult result) throws IOException {
    String testExecInfo = TestingUtils.readResourceFileForImportResults("XrayCloudIT/cucumber_multipart/testExecInfo.json");
    String report = TestingUtils.readResourceFileForImportResults("XrayCloudIT/cucumber_multipart/cucumber.json");

    wm.verify(
        postRequestedFor(urlPathEqualTo("/api/v2/import/execution/cucumber/multipart"))
            .withHeader("Authorization", equalTo("Bearer " + TOKEN))
            .withHeader("Content-Type", containing("multipart/form-data;"))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("results")
                    .withHeader("Content-Type", containing("application/json"))
                    .withBody(equalToJson(report)))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("info")
                    .withHeader("Content-Type", containing("application/json"))
                    .withBody(equalToJson(testExecInfo))
                )

    );
    assertThat(result).isSuccessful();
 }

 @MavenTest
 @MavenGoal("xray:import-results")
 @SystemProperty(value = "xray.cloud", content = "true")
 @SystemProperty(value = "xray.clientId", content = CLIENT_ID)
 @SystemProperty(value = "xray.clientSecret", content = CLIENT_SECRET)
 @SystemProperty(value = "xray.reportFormat", content = "behave")
 @SystemProperty(value = "xray.reportFile", content = "behave.json")
 @SystemProperty(value = "xray.useInternalTestProxy", content = "true")
 @Requirement("XMP-135")
 void behave_standard(MavenExecutionResult result) throws IOException {
    String report = TestingUtils.readResourceFileForImportResults("XrayCloudIT/behave_standard/behave.json");

    wm.verify(
        postRequestedFor(urlPathEqualTo("/api/v2/import/execution/behave"))
            .withHeader("Content-Type", containing("application/json"))
            .withRequestBody(equalToJson(report))
    );
    assertThat(result).isSuccessful();
 }

 @MavenTest
 @MavenGoal("xray:import-results")
 @SystemProperty(value = "xray.cloud", content = "true")
 @SystemProperty(value = "xray.clientId", content = CLIENT_ID)
 @SystemProperty(value = "xray.clientSecret", content = CLIENT_SECRET)
 @SystemProperty(value = "xray.reportFormat", content = "behave")
 @SystemProperty(value = "xray.reportFile", content = "behave.json")
 @SystemProperty(value = "xray.testExecInfoJson", content = "testExecInfo.json")
 @SystemProperty(value = "xray.useInternalTestProxy", content = "true")
 @Requirement("XMP-136")
 void behave_multipart(MavenExecutionResult result) throws IOException {
    String testExecInfo = TestingUtils.readResourceFileForImportResults("XrayCloudIT/behave_multipart/testExecInfo.json");
    String report = TestingUtils.readResourceFileForImportResults("XrayCloudIT/behave_multipart/behave.json");

    wm.verify(
        postRequestedFor(urlPathEqualTo("/api/v2/import/execution/behave/multipart"))
            .withHeader("Authorization", equalTo("Bearer " + TOKEN))
            .withHeader("Content-Type", containing("multipart/form-data;"))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("results")
                    .withHeader("Content-Type", containing("application/json"))
                    .withBody(equalToJson(report)))
            .withAnyRequestBodyPart(
                aMultipart()
                    .withName("info")
                    .withHeader("Content-Type", containing("application/json"))
                    .withBody(equalToJson(testExecInfo))
                )

    );
    assertThat(result).isSuccessful();
}

}
