package app.getxray.xray;

import static app.getxray.xray.CommonCloud.authenticateXrayAPIKeyCredentials;
import static app.getxray.xray.CommonUtils.createHttpClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.Map;
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

// define a custom exception for import errors
class XrayFeaturesImporterException extends Exception {
    public XrayFeaturesImporterException(String message) {
        super(message);
    }
}

public class XrayFeaturesImporter {
    private static final String FEATURE_EXTENSION = ".feature";
    private static final MediaType MEDIA_TYPE_ZIP = MediaType.parse("application/zip");
    private static final MediaType MEDIA_TYPE_FOR_FEATURE_FILES = MediaType.parse("text/plain");
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_HEADER_PREFIX = "Bearer ";
    private static final String TEMP_DIR_PREFIX = "import_features";

    private String jiraBaseUrl;
    private String jiraUsername;
    private String jiraPassword;
    private String jiraPersonalAccessToken;

    private String clientId;
    private String clientSecret;
    private String cloudApiBaseUrl;

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
        this.cloudApiBaseUrl = builder.cloudApiBaseUrl;

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
        private String cloudApiBaseUrl = CommonCloud.XRAY_CLOUD_API_BASE_URL;

        private String projectKey;
        private String projectId;
        private String source;

        private Boolean ignoreSslErrors = false;
        private Boolean useInternalTestProxy = false;
        private Integer timeout = 50;
        private Boolean verbose = false;
        private Log logger;

        public CloudBuilder(String clientId, String clientSecret, String cloudApiBaseUrl) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            this.cloudApiBaseUrl = cloudApiBaseUrl;
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

    public JSONArray importFrom(String inputPath) throws IOException, XrayFeaturesImporterException {
        if (clientId != null) {
            return importCloud(inputPath, null, null);
        } else {
            return importServerDC(inputPath, null, null);
        }
    }

    public JSONArray importFrom(String inputPath, JSONObject testInfo) throws IOException, XrayFeaturesImporterException{
        if (clientId != null) {
            return importCloud(inputPath, testInfo, null);
        } else {
            return importServerDC(inputPath, testInfo, null);
        }
    }

    public JSONArray importFrom(String inputPath, JSONObject testInfo, JSONObject precondInfo) throws IOException, XrayFeaturesImporterException{
        if (clientId != null) {
            return importCloud(inputPath, testInfo, precondInfo);
        } else {
            return importServerDC(inputPath, testInfo, precondInfo);
        }
    }

    public JSONArray importServerDC(String inputPath, JSONObject testInfo, JSONObject precondInfo) throws XrayFeaturesImporterException, IOException {
        OkHttpClient client = createHttpClient(this.useInternalTestProxy, this.ignoreSslErrors, this.timeout);

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
            if (inputFile.isDirectory()) {
                mediaType = MEDIA_TYPE_ZIP;
                File tempDir = Files.createTempDirectory(TEMP_DIR_PREFIX).toFile();
                File tempZip = File.createTempFile("dummy", ".zip", tempDir);
                zipDirectory(inputPath, tempZip.getAbsolutePath());
                inputFile = tempZip;
            } else {
                throw new XrayFeaturesImporterException("Unsupported file format");
            }
        }

        String partName = "file";
        Builder requestBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(partName, inputFile.getName(), RequestBody.create(inputFile, mediaType));
        if (testInfo != null) {
            requestBodyBuilder = requestBodyBuilder.addFormDataPart("testInfo", "testinfo.json", RequestBody.create(testInfo.toString(), MEDIA_TYPE_JSON));
        }
        if (precondInfo != null) {
            requestBodyBuilder = requestBodyBuilder.addFormDataPart("preCondInfo", "precondinfo.json", RequestBody.create(precondInfo.toString(), MEDIA_TYPE_JSON));
        }
        requestBody = requestBodyBuilder.build();

        Request request = new Request.Builder().url(builder.build()).post(requestBody).addHeader(AUTHORIZATION_HEADER, credentials).build();
        CommonUtils.logRequest(logger, request, this.verbose);
        try {
            response = client.newCall(request).execute();
            CommonUtils.logResponse(logger, response, this.verbose);
            String responseBody = response.body().string();
            if (response.isSuccessful()){
                return new JSONArray(responseBody);
            } else {
                throw new IOException("Unexpected HTTP code " + response);
            }
        } catch (IOException e) {
            logger.error(e);
            throw new XrayFeaturesImporterException(e.getMessage());
        }
    }

    public JSONArray importCloud(String inputPath, JSONObject testInfo, JSONObject precondInfo) throws XrayFeaturesImporterException, IOException {
        OkHttpClient client = createHttpClient(this.useInternalTestProxy, this.ignoreSslErrors, this.timeout);

        String authToken = authenticateXrayAPIKeyCredentials(logger, verbose, client, clientId, clientSecret, cloudApiBaseUrl);
        String credentials = BEARER_HEADER_PREFIX + authToken;
        
        File inputFile = new File(inputPath);

        String endpointUrl = cloudApiBaseUrl + "/import/feature";
        HttpUrl url = HttpUrl.get(endpointUrl);
        HttpUrl.Builder builder = url.newBuilder();
        MultipartBody requestBody = null;

        Map<String, String> parameters = new HashMap<>();
        parameters.put("projectKey", projectKey);
        parameters.put("projectId", projectId);
        parameters.put("source", source);

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String value = entry.getValue();
            if (value != null) {
                builder.addQueryParameter(entry.getKey(), value);
            }
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
                File tempDir = Files.createTempDirectory(TEMP_DIR_PREFIX).toFile();
                File tempZip = File.createTempFile("dummy", ".zip", tempDir);
                zipDirectory(inputPath, tempZip.getAbsolutePath());
                inputFile = tempZip;
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
            throw new XrayFeaturesImporterException(e1.getMessage());
        }

        Request request = new Request.Builder().url(builder.build()).post(requestBody).addHeader(AUTHORIZATION_HEADER, credentials).build();
        CommonUtils.logRequest(logger, request, this.verbose);
        try {
            Response response = client.newCall(request).execute();
            CommonUtils.logResponse(logger, response, this.verbose);
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
            throw new XrayFeaturesImporterException(e.getMessage());
        }
    }


    public static void zipDirectory(String sourceDir, String outputZip) throws IOException {
        FileOutputStream fos = new FileOutputStream(outputZip);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        ZipOutputStream zipOut = new ZipOutputStream(bos);
        zipOut.setMethod(ZipOutputStream.DEFLATED);
        zipOut.setLevel(0);
        File fileToZip = new File(sourceDir);

        zipFile(fileToZip, fileToZip.getName(), zipOut, false);
        zipOut.finish();
        zipOut.close();
        fos.close();
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut, boolean createDir) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (createDir) {
                ZipEntry zipEntry;
                if (fileName.endsWith("/")) {
                    zipEntry = new ZipEntry(fileName);
                } else {
                    zipEntry = new ZipEntry(fileName + "/");
                }
                FileTime time=FileTime.fromMillis(System.currentTimeMillis());
                zipEntry.setCreationTime(time);
                zipEntry.setLastAccessTime(time);
                zipEntry.setLastModifiedTime(time);
                zipOut.putNextEntry(zipEntry);
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
            zipEntry.setSize(fileToZip.length());
            FileTime time=FileTime.fromMillis(System.currentTimeMillis());
            zipEntry.setCreationTime(time);
            zipEntry.setLastAccessTime(time);
            zipEntry.setLastModifiedTime(time);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) > 0) {
                zipOut.write(bytes, 0, length);
            }
            zipOut.flush();
            zipOut.closeEntry();
        }
    }
}
