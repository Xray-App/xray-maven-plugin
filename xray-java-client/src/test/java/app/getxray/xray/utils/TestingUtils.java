package app.getxray.xray.utils;

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
        return Files.readAllBytes( Paths.get("src/test/resources/" + path));

    }

}
