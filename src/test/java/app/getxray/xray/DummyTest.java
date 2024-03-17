package app.getxray.xray;

import static app.getxray.xray.CommonUtils.isTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void importFeaturesCloudBuilderTest() {
        XrayFeaturesImporter.CloudBuilder xrayBuilder = new XrayFeaturesImporter.CloudBuilder("clientId", "clientSecret")
            .withProjectKey("CALC")
            .withIgnoreSslErrors(true)
            .withVerbose(true)
            .withTimeout(5);
        assertNotNull(xrayBuilder.build());
    }

    @Test
    void importFeaturesDCBuilderTest() {
        XrayFeaturesImporter.ServerDCBuilder xrayBuilder = new XrayFeaturesImporter.ServerDCBuilder("https://jira.example.com", "jiraUsername", "jiraPassword")
            .withProjectKey("CALC")
            .withIgnoreSslErrors(true)
            .withVerbose(true)
            .withTimeout(5);
        assertNotNull(xrayBuilder.build());
    }

    @Test
    void importFeaturesDCBuilderWithPersonalAccessTokenTest() {
        XrayFeaturesImporter.ServerDCBuilder xrayBuilder = new XrayFeaturesImporter.ServerDCBuilder("https://jira.example.com", "000000")
            .withProjectKey("CALC")
            .withIgnoreSslErrors(true)
            .withVerbose(true)
            .withTimeout(5);
        assertNotNull(xrayBuilder.build());
    }

    @Test
    void isTrueTruethnessTest() {
        assertAll("conditions for truethness", 
            () -> assertTrue(isTrue(Boolean.TRUE)),
            () -> assertTrue(isTrue(true))
        );
    }

    @Test
    void isTrueFalseTest() {
        assertAll("conditions for false", 
            () -> assertFalse(isTrue(Boolean.FALSE)),
            () -> assertFalse(isTrue(false)),
            () -> assertFalse(isTrue(null))
        );
    }



}
