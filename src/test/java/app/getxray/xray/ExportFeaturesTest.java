package app.getxray.xray;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

class ExportFeaturesTest {
    
    @Test
    void exportFeaturesCloudBuilderTest() {
        XrayFeaturesExporter.CloudBuilder xrayBuilder = new XrayFeaturesExporter.CloudBuilder("clientId", "clientSecret")
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

}
