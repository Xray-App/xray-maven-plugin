package app.getxray.xray;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

class ImportFeaturesTest {
    
    @Test
    void importFeaturesCloudBuilderTest() {
        XrayFeaturesImporter.CloudBuilder xrayBuilder = new XrayFeaturesImporter.CloudBuilder("clientId", "clientSecret")
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

}
