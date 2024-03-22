package app.getxray.xray;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Response.Builder;
import okio.Buffer;

class CommonCloudTest {
    
    @Test
    void authenticateXrayAPIKeyCredentialsTest() throws Exception{
        String clientId = "22334455";
        String clientSecret = "998877665544";
        String authenticationPayload = "{ \"client_id\": \"" + clientId +"\", \"client_secret\": \"" + clientSecret +"\" }";
        String expectedToken = "00000000000000000000000000111111111111111111111111222222222222222222222223333333333333";

        Log log = Mockito.mock(Log.class);
        OkHttpClient client = Mockito.mock(OkHttpClient.class);
        Builder responseBuilder = new Response.Builder();
        responseBuilder.protocol(okhttp3.Protocol.HTTP_1_1);
        responseBuilder.request(new Request.Builder().url("https://xray.cloud.getxray.app/api/v2/authenticate").build());
        responseBuilder.code(200);
        responseBuilder.message("OK");
        responseBuilder.header("Content-Type", "application/json");
        responseBuilder.body(okhttp3.ResponseBody.create("\""+expectedToken+"\"", okhttp3.MediaType.parse("application/json"))); 
        Response response = responseBuilder.build();

        final Call remoteCall = Mockito.mock(Call.class);
        when(remoteCall.execute()).thenReturn(response);
        when(client.newCall(any())).thenReturn(remoteCall);
        
        // OkHttpClient client2 = mockHttpClient("\""+expectedToken+"\"");
        String authToken = CommonCloud.authenticateXrayAPIKeyCredentials(log, true, client, clientId, clientSecret);
        assertEquals(expectedToken, authToken);

        ArgumentCaptor<Request> req = ArgumentCaptor.forClass(Request.class);
        verify(client).newCall(req.capture());
        assertEquals("https://xray.cloud.getxray.app/api/v2/authenticate", req.getValue().url().toString());
        assertEquals("POST", req.getValue().method());
        assertEquals("application/json; charset=utf-8", req.getValue().body().contentType().toString());
        final Buffer buffer = new Buffer();
        req.getValue().body().writeTo(buffer);
        assertEquals(authenticationPayload,  buffer.readUtf8());
    }
            
}
