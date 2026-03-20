package app.getxray.xray;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class ExportFeaturesTest {
    
    @Test
    void exportFeaturesCloudBuilderTest() {
        XrayFeaturesExporter.CloudBuilder xrayBuilder = new XrayFeaturesExporter.CloudBuilder("clientId", "clientSecret", "https://xray.cloud.getxray.app/api/v2")
            .withIssueKeys("CALC-1")
            .withFilterId("1234")
            .withIgnoreSslErrors(true)
            .withVerbose(true)
            .withTimeout(5);
        assertNotNull(xrayBuilder.build());
    }

    @Test
    void exportFeaturesDCBuilderWithPersonalAccessTokenTest() {
        XrayFeaturesExporter.ServerDCBuilder xrayBuilder = new XrayFeaturesExporter.ServerDCBuilder("https://jira.example.com", "000000")
            .withIssueKeys("CALC-1")
            .withFilterId("1234")
            .withIgnoreSslErrors(true)
            .withVerbose(true)
            .withTimeout(5);
        assertNotNull(xrayBuilder.build());
    }

    @Test
    void exportFeaturesDCBuilderWithUsernameAndPasswordTest() {
        XrayFeaturesExporter.ServerDCBuilder xrayBuilder = new XrayFeaturesExporter.ServerDCBuilder("https://jira.example.com", "jiraUsername", "jiraPassword")
            .withIssueKeys("CALC-1")
            .withFilterId("1234")
            .withIgnoreSslErrors(true)
            .withVerbose(true)
            .withTimeout(5);
        assertNotNull(xrayBuilder.build());
    }

    @Test
    void exportFeaturesExceptionTest() {
        XrayFeaturesExporterException exception = new XrayFeaturesExporterException("message");
        assertEquals("message", exception.getMessage());
    }

    // ServerDCBuilder(jiraBaseUrl, jiraUsername, jiraPassword) null validation tests

    @Test
    void exportFeaturesDCBuilderWithUsernameAndPasswordNullJiraBaseUrlTest() {
        assertThrows(IllegalArgumentException.class, () ->
            new XrayFeaturesExporter.ServerDCBuilder(null, "jiraUsername", "jiraPassword"));
    }

    @Test
    void exportFeaturesDCBuilderWithUsernameAndPasswordNullJiraUsernameTest() {
        assertThrows(IllegalArgumentException.class, () ->
            new XrayFeaturesExporter.ServerDCBuilder("https://jira.example.com", null, "jiraPassword"));
    }

    @Test
    void exportFeaturesDCBuilderWithUsernameAndPasswordNullJiraPasswordTest() {
        assertThrows(IllegalArgumentException.class, () ->
            new XrayFeaturesExporter.ServerDCBuilder("https://jira.example.com", "jiraUsername", null));
    }

    // ServerDCBuilder(jiraBaseUrl, jiraPersonalAccessToken) null validation tests

    @Test
    void exportFeaturesDCBuilderWithPersonalAccessTokenNullJiraBaseUrlTest() {
        assertThrows(IllegalArgumentException.class, () ->
            new XrayFeaturesExporter.ServerDCBuilder(null, "000000"));
    }

    @Test
    void exportFeaturesDCBuilderWithPersonalAccessTokenNullTokenTest() {
        assertThrows(IllegalArgumentException.class, () ->
            new XrayFeaturesExporter.ServerDCBuilder("https://jira.example.com", (String) null));
    }

    // CloudBuilder(clientId, clientSecret, cloudApiBaseUrl) null validation tests

    @Test
    void exportFeaturesCloudBuilderNullClientIdTest() {
        assertThrows(IllegalArgumentException.class, () ->
            new XrayFeaturesExporter.CloudBuilder(null, "clientSecret", "https://xray.cloud.getxray.app/api/v2"));
    }

    @Test
    void exportFeaturesCloudBuilderNullClientSecretTest() {
        assertThrows(IllegalArgumentException.class, () ->
            new XrayFeaturesExporter.CloudBuilder("clientId", null, "https://xray.cloud.getxray.app/api/v2"));
    }

    @Test
    void exportFeaturesCloudBuilderNullCloudApiBaseUrlTest() {
        assertThrows(IllegalArgumentException.class, () ->
            new XrayFeaturesExporter.CloudBuilder("clientId", "clientSecret", null));
    }

}
