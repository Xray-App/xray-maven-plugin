package app.getxray.xray;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import app.getxray.xray.XrayResultsImporter.CloudBuilder;
import app.getxray.xray.XrayResultsImporter.ServerDCBuilder;;

class DummyTest {
    
    @Test
    void resultsImporterCloudBuilderTest() {
        CloudBuilder xrayImporterBuilder = new XrayResultsImporter.CloudBuilder("clientId", "clientSecret");
        assertNotNull(xrayImporterBuilder.build());
    }

    void resultsImporterDCBuilderTest() {
        ServerDCBuilder xrayImporterBuilder = new XrayResultsImporter.ServerDCBuilder("https://jira.example.com", "000000");
        assertNotNull(xrayImporterBuilder.build());
    }
}
