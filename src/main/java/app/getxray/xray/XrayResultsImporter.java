package app.getxray.xray;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.apache.maven.plugin.logging.Log;

import static app.getxray.xray.CommonCloud.authenticateXrayAPIKeyCredentials;
import static app.getxray.xray.CommonUtils.createHttpClient;

// define a custom exception for import errors
class XrayResultsImporterException extends Exception {
    public XrayResultsImporterException(String message) {
        super(message);
    }
}

public class XrayResultsImporter {
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");
    private static final MediaType MEDIA_TYPE_XML = MediaType.parse("application/xml");

    public static final String XRAY_FORMAT = "xray";
    public static final String JUNIT_FORMAT = "junit";
    public static final String TESTNG_FORMAT = "testng";
    public static final String ROBOT_FORMAT = "robot";
    public static final String XUNIT_FORMAT = "xunit";
    public static final String NUNIT_FORMAT = "nunit";
    public static final String CUCUMBER_FORMAT = "cucumber";
    public static final String BEHAVE_FORMAT = "behave";
    private static final String UNSUPPORTED_REPORT_FORMAT = "unsupported report format: ";
    private static final String UNEXPECTED_HTTP_CODE = "Unexpected HTTP code ";
    private static final String BEARER_HEADER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private String jiraBaseUrl;
    private String jiraUsername;
    private String jiraPassword;
    private String jiraPersonalAccessToken;

    private String clientId;
    private String clientSecret;
    private String cloudApiBaseUrl;

    private String projectKey;
    private String fixVersion;
    private String revision;
    private String testPlanKey;
    private String testExecKey;
    private String testEnvironment;

    private Boolean ignoreSslErrors = false;
    private Boolean useInternalTestProxy = false;
    private Integer timeout = 50;
    private Boolean verbose = false;
    private Log logger;

    private XrayResultsImporter(ServerDCBuilder builder){
        this.jiraBaseUrl = builder.jiraBaseUrl;
        this.jiraUsername = builder.jiraUsername;
        this.jiraPassword = builder.jiraPassword;
        this.jiraPersonalAccessToken = builder.jiraPersonalAccessToken;
        this.projectKey = builder.projectKey;
        this.fixVersion = builder.fixVersion;
        this.revision = builder.revision;
        this.testPlanKey = builder.testPlanKey;
        this.testExecKey = builder.testExecKey;
        this.testEnvironment = builder.testEnvironment;

        this.ignoreSslErrors = builder.ignoreSslErrors;
        this.useInternalTestProxy = builder.useInternalTestProxy;
        this.timeout = builder.timeout;
        this.verbose = builder.verbose;
        this.logger = builder.logger;
    }

    private XrayResultsImporter(CloudBuilder builder){
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret;
        this.cloudApiBaseUrl = builder.cloudApiBaseUrl;

        this.projectKey = builder.projectKey;
        this.fixVersion = builder.fixVersion;
        this.revision = builder.revision;
        this.testPlanKey = builder.testPlanKey;
        this.testExecKey = builder.testExecKey;
        this.testEnvironment = builder.testEnvironment;

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
        private String fixVersion;
        private String revision;
        private String testPlanKey;
        private String testExecKey;
        private String testEnvironment;

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
    
        public ServerDCBuilder withVersion(String fixVersion) {
            this.fixVersion = fixVersion;
            return this;
        }

        public ServerDCBuilder withRevision(String revision) {
            this.revision = revision;
            return this;
        }

        public ServerDCBuilder withTestPlanKey(String testPlanKey) {
            this.testPlanKey = testPlanKey;
            return this;
        }

        public ServerDCBuilder withTestExecKey(String testExecKey) {
            this.testExecKey = testExecKey;
            return this;
        }

        public ServerDCBuilder withTestEnvironment(String testEnvironment) {
            this.testEnvironment = testEnvironment;
            return this;
        }

        public XrayResultsImporter build() {
            return new XrayResultsImporter(this);
        }
    
    }

    public static class CloudBuilder {

        private final String clientId;
        private final String clientSecret;
        private String cloudApiBaseUrl = CommonCloud.XRAY_CLOUD_API_BASE_URL;

        private String projectKey;
        private String fixVersion;
        private String revision;
        private String testPlanKey;
        private String testExecKey;
        private String testEnvironment;

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
/*
        public CloudBuilder withCloudApiBaseUrl(String cloudApiBaseUrl) {
            this.cloudApiBaseUrl = cloudApiBaseUrl;
            return this;
        }
*/
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

        public CloudBuilder withVersion(String fixVersion) {
            this.fixVersion = fixVersion;
            return this;
        }

        public CloudBuilder withRevision(String revision) {
            this.revision = revision;
            return this;
        }

        public CloudBuilder withTestPlanKey(String testPlanKey) {
            this.testPlanKey = testPlanKey;
            return this;
        }

