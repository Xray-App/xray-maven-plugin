package app.getxray.xray;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.maven.plugin.logging.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

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

    public static OkHttpClient createHttpClient(Boolean useInternalTestProxy, Boolean ignoreSslErrors, Integer timeout) throws IOException {
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
            SSLContext sslContext;
            try {
                sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                newBuilder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0]);
                newBuilder.hostnameVerifier((host, session) -> true);
            } catch (KeyManagementException | NoSuchAlgorithmException e) {
                throw new IOException("problem setting up SSL http client configuration");
            }
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

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    public static void unzipContentsToFolder(InputStream zippedContents, String outputFolder) throws IOException {
        File destDir = new File(outputFolder);
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(zippedContents));
        ZipEntry zipEntry;
        while ((zipEntry = zis.getNextEntry()) != null) {
            File newFile = newFile(destDir, zipEntry);

            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                // fix for Windows-created archives
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }

                try (
                FileOutputStream fos = new FileOutputStream(newFile)) {
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
            }
        }
        zis.closeEntry();
        zis.close();
    }

}
