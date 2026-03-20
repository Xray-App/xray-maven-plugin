package app.getxray.xray.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestingUtils {

    public static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
 
    public static byte[] readRawResourceFile(String path) throws IOException {
        return Files.readAllBytes( Paths.get("src/test/resources/" + path));

    }

}
