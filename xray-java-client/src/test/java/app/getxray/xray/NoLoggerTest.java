package app.getxray.xray;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

// clients built without a logger must report request failures with their own exception, not a NullPointerException
class NoLoggerTest {

    private static final String UNREACHABLE_JIRA_BASE_URL = "http://localhost:1";

    @TempDir
    File tempDir;

    @Test
    void resultsImporterWithoutLoggerTest() throws IOException {
        File reportFile = Files.writeString(new File(tempDir, "junit.xml").toPath(), "<testsuite/>").toFile();
        XrayResultsImporter importer = new XrayResultsImporter.ServerDCBuilder(UNREACHABLE_JIRA_BASE_URL, "token")
            .withTimeout(5)
            .build();

        assertThrows(XrayResultsImporterException.class, () ->
            importer.submit(XrayResultsImporter.JUNIT_FORMAT, reportFile.getAbsolutePath()));
    }

    @Test
    void featuresImporterWithoutLoggerTest() throws IOException {
        File featureFile = Files.writeString(new File(tempDir, "dummy.feature").toPath(), "Feature: dummy").toFile();
        XrayFeaturesImporter importer = new XrayFeaturesImporter.ServerDCBuilder(UNREACHABLE_JIRA_BASE_URL, "token")
            .withTimeout(5)
            .build();

        assertThrows(XrayFeaturesImporterException.class, () ->
            importer.importFrom(featureFile.getAbsolutePath()));
    }

    @Test
    void featuresExporterWithoutLoggerTest() {
        XrayFeaturesExporter exporter = new XrayFeaturesExporter.ServerDCBuilder(UNREACHABLE_JIRA_BASE_URL, "token")
            .withTimeout(5)
            .withLogger(null)
            .build();

        assertThrows(XrayFeaturesExporterException.class, () ->
            exporter.submit(tempDir.getAbsolutePath()));
    }
}
