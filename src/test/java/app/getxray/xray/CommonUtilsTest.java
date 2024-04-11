package app.getxray.xray;

import static app.getxray.xray.CommonUtils.createHttpClient;
import static app.getxray.xray.CommonUtils.isTrue;
import static app.getxray.xray.CommonUtils.unzipContentsToFolder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import app.getxray.xray.it.TestingUtils;
import junit.framework.Assert;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

class CommonUtilsTest {
 
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

    @Test
    void getHttpClientTest() throws IOException {
        OkHttpClient client = createHttpClient(Boolean.FALSE, Boolean.TRUE, Integer.valueOf(5));
        assertNotNull(client);
    }

    @Test
    void getHttpClientUsingInternalProxyTest() throws IOException {
        OkHttpClient client = createHttpClient(Boolean.TRUE, Boolean.TRUE, Integer.valueOf(5));
        assertNotNull(client);
    }

    @Test
    void testLogRequest() {
        Log log = Mockito.mock(Log.class);
        Request request = new Request.Builder()
            .url("http://example.com")
            .method("PUT", RequestBody.create("{}", MediaType.get("application/json")))
            .build();

        CommonUtils.logRequest(log, request, Boolean.TRUE);
        assertAll("logRequest",
            () -> Mockito.verify(log).debug("REQUEST_URL: " + request.url().toString()),
            () -> Mockito.verify(log).debug("REQUEST_METHOD: " + request.method()),
            () -> Mockito.verify(log).debug("REQUEST_CONTENT_TYPE: " + request.body().contentType().toString()),
            () -> Mockito.verifyNoMoreInteractions(log)
        );
    }

    @Test
    void testUnzipContentsToFolder() throws Exception {
        File tempDir = Files.createTempDirectory("dummy").toFile();
        byte[] zippedContent = TestingUtils.readRawResourceFile("import_features/XrayDatacenterIT/multiple_features/features.zip");
        InputStream zippedContentStream = new ByteArrayInputStream(zippedContent);
        unzipContentsToFolder(zippedContentStream, tempDir.getAbsolutePath().toString());
        assertThat(tempDir.listFiles()).hasSize(2);
        assertThat(tempDir.listFiles()).extracting(File::getName).containsExactlyInAnyOrder("core", "other");
        assertThat((new File(tempDir, "core")).listFiles()).extracting(File::getName).containsExactlyInAnyOrder("positive_sum.feature");
        assertThat((new File(tempDir, "other")).listFiles()).extracting(File::getName).containsExactlyInAnyOrder("negative_sum.feature");
    }

    @Test
    void testLogResponse() {
        Log log = Mockito.mock(Log.class);
        Response response = new Response.Builder()
            .code(200)
            .message("OK")
            .header("Content-Type", "text/plain")
            .body(ResponseBody.create("dummy text", null))
            .protocol(okhttp3.Protocol.HTTP_1_1)
            .request(new Request.Builder().url("http://example.com").build())
            .build();

        CommonUtils.logResponse(log, response, true);
        assertAll("logResponse",
            () -> Mockito.verify(log).debug("RESPONSE_CONTENT_TYPE:" + response.header("Content-Type")),
            () -> Mockito.verify(log).debug("RESPONSE_HTTP_STATUS: " + response.code()),
            () -> Mockito.verify(log).debug("RESPONSE_BODY:"),
            () -> Mockito.verify(log).debug("======================="),
            () -> Mockito.verify(log).debug("dummy text"),
            () -> Mockito.verifyNoMoreInteractions(log)
        );
    }


}
