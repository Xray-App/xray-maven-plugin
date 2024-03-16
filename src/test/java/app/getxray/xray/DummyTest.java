package app.getxray.xray;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import app.getxray.xray.XrayResultsImporter.CloudBuilder;

public class DummyTest {
    
    @Test
    public void resultsImporterBuilderTest() {
        CloudBuilder xrayImporterBuilder = new XrayResultsImporter.CloudBuilder("clientId", "clientSecret");
        assertNotNull(xrayImporterBuilder.build());
    }
}
