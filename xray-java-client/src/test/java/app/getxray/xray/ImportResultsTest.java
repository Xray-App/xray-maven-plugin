package app.getxray.xray;

import app.getxray.xray.XrayResultsImporter.CloudBuilder;
import app.getxray.xray.XrayResultsImporter.ServerDCBuilder;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ImportResultsTest {
    
    @Test
    void resultsImporterCloudBuilderTest() {
        CloudBuilder xrayImporterBuilder = new XrayResultsImporter.CloudBuilder("clientId", "clientSecret", "https://xray.cloud.getxray.app/api/v2")
            .withProjectKey("CALC")
            .withVersion("1.0")
            .withRevision("1234")
            .withTestPlanKey("CALC-124")
            .withTestExecKey("CALC-200")
            .withTestEnvironment("chrome")
            .withIgnoreSslErrors(true)
            .withVerbose(true)
            .withTimeout(5);
        Assertions.assertNotNull(xrayImporterBuilder.build());
    }

    @Test
    void resultsImporterDCBuilderWithPersonalAccessTokenTest() {
        ServerDCBuilder xrayImporterBuilder = new XrayResultsImporter.ServerDCBuilder("https://jira.example.com", "000000")
            .withProjectKey("CALC")
            .withVersion("1.0")
            .withRevision("1234")
            .withTestPlanKey("CALC-124")
            .withTestExecKey("CALC-200")
            .withTestEnvironment("chrome")
            .withIgnoreSslErrors(true)
            .withVerbose(true)
            .withTimeout(5);
        Assertions.assertNotNull(xrayImporterBuilder.build());
    }

    @Test
    void resultsImporterDCBuilderWithUsernameAndPasswordTest() {
        ServerDCBuilder xrayImporterBuilder = new XrayResultsImporter.ServerDCBuilder("https://jira.example.com", "jiraUsername", "jiraPassword")
            .withProjectKey("CALC")
            .withVersion("1.0")
            .withRevision("1234")
            .withTestPlanKey("CALC-124")
            .withTestExecKey("CALC-200")
            .withTestEnvironment("chrome")
            .withIgnoreSslErrors(true)
            .withVerbose(true)
            .withTimeout(5);
        Assertions.assertNotNull(xrayImporterBuilder.build());
    }

    @Test
    void generateDCAuthorizationHeaderContentTest() {
        ServerDCBuilder xrayImporterBuilder = new XrayResultsImporter.ServerDCBuilder("https://jira.example.com", "jiraUsername", "jiraPassword");

        Assertions.assertEquals("Basic amlyYVVzZXJuYW1lOmppcmFQYXNzd29yZA==", xrayImporterBuilder.build().generateDCAuthorizationHeaderContent());
    }

    @Test
    void importResultsExceptionTest() {
        XrayResultsImporterException exception = new XrayResultsImporterException("message");
        Assertions.assertEquals("message", exception.getMessage());
    }

    // ServerDCBuilder(jiraBaseUrl, jiraUsername, jiraPassword) null validation tests

    @Test
    void resultsImporterDCBuilderWithUsernameAndPasswordNullJiraBaseUrlTest() {
        assertThrows(IllegalArgumentException.class, () ->
            new XrayResultsImporter.ServerDCBuilder(null, "jiraUsername", "jiraPassword"));
    }

    @Test
    void resultsImporterDCBuilderWithUsernameAndPasswordNullJiraUsernameTest() {
        assertThrows(IllegalArgumentException.class, () ->
            new XrayResultsImporter.ServerDCBuilder("https://jira.example.com", null, "jiraPassword"));
    }

    @Test
    void resultsImporterDCBuilderWithUsernameAndPasswordNullJiraPasswordTest() {
        assertThrows(IllegalArgumentException.class, () ->
            new XrayResultsImporter.ServerDCBuilder("https://jira.example.com", "jiraUsername", null));
    }

    // ServerDCBuilder(jiraBaseUrl, jiraPersonalAccessToken) null validation tests

    @Test
    void resultsImporterDCBuilderWithPersonalAccessTokenNullJiraBaseUrlTest() {
        assertThrows(IllegalArgumentException.class, () ->
            new XrayResultsImporter.ServerDCBuilder(null, "000000"));
    }

    @Test
    void resultsImporterDCBuilderWithPersonalAccessTokenNullTokenTest() {
        assertThrows(IllegalArgumentException.class, () ->
            new XrayResultsImporter.ServerDCBuilder("https://jira.example.com", (String) null));
    }

    // CloudBuilder(clientId, clientSecret, cloudApiBaseUrl) null validation tests

    @Test
    void resultsImporterCloudBuilderNullClientIdTest() {
        assertThrows(IllegalArgumentException.class, () ->
            new XrayResultsImporter.CloudBuilder(null, "clientSecret", "https://xray.cloud.getxray.app/api/v2"));
    }

    @Test
    void resultsImporterCloudBuilderNullClientSecretTest() {
        assertThrows(IllegalArgumentException.class, () ->
            new XrayResultsImporter.CloudBuilder("clientId", null, "https://xray.cloud.getxray.app/api/v2"));
    }

    @Test
    void resultsImporterCloudBuilderNullCloudApiBaseUrlTest() {
        assertThrows(IllegalArgumentException.class, () ->
            new XrayResultsImporter.CloudBuilder("clientId", "clientSecret", null));
    }

}
