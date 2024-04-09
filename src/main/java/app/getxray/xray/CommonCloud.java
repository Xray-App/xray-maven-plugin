package app.getxray.xray;

import java.io.IOException;

import org.apache.maven.plugin.logging.Log;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommonCloud {
   
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");
    static final String XRAY_CLOUD_API_BASE_URL = "https://xray.cloud.getxray.app/api/v2";
	static final String XRAY_CLOUD_AUTHENTICATE_URL = XRAY_CLOUD_API_BASE_URL + "/authenticate";


    private CommonCloud() {
        throw new IllegalStateException("Utility class");
    }


    public static String authenticateXrayAPIKeyCredentials(Log logger,boolean verbose, OkHttpClient client, String clientId, String clientSecret) throws IOException {
        String authenticationPayload = "{ \"client_id\": \"" + clientId +"\", \"client_secret\": \"" + clientSecret +"\" }";
        RequestBody body = RequestBody.create(authenticationPayload, MEDIA_TYPE_JSON);
        Request request = new Request.Builder().url(XRAY_CLOUD_AUTHENTICATE_URL).post(body).build();
        CommonUtils.logRequest(logger, request, verbose);
        try (Response response = client.newCall(request).execute()) {
            CommonUtils.logResponse(logger, response, false);
            String responseBody = response.body().string();
            if (response.isSuccessful()) {
                return responseBody.replace("\"", "");
            } else {
                throw new IOException("failed to authenticate " + response);
            }
        }
    }
}
