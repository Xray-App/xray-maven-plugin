package app.getxray.xray;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class ImportFeaturesTest {
    
    @Test
    void importFeaturesCloudBuilderTest() {
        XrayFeaturesImporter.CloudBuilder xrayBuilder = new XrayFeaturesImporter.CloudBuilder("clientId", "clientSecret", "https://xray.cloud.getxray.app/api/v2")
            .withProjectKey("CALC")
            .withSource("dummySource")
            .withIgnoreSslErrors(true)
            .withVerbose(true)
            .withTimeout(5);
        assertNotNull(xrayBuilder.build());
    }

    @Test
    void importFeaturesDCBuilderTest() {
        XrayFeaturesImporter.ServerDCBuilder xrayBuilder = new XrayFeaturesImporter.ServerDCBuilder("https://jira.example.com", "jiraUsername", "jiraPassword")
            .withProjectKey("CALC")
            .withupdateRepository(Boolean.FALSE)
            .withIgnoreSslErrors(true)
            .withVerbose(true)
            .withTimeout(5);
        assertNotNull(xrayBuilder.build());
    }

    @Test
    void importFeaturesDCBuilderWithPersonalAccessTokenTest() {
        XrayFeaturesImporter.ServerDCBuilder xrayBuilder = new XrayFeaturesImporter.ServerDCBuilder("https://jira.example.com", "000000")
            .withProjectKey("CALC")
            .withupdateRepository(Boolean.FALSE)
            .withIgnoreSslErrors(true)
            .withVerbose(true)
            .withTimeout(5);
        assertNotNull(xrayBuilder.build());
    }

    @Test
    void importFeaturesExceptionTest() {
        XrayFeaturesImporterException exception = new XrayFeaturesImporterException("message");
        assertEquals("message", exception.getMessage());
    }

    // ServerDCBuilder(jiraBaseUrl, jiraUsername, jiraPassword) null validation tests

    @Test
    void importFeaturesDCBuilderWithUsernameAndPasswordNullJiraBaseUrlTest() {
        assertThrows(IllegalArgumentException.class, () ->
            new XrayFeaturesImporter.ServerDCBuilder(null, "jiraUsername", "jiraPassword"));
    }

    @Test
    void importFeaturesDCBuilderWithUsernameAndPasswordNullJiraUsernameTest() {
        assertThrows(IllegalArgumentException.class, () ->
            new XrayFeaturesImporter.ServerDCBuilder("https://jira.example.com", null, "jiraPassword"));
    }

    @Test
    void importFeaturesDCBuilderWithUsernameAndPasswordNullJiraPasswordTest() {
        assertThrows(IllegalArgumentException.class, () ->
            new XrayFeaturesImporter.ServerDCBuilder("https://jira.example.com", "jiraUsername", null));
    }

    // ServerDCBuilder(jiraBaseUrl, jiraPersonalAccessToken) null validation tests

    @Test
    void importFeaturesDCBuilderWithPersonalAccessTokenNullJiraBaseUrlTest() {
        assertThrows(IllegalArgumentException.class, () ->
            new XrayFeaturesImporter.ServerDCBuilder(null, "000000"));
    }

    @Test
    void importFeaturesDCBuilderWithPersonalAccessTokenNullTokenTest() {
        assertThrows(IllegalArgumentException.class, () ->
            new XrayFeaturesImporter.ServerDCBuilder("https://jira.example.com", (String) null));
    }

    // CloudBuilder(clientId, clientSecret, cloudApiBaseUrl) null validation tests

    @Test
    void importFeaturesCloudBuilderNullClientIdTest() {
        assertThrows(IllegalArgumentException.class, () ->
            new XrayFeaturesImporter.CloudBuilder(null, "clientSecret", "https://xray.cloud.getxray.app/api/v2"));
    }

    @Test
    void importFeaturesCloudBuilderNullClientSecretTest() {
        assertThrows(IllegalArgumentException.class, () ->
            new XrayFeaturesImporter.CloudBuilder("clientId", null, "https://xray.cloud.getxray.app/api/v2"));
    }

    @Test
    void importFeaturesCloudBuilderNullCloudApiBaseUrlTest() {
        assertThrows(IllegalArgumentException.class, () ->
            new XrayFeaturesImporter.CloudBuilder("clientId", "clientSecret", null));
    }

}