        public CloudBuilder withTestExecKey(String testExecKey) {
            this.testExecKey = testExecKey;
            return this;
        }

        public CloudBuilder withTestEnvironment(String testEnvironment) {
            this.testEnvironment = testEnvironment;
            return this;
        }

        public XrayResultsImporter build() {
            return new XrayResultsImporter(this);
        }
    
    }    

    public String submit(String format, String reportFile) throws IOException, XrayResultsImporterException {
        if (clientId != null) {
            return submitStandardCloud(format, reportFile);
        } else {
            return submitStandardServerDC(format, reportFile);
        }
    }

    public String submitMultipartServerDC(String format, String reportFile, JSONObject testExecInfo, JSONObject testInfo) throws XrayResultsImporterException, IOException {
        OkHttpClient client = createHttpClient(this.useInternalTestProxy, this.ignoreSslErrors, this.timeout);

        String credentials = generateDCAuthorizationHeaderContent();

        MediaType mediaType = getMediaTypeForFormat(format);

        String endpointUrl;
        if (XRAY_FORMAT.equals(format)) {
            endpointUrl = jiraBaseUrl + "/rest/raven/2.0/import/execution/multipart"; 
        } else {
            endpointUrl = jiraBaseUrl + "/rest/raven/2.0/import/execution/" + format + "/multipart"; 
        }
        
        HttpUrl url = HttpUrl.get(endpointUrl);
        HttpUrl.Builder builder = url.newBuilder();
        MultipartBody requestBody = null;
        String partName;
        // for xray json, cucumber and behave reports use "result" instead of "file" for the multipart entity
        if (XRAY_FORMAT.equals(format) || CUCUMBER_FORMAT.equals(format) || BEHAVE_FORMAT.equals(format)) {
            partName = "result";
        } else {
            partName = "file";
        }

        try {
            okhttp3.MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(partName, reportFile, RequestBody.create(new File(reportFile), mediaType))
                    .addFormDataPart("info", "info.json", RequestBody.create(testExecInfo.toString(), MEDIA_TYPE_JSON));
            if (testInfo != null) {
                requestBodyBuilder.addFormDataPart("testInfo", "testInfo.json", RequestBody.create(testInfo.toString(), MEDIA_TYPE_JSON));
            }
            requestBody = requestBodyBuilder.build();
        } catch (Exception e1) {
            logger.error(e1);
            throw e1;
        }

        return makeHttpRequest(client, credentials, builder, requestBody);
    }

    private String makeHttpRequest(OkHttpClient client, String credentials, HttpUrl.Builder builder, RequestBody requestBody) throws XrayResultsImporterException {
        Request request = new Request.Builder().url(builder.build()).post(requestBody).addHeader(AUTHORIZATION_HEADER, credentials).build();
        CommonUtils.logRequest(logger, request, this.verbose);

        try {
            Response response = client.newCall(request).execute();
            CommonUtils.logResponse(logger, response, this.verbose);
            String responseBody = response.body().string();
            if (response.isSuccessful()){
                return responseBody;
            } else {
                throw new IOException(UNEXPECTED_HTTP_CODE + response);
            }
        } catch (IOException e) {
            logger.error(e);
            throw new XrayResultsImporterException(e.getMessage());
        }
    }

    public String submitMultipartCloud(String format, String reportFile, JSONObject testExecInfo, JSONObject testInfo) throws IOException, XrayResultsImporterException {  	
        OkHttpClient client = createHttpClient(this.useInternalTestProxy, this.ignoreSslErrors, this.timeout);

        String authToken = authenticateXrayAPIKeyCredentials(logger, verbose, client, clientId, clientSecret, cloudApiBaseUrl);
        String credentials = BEARER_HEADER_PREFIX + authToken;

        MediaType mediaType = getMediaTypeForFormat(format);

        String endpointUrl;
        if ("xray".equals(format)) {
            endpointUrl =  cloudApiBaseUrl + "/import/execution/multipart";
        } else {
            endpointUrl =  cloudApiBaseUrl + "/import/execution/" + format + "/multipart";
        }

        HttpUrl url = HttpUrl.get(endpointUrl);
        HttpUrl.Builder builder = url.newBuilder();
        MultipartBody requestBody = null;
        okhttp3.MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("results", reportFile, RequestBody.create(new File(reportFile), mediaType))
                .addFormDataPart("info", "info.json", RequestBody.create(testExecInfo.toString(), MEDIA_TYPE_JSON));
        if (testInfo != null) {
            requestBodyBuilder.addFormDataPart("testInfo", "testInfo.json", RequestBody.create(testInfo.toString(), MEDIA_TYPE_JSON));
        }
        requestBody = requestBodyBuilder.build();

        return makeHttpRequest(client, credentials, builder, requestBody);
    }

