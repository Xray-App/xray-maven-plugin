package app.getxray.xray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.maven.plugin.logging.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.MultipartBody.Builder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// https://docs.getxray.app/display/XRAYCLOUD/Importing+Cucumber+Tests+-+REST+v2
// https://docs.getxray.app/display/XRAY/Importing+Cucumber+Tests+-+REST

public class XrayFeaturesImporter {
    private static final String FEATURE_EXTENSION = ".feature";
    private static final MediaType MEDIA_TYPE_ZIP = MediaType.parse("application/zip");
    private static final MediaType MEDIA_TYPE_FOR_FEATURE_FILES = MediaType.parse("text/plain");
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");

    private static final String XRAY_CLOUD_API_BASE_URL = "https://xray.cloud.getxray.app/api/v2";
	private static final String XRAY_CLOUD_AUTHENTICATE_URL = XRAY_CLOUD_API_BASE_URL + "/authenticate";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_HEADER_PREFIX = "Bearer ";

    private String jiraBaseUrl;
    private String jiraUsername;
    private String jiraPassword;
    private String jiraPersonalAccessToken;

    private String clientId;
    private String clientSecret;

    private String projectKey;
    private String projectId;
    private String source;
    private Boolean updateRepository = false;

    private Boolean ignoreSslErrors = false;
    private Boolean useInternalTestProxy = false;
    private Integer timeout = 50;
    private Boolean verbose = false;
    private Log logger;

    private XrayFeaturesImporter(ServerDCBuilder builder) {
        this.jiraBaseUrl = builder.jiraBaseUrl;
        this.jiraUsername = builder.jiraUsername;
        this.jiraPassword = builder.jiraPassword;
        this.jiraPersonalAccessToken = builder.jiraPersonalAccessToken;

        this.projectKey = builder.projectKey;
        this.projectId = builder.projectId;
        this.source = builder.source;
        this.updateRepository = builder.updateRepository;

        this.ignoreSslErrors = builder.ignoreSslErrors;
        this.useInternalTestProxy = builder.useInternalTestProxy;
        this.timeout = builder.timeout;
        this.verbose = builder.verbose;
        this.logger = builder.logger;
    }

    private XrayFeaturesImporter(CloudBuilder builder) {
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret;

        this.projectKey = builder.projectKey;
        this.projectId = builder.projectId;
        this.source = builder.source;

        this.ignoreSslErrors = builder.ignoreSslErrors;
        this.useInternalTestProxy = builder.useInternalTestProxy;
        this.timeout = builder.timeout;
        this.verbose = builder.verbose;
        this.logger = builder.logger;
    }

    public static class ServerDCBuilder {

        private final String jiraBaseUrl;
        private String jiraUsername;
        private String jiraPassword;
        private String jiraPersonalAccessToken;

        private String projectKey;
        private String projectId;   // unused
        private String source;      // unused
        private Boolean updateRepository = false;

        private Boolean ignoreSslErrors = false;
        private Boolean useInternalTestProxy = false;
        private Integer timeout = 50;
        private Boolean verbose = false;
        private Log logger;

        public ServerDCBuilder(String jiraBaseUrl, String jiraUsername, String jiraPassword) {
            this.jiraBaseUrl = jiraBaseUrl;
            this.jiraUsername = jiraUsername;
            this.jiraPassword = jiraPassword;
        }

        public ServerDCBuilder(String jiraBaseUrl, String jiraPersonalAccessToken) {
            this.jiraBaseUrl = jiraBaseUrl;
            this.jiraPersonalAccessToken = jiraPersonalAccessToken;
        }

        public ServerDCBuilder withIgnoreSslErrors(Boolean ignoreSslErrors) {
            this.ignoreSslErrors = ignoreSslErrors;
            return this;
        }

        public ServerDCBuilder withInternalTestProxy(Boolean useInternalTestProxy) {
            this.useInternalTestProxy = useInternalTestProxy;
            return this;
        }

        public ServerDCBuilder withTimeout(Integer timeout) {
            this.timeout = timeout;
            return this;
        }

        public ServerDCBuilder withVerbose(Boolean verbose) {
            this.verbose = verbose;
            return this;
        }

        public ServerDCBuilder withLogger(Log logger) {
            this.logger = logger;
            return this;
        }
    
        public ServerDCBuilder withProjectKey(String projectKey) {
            this.projectKey = projectKey;
            return this;
        }

        public ServerDCBuilder withupdateRepository(Boolean updateRepository) {
            this.updateRepository = updateRepository;
            return this;
        }

        public XrayFeaturesImporter build() {
            return new XrayFeaturesImporter(this);
        }

    }

    public static class CloudBuilder {

        private final String clientId;
        private final String clientSecret;

        private String projectKey;
        private String projectId;
        private String source;

        private Boolean ignoreSslErrors = false;
        private Boolean useInternalTestProxy = false;
        private Integer timeout = 50;
        private Boolean verbose = false;
        private Log logger;

