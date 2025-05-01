package app.getxray.xray.it;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestingUtils {

    public static String readFile(String path) throws IOException {
        String content = null;
        content = new String ( Files.readAllBytes( Paths.get(path) ) );
        return content;
    }
 
    public static byte[] readRawResourceFile(String path) throws IOException {
        return Files.readAllBytes( Paths.get("src/test/resources-its/app/getxray/xray/it/" + path));

    }

    private static String readResourceFile(String path) throws IOException {
        return readFile("src/test/resources-its/app/getxray/xray/it/" + path);
    }

    public static String readResourceFileForImportResults(String path) throws IOException {
        return readResourceFile("import_results/" + path);
    }

    public static String readResourceFileForImportFeatures(String path) throws IOException {
        return readResourceFile("import_features/" + path);
    }

    public static String readResourceFileForExportFeatures(String path) throws IOException {
        return readResourceFile("export_features/" + path);
    }
}
