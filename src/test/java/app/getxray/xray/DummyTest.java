package app.getxray.xray;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import app.getxray.xray.XrayResultsImporter.CloudBuilder;
import app.getxray.xray.XrayResultsImporter.ServerDCBuilder;;

class DummyTest {
    
    @Test
    void resultsImporterCloudBuilderTest() {
        CloudBuilder xrayImporterBuilder = new XrayResultsImporter.CloudBuilder("clientId", "clientSecret")
            .withProjectKey("CALC")
            .withVersion("1.0")
            .withRevision("1234")
            .withTestPlanKey("CALC-124")
            .withTestExecKey("CALC-200")
            .withTestEnvironment("chrome")
            .withIgnoreSslErrors(true)
            .withVerbose(true);
        assertNotNull(xrayImporterBuilder.build());
    }

    void resultsImporterDCBuilderTest() {
        ServerDCBuilder xrayImporterBuilder = new XrayResultsImporter.ServerDCBuilder("https://jira.example.com", "000000")
            .withProjectKey("CALC")
            .withVersion("1.0")
            .withRevision("1234")
            .withTestPlanKey("CALC-124")
            .withTestExecKey("CALC-200")
            .withTestEnvironment("chrome")
            .withIgnoreSslErrors(true)
            .withVerbose(true);
        assertNotNull(xrayImporterBuilder.build());
    }

    @Test
    void exportFeaturesCloudBuilderTest() {
        XrayFeaturesExporter.CloudBuilder xrayBuilder = new XrayFeaturesExporter.CloudBuilder("clientId", "clientSecret");
        assertNotNull(xrayBuilder.build());
    }

    void exportFeaturesDCBuilderTest() {
        XrayFeaturesExporter.ServerDCBuilder xrayBuilder = new XrayFeaturesExporter.ServerDCBuilder("https://jira.example.com", "000000");
        assertNotNull(xrayBuilder.build());
    }

    @Test
    void importFeaturesCloudBuilderTest() {
        XrayFeaturesImporter.CloudBuilder xrayBuilder = new XrayFeaturesImporter.CloudBuilder("clientId", "clientSecret");
        assertNotNull(xrayBuilder.build());
    }

    void importFeaturesDCBuilderTest() {
        XrayFeaturesImporter.ServerDCBuilder xrayBuilder = new XrayFeaturesImporter.ServerDCBuilder("https://jira.example.com", "000000");
        assertNotNull(xrayBuilder.build());
    }
}
