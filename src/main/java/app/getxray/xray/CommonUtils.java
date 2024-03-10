package app.getxray.xray;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import org.apache.maven.plugin.logging.Log;

public class CommonUtils {

    public static void logRequest(Log logger, Request request) {
        if (logger != null) {
            logger.debug("REQUEST_URL: " + request.url().toString());
            logger.debug("REQUEST_METHOD: " + request.method());

            Request copy = request.newBuilder().build();
            RequestBody body = copy.body();
            if (body != null) {
                logger.debug("REQUEST_CONTENT_TYPE: " + body.contentType().toString());
            }
        }
    }

    public static void logResponse(Log logger, Response response) {
        logResponse(logger, response, true);
    }

    public static void logResponse(Log logger, Response response, boolean logBody) {
        if (logger != null) {
            logger.debug("RESPONSE_CONTENT_TYPE:" + response.header("Content-Type"));
            logger.debug("RESPONSE_HTTP_STATUS: " + response.code());
            if (logBody) {
                logger.debug("RESPONSE_BODY:");
                logger.debug("=======================");
                
                try (ResponseBody responseBody = response.peekBody(1024L * 1024L)) {
                    logger.debug(responseBody.string());
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
        }
    }

    public static boolean isTrue(Boolean bool) {
        return (bool!=null && bool);
    }

    public static OkHttpClient getHttpClient(Boolean useInternalTestProxy, Boolean ignoreSslErrors, Integer timeout) throws Exception {
        OkHttpClient client;
        OkHttpClient.Builder newBuilder = new OkHttpClient.Builder();

        if (isTrue(ignoreSslErrors) || isTrue(useInternalTestProxy)) {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }
            
                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }
            
                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
            };
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            newBuilder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0]);
            newBuilder.hostnameVerifier((host, session) -> true);
        }

        if (isTrue(useInternalTestProxy)) {
            String hostname = "localhost"/*127.0.0.1*/;
            int port = 18080;
            Proxy proxy = new Proxy(Proxy.Type.HTTP,
            new InetSocketAddress(hostname, port));
            client = newBuilder
                        .connectTimeout(timeout, TimeUnit.SECONDS)
                        .readTimeout(timeout, TimeUnit.SECONDS)
                        .writeTimeout(timeout, TimeUnit.SECONDS)
                        .callTimeout(timeout, TimeUnit.SECONDS)
                        .proxy(proxy)
                        .build();
        } else {
            client = newBuilder
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .callTimeout(timeout, TimeUnit.SECONDS)
                .build();
        }
        return client;
    }

}
