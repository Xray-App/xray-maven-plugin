package app.getxray.xray;

import static app.getxray.xray.CommonUtils.isTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.Test;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.mockito.Mockito;

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
    void getHttpClientTest() throws KeyManagementException, NoSuchAlgorithmException {
        OkHttpClient client = CommonUtils.getHttpClient(false, true, 5);
        assertNotNull(client);
    }

    @Test
    void getHttpClientUsingInternalProxyTest() throws KeyManagementException, NoSuchAlgorithmException {
        OkHttpClient client = CommonUtils.getHttpClient(true, true, 5);
        assertNotNull(client);
    }

    @Test
    void testLogRequest() {
        Log log = Mockito.mock(Log.class);
        Request request = new Request.Builder()
            .url("http://example.com")
            .method("PUT", RequestBody.create("{}", MediaType.get("application/json")))
            .build();

        CommonUtils.logRequest(log, request, true);
        assertAll("logRequest",
            () -> Mockito.verify(log).debug("REQUEST_URL: " + request.url().toString()),
            () -> Mockito.verify(log).debug("REQUEST_METHOD: " + request.method()),
            () -> Mockito.verify(log).debug("REQUEST_CONTENT_TYPE: " + request.body().contentType().toString()),
            () -> Mockito.verifyNoMoreInteractions(log)
        );
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
