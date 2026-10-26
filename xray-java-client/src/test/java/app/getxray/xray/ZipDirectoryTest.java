package app.getxray.xray;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

// only .feature files and subdirectories are zipped, with paths relative to the source directory
class ZipDirectoryTest {

    private static final List<String> EXPECTED_ENTRIES = List.of("a.feature", "sub/", "sub/b.feature");

    @TempDir
    File tempDir;

    File featuresDir;

    @BeforeEach
    void setUp() throws IOException {
        featuresDir = new File(tempDir, "features");
        File subDir = new File(featuresDir, "sub");
        Files.createDirectories(subDir.toPath());
        Files.writeString(new File(featuresDir, "a.feature").toPath(), "Feature: a");
        Files.writeString(new File(featuresDir, "notes.txt").toPath(), "ignored");
        Files.writeString(new File(subDir, "b.feature").toPath(), "Feature: b");
    }

    @Test
    void zipDirectoryToStreamTest() throws IOException {
        ByteArrayOutputStream zipContent = new ByteArrayOutputStream();
        XrayFeaturesImporter.zipDirectory(featuresDir.getAbsolutePath(), zipContent);

        assertEquals(EXPECTED_ENTRIES, entryNames(new ByteArrayInputStream(zipContent.toByteArray())));
    }

    @Test
    void zipDirectoryToFileTest() throws IOException {
        File zipFile = new File(tempDir, "features.zip");
        XrayFeaturesImporter.zipDirectory(featuresDir.getAbsolutePath(), zipFile.getAbsolutePath());

        try (InputStream input = Files.newInputStream(zipFile.toPath())) {
            assertEquals(EXPECTED_ENTRIES, entryNames(input));
        }
    }

    private static List<String> entryNames(InputStream input) throws IOException {
        List<String> names = new ArrayList<>();
        try (ZipInputStream zipIn = new ZipInputStream(input)) {
            for (ZipEntry entry = zipIn.getNextEntry(); entry != null; entry = zipIn.getNextEntry()) {
                names.add(entry.getName());
            }
        }
        names.sort(null);
        return names;
    }
}
