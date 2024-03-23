package app.getxray.xray.it;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TestingUtils {

    private static OkHttpClient mockHttpClient(final String serializedBody) throws IOException {
        final OkHttpClient okHttpClient = mock(OkHttpClient.class);

        final Call remoteCall = mock(Call.class);

        final Response response = new Response.Builder()
                .request(new Request.Builder().url("https://xray.cloud.getxray.app/api/v2/authenticate").build())
                .protocol(Protocol.HTTP_1_1)
                .code(200).message("").body(
                    ResponseBody.create(
                        serializedBody,
                        MediaType.parse("application/json")
                ))
                .build();

        when(remoteCall.execute()).thenReturn(response);
        when(okHttpClient.newCall(any())).thenReturn(remoteCall);

        return okHttpClient;
    }

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
