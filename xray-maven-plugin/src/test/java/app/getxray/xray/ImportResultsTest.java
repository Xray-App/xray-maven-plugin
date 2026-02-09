package app.getxray.xray;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import app.getxray.xray.XrayResultsImporter.CloudBuilder;
import app.getxray.xray.XrayResultsImporter.ServerDCBuilder;

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
        assertNotNull(xrayImporterBuilder.build());
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
        assertNotNull(xrayImporterBuilder.build());
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
        assertNotNull(xrayImporterBuilder.build());
    }

    @Test
    void generateDCAuthorizationHeaderContentTest() {
        ServerDCBuilder xrayImporterBuilder = new XrayResultsImporter.ServerDCBuilder("https://jira.example.com", "jiraUsername", "jiraPassword");

        assertEquals("Basic amlyYVVzZXJuYW1lOmppcmFQYXNzd29yZA==", xrayImporterBuilder.build().generateDCAuthorizationHeaderContent());
    }

    @Test
    void importResultsExceptionTest() {
        XrayResultsImporterException exception = new XrayResultsImporterException("message");
        assertEquals("message", exception.getMessage());
    }   

}
