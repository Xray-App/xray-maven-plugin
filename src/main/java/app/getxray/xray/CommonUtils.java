package app.getxray.xray;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
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

    private CommonUtils() {
        throw new IllegalStateException("Utility class");
      }

    public static void logRequest(Log logger, Request request, Boolean logRequest) {
        if ((logger != null) && (isTrue(logRequest))) {
            logger.debug("REQUEST_URL: " + request.url().toString());
            logger.debug("REQUEST_METHOD: " + request.method());

            Request copy = request.newBuilder().build();
            RequestBody body = copy.body();
            if (body != null) {
                logger.debug("REQUEST_CONTENT_TYPE: " + body.contentType().toString());
            }
        }
    }

    public static void logResponse(Log logger, Response response, boolean logResponse) {
        if (logger != null) {
            logger.debug("RESPONSE_CONTENT_TYPE:" + response.header("Content-Type"));
            logger.debug("RESPONSE_HTTP_STATUS: " + response.code());
            if (logResponse) {
                logger.debug("RESPONSE_BODY:");
                logger.debug("=======================");
                
                try (ResponseBody responseBody = response.peekBody(1024L * 1024L)) {
                    logger.debug(responseBody.string());
                } catch (IOException e) {
                    logger.error(e);
                }
            }
        }
    }

    public static boolean isTrue(Boolean bool) {
        return Boolean.TRUE.equals(bool);
    }

    public static OkHttpClient createHttpClient(Boolean useInternalTestProxy, Boolean ignoreSslErrors, Integer timeout) throws KeyManagementException,  NoSuchAlgorithmException {
        OkHttpClient client;
        OkHttpClient.Builder newBuilder = new OkHttpClient.Builder();

        if (isTrue(ignoreSslErrors) || isTrue(useInternalTestProxy)) {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        // we want to ignore the server certificate on purpose, as self-signed certificates are used by some users
                    }
            
                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        // we want to ignore the server certificate on purpose, as self-signed certificates are used by some users
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