        public CloudBuilder(String clientId, String clientSecret) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
        }

        public CloudBuilder withIgnoreSslErrors(Boolean ignoreSslErrors) {
            this.ignoreSslErrors = ignoreSslErrors;
            return this;
        }

        public CloudBuilder withInternalTestProxy(Boolean useInternalTestProxy) {
            this.useInternalTestProxy = useInternalTestProxy;
            return this;
        }

        public CloudBuilder withTimeout(Integer timeout) {
            this.timeout = timeout;
            return this;
        }

        public CloudBuilder withVerbose(Boolean verbose) {
            this.verbose = verbose;
            return this;
        }
    
        public CloudBuilder withLogger(Log logger) {
            this.logger = logger;
            return this;
        }
    
        public CloudBuilder withProjectKey(String projectKey) {
            this.projectKey = projectKey;
            return this;
        }

        public CloudBuilder withProjectId(String projectId) {
            this.projectId = projectId;
            return this;
        }

        public CloudBuilder withSource(String source) {
            this.source = source;
            return this;
        }

        public XrayFeaturesImporter build() {
            return new XrayFeaturesImporter(this);
        }

    }

    public JSONArray importFrom(String inputPath) throws Exception {
        if (clientId != null) {
            return importCloud(inputPath, null, null);
        } else {
            return importServerDC(inputPath, null, null);
        }
    }

    public JSONArray importFrom(String inputPath, JSONObject testInfo) throws Exception {
        if (clientId != null) {
            return importCloud(inputPath, testInfo, null);
        } else {
            return importServerDC(inputPath, testInfo, null);
        }
    }

    public JSONArray importFrom(String inputPath, JSONObject testInfo, JSONObject precondInfo) throws Exception {
        if (clientId != null) {
            return importCloud(inputPath, testInfo, precondInfo);
        } else {
            return importServerDC(inputPath, testInfo, precondInfo);
        }
    }

    public JSONArray importServerDC(String inputPath, JSONObject testInfo, JSONObject precondInfo) throws Exception {
        OkHttpClient client = CommonUtils.getHttpClient(this.useInternalTestProxy, this.ignoreSslErrors, this.timeout);

        File inputFile = new File(inputPath);
        String credentials;
        if (jiraPersonalAccessToken!= null) {
            credentials = BEARER_HEADER_PREFIX + jiraPersonalAccessToken;
        } else {
            credentials = Credentials.basic(jiraUsername, jiraPassword);
        }

        String endpointUrl = jiraBaseUrl + "/rest/raven/2.0/import/feature";
        Response response = null;
        HttpUrl url = HttpUrl.get(endpointUrl);
        HttpUrl.Builder builder = url.newBuilder();
        MultipartBody requestBody = null;

        if (projectKey != null) {
            builder.addQueryParameter("projectKey", this.projectKey);
        }
        if (updateRepository != null) {
            builder.addQueryParameter("updateRepository", this.updateRepository.toString());
        }

        MediaType mediaType;
        if (inputPath.toLowerCase().endsWith(".zip")) {
            mediaType = MEDIA_TYPE_ZIP;
        } else if (inputPath.toLowerCase().endsWith(FEATURE_EXTENSION)) {
            mediaType = MEDIA_TYPE_FOR_FEATURE_FILES;
        } else {
            // it may be a directory; check it, and if so zip it before sending it
            mediaType = MEDIA_TYPE_ZIP;
            if (inputFile.isDirectory()) {
                Path tempZip = Files.createTempFile("dummy", ".zip");
                zipDirectory(inputPath, tempZip.toFile().getAbsolutePath());
                inputFile = tempZip.toFile();
            }
        }

        String partName = "file";
        try {
            Builder requestBodyBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(partName, inputFile.getName(), RequestBody.create(inputFile, mediaType));
            if (testInfo != null) {
                requestBodyBuilder =  requestBodyBuilder.addFormDataPart("testInfo", "testinfo.json", RequestBody.create(testInfo.toString(), MEDIA_TYPE_JSON));
            }
            if (precondInfo != null) {
                requestBodyBuilder = requestBodyBuilder.addFormDataPart("preCondInfo", "precondinfo.json", RequestBody.create(precondInfo.toString(), MEDIA_TYPE_JSON));
            }
            requestBody = requestBodyBuilder.build();
        } catch (Exception e1) {
            logger.error(e1);
            throw e1;
        }

        Request request = new Request.Builder().url(builder.build()).post(requestBody).addHeader(AUTHORIZATION_HEADER, credentials).build();
        CommonUtils.logRequest(logger, request);
        try {
            response = client.newCall(request).execute();
            CommonUtils.logResponse(logger, response);
            String responseBody = response.body().string();
            if (response.isSuccessful()){
                return new JSONArray(responseBody);
            } else {
                throw new IOException("Unexpected HTTP code " + response);
            }
        } catch (IOException e) {
            logger.error(e);
            throw e;
        }
    }

    public JSONArray importCloud(String inputPath, JSONObject testInfo, JSONObject precondInfo) throws Exception {
        OkHttpClient client = CommonUtils.getHttpClient(this.useInternalTestProxy, this.ignoreSslErrors, this.timeout);
        
        File inputFile = new File(inputPath);
        String authenticationPayload = "{ \"client_id\": \"" + clientId +"\", \"client_secret\": \"" + clientSecret +"\" }";
        RequestBody body = RequestBody.create(authenticationPayload, MEDIA_TYPE_JSON);
        Request request = new Request.Builder().url(XRAY_CLOUD_AUTHENTICATE_URL).post(body).build();
        CommonUtils.logRequest(logger, request);
    
        Response response = null;
        String authToken = null;
        try {
            response = client.newCall(request).execute();
            CommonUtils.logResponse(logger, response, false);
            String responseBody = response.body().string();
            if (response.isSuccessful()){
                authToken = responseBody.replace("\"", "");	
            } else {
                throw new IOException("failed to authenticate " + response);
            }
        } catch (IOException e) {
            logger.error(e);
            throw e;
        }
        String credentials = BEARER_HEADER_PREFIX + authToken;

        String endpointUrl =  XRAY_CLOUD_API_BASE_URL + "/import/feature";
        HttpUrl url = HttpUrl.get(endpointUrl);
        HttpUrl.Builder builder = url.newBuilder();
        MultipartBody requestBody = null;

        if (projectKey != null) {
            builder.addQueryParameter("projectKey", this.projectKey);
        }
        if (projectId != null) {
            builder.addQueryParameter("projectId", this.projectId);
        }
        if (source != null) {
            builder.addQueryParameter("source", this.source);
        }

        MediaType mediaType;
        if (inputPath.toLowerCase().endsWith(".zip")) {
            mediaType = MEDIA_TYPE_ZIP;
        } else if (inputPath.toLowerCase().endsWith(FEATURE_EXTENSION)) {
            mediaType = MEDIA_TYPE_FOR_FEATURE_FILES;
        } else {
            // it may be a directory; check it, and if so zip it before sending it
            mediaType = MEDIA_TYPE_ZIP;
            if (inputFile.isDirectory()) {
                Path tempZip = Files.createTempFile("dummy", ".zip");
                zipDirectory(inputPath, tempZip.toFile().getAbsolutePath());
                inputFile = tempZip.toFile();
            }
        }

        String partName = "file";
        try {
            Builder requestBodyBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(partName, inputFile.getName(), RequestBody.create(inputFile, mediaType));
            if (testInfo != null) {
                requestBodyBuilder = requestBodyBuilder.addFormDataPart("testInfo", "testinfo.json", RequestBody.create(testInfo.toString(), MEDIA_TYPE_JSON));
            }
            if (precondInfo != null) {
                requestBodyBuilder = requestBodyBuilder.addFormDataPart("precondInfo", "precondinfo.json", RequestBody.create(precondInfo.toString(), MEDIA_TYPE_JSON));
            }
            requestBody = requestBodyBuilder.build();
        } catch (Exception e1) {
            logger.error(e1);
            throw e1;
        }

        request = new Request.Builder().url(builder.build()).post(requestBody).addHeader(AUTHORIZATION_HEADER, credentials).build();
        CommonUtils.logRequest(logger, request);
        try {
            response = client.newCall(request).execute();
            CommonUtils.logResponse(logger, response);
            String responseBody = response.body().string();
            if (response.isSuccessful()){
                JSONArray responseObj = new JSONArray();
                responseObj.put(new JSONObject(responseBody));
                return responseObj;
            } else {
                throw new IOException("Unexpected HTTP code " + response);
            }
        } catch (IOException e) {
            logger.error(e);
            throw e;
        }
    }


    public static void zipDirectory(String sourceDir, String outputZip) throws IOException {
        FileOutputStream fos = new FileOutputStream(outputZip);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(sourceDir);

        zipFile(fileToZip, fileToZip.getName(), zipOut, false);
        zipOut.close();
        fos.close();
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut, boolean createDir) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (createDir) {
                if (fileName.endsWith("/")) {
                    zipOut.putNextEntry(new ZipEntry(fileName));
                } else {
                    zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                }
                zipOut.closeEntry();
            }

            File[] children = fileToZip.listFiles();
            for (File childFile : children) {

                // zip only .feature files and subdirs
                if (!(childFile.isDirectory() || childFile.getName().toLowerCase().endsWith(FEATURE_EXTENSION)))
                    continue;

                if (createDir) {
                    zipFile(childFile, fileName + "/" + childFile.getName(), zipOut, true);
                } else {
                    zipFile(childFile, childFile.getName(), zipOut, true);
                }
                
            }
            return;
        }
        try (FileInputStream fis = new FileInputStream(fileToZip)) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        }
    }
}
