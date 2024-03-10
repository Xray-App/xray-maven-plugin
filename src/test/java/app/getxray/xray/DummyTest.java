package app.getxray.xray;

import org.junit.jupiter.api.Test;

import app.getxray.xray.XrayResultsImporter.CloudBuilder;

public class DummyTest {
    
    @Test
    public void resultsImporterBuilderTest() {
        CloudBuilder xrayImporterBuilder = new XrayResultsImporter.CloudBuilder("clientId", "clientSecret");
        xrayImporterBuilder.build();
    }
}