    public String generateDCAuthorizationHeaderContent() {
        if (jiraPersonalAccessToken!= null) {
            return BEARER_HEADER_PREFIX + jiraPersonalAccessToken;
        } else {
            return Credentials.basic(jiraUsername, jiraPassword);
        }
    }


    public String submitStandardServerDC(String format, String reportFile) throws IOException, XrayResultsImporterException {        
        OkHttpClient client = createHttpClient(this.useInternalTestProxy, this.ignoreSslErrors, this.timeout);

        String credentials = generateDCAuthorizationHeaderContent();
    
        MediaType mediaType = getMediaTypeForFormat(format);

        String endpointUrl;
        if (XRAY_FORMAT.equals(format)) {
            endpointUrl = jiraBaseUrl + "/rest/raven/2.0/import/execution";
        } else {
            endpointUrl = jiraBaseUrl + "/rest/raven/2.0/import/execution/" + format;
        }

        HttpUrl url = HttpUrl.get(endpointUrl);
        HttpUrl.Builder builder = url.newBuilder();
        // for cucumber and behave reports send the report directly on the body

        RequestBody requestBody = null;
        if (XRAY_FORMAT.equals(format) || CUCUMBER_FORMAT.equals(format) || BEHAVE_FORMAT.equals(format) ) {
            String reportContent = new String ( Files.readAllBytes( Paths.get(reportFile) ) );
            requestBody = RequestBody.create(reportContent, mediaType);
        } else {
            requestBody = new MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", reportFile, RequestBody.create(new File(reportFile), mediaType))
            .build();

            // for cucumber and behave formats, these URL parameters are not yet available
            Map<String, String> parameters = new HashMap<>();
            parameters.put("projectKey", projectKey);
            parameters.put("fixVersion", fixVersion);
            parameters.put("revision", revision);
            parameters.put("testPlanKey", testPlanKey);
            parameters.put("testExecKey", testExecKey);
            parameters.put("testEnvironments", testEnvironment);

            // Iterate over the parameters and add query parameters
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                String value = entry.getValue();
                if (value != null) {
                    builder.addQueryParameter(entry.getKey(), value);
                }
            }
        }

        return makeHttpRequest(client, credentials, builder, requestBody);
    }

    private MediaType getMediaTypeForFormat(String format) {
        String[] supportedFormats = new String [] { XRAY_FORMAT, JUNIT_FORMAT, TESTNG_FORMAT, ROBOT_FORMAT, NUNIT_FORMAT, XUNIT_FORMAT, CUCUMBER_FORMAT, BEHAVE_FORMAT };
        if (!Arrays.asList(supportedFormats).contains(format)) {
            throw new IllegalArgumentException(UNSUPPORTED_REPORT_FORMAT + format);
        }

        MediaType mediaType;
        String[] xmlBasedFormats = new String [] { JUNIT_FORMAT, TESTNG_FORMAT, ROBOT_FORMAT, NUNIT_FORMAT, XUNIT_FORMAT}; 
        if (Arrays.asList(xmlBasedFormats).contains(format)) {
            mediaType = MEDIA_TYPE_XML;
        } else {
            mediaType = MEDIA_TYPE_JSON;
        }
        return mediaType;
    }
    
    public String submitStandardCloud(String format, String reportFile) throws IOException, XrayResultsImporterException {
        OkHttpClient client = createHttpClient(this.useInternalTestProxy, this.ignoreSslErrors, this.timeout);

        String authToken = authenticateXrayAPIKeyCredentials(logger, verbose, client, clientId, clientSecret, cloudApiBaseUrl);
        String credentials = BEARER_HEADER_PREFIX + authToken;

        MediaType mediaType = getMediaTypeForFormat(format);

        String endpointUrl;
        if (XRAY_FORMAT.equals(format)) {
            endpointUrl = cloudApiBaseUrl + "/import/execution";
        } else {
            endpointUrl = cloudApiBaseUrl + "/import/execution/" + format;
        }
        String reportContent = new String ( Files.readAllBytes( Paths.get(reportFile) ) );
        RequestBody requestBody = RequestBody.create(reportContent, mediaType);


        HttpUrl url = HttpUrl.get(endpointUrl);
        HttpUrl.Builder builder = url.newBuilder();

        // cucumber, behave and xray endpoints dont support these URL parameters
        Map<String, String> parameters = new HashMap<>();
        parameters.put("projectKey", projectKey);
        parameters.put("fixVersion", fixVersion);
        parameters.put("revision", revision);
        parameters.put("testPlanKey", testPlanKey);
        parameters.put("testExecKey", testExecKey);
        parameters.put("testEnvironments", testEnvironment);
        
        // Iterate over the parameters and add query parameters
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String value = entry.getValue();
            if (value != null) {
                builder.addQueryParameter(entry.getKey(), value);
            }
        }

        return makeHttpRequest(client, credentials, builder, requestBody);
    }

}

