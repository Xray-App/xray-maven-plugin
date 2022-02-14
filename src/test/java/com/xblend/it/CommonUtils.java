package com.xblend.it;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CommonUtils {

    public static boolean isTrue(Boolean bool) {
        return (bool!=null && bool);
    }
    
    public static String readFile(String path) throws IOException {
        String content = null;
        content = new String ( Files.readAllBytes( Paths.get(path) ) );
        return content;
    }
    
    public static String readResourceFile(String path) throws IOException {
        return readFile("src/test/resources-its/com/xblend/it/" + path);
    }  
}
