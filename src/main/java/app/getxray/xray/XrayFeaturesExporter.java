package app.getxray.xray;

import static app.getxray.xray.CommonCloud.XRAY_CLOUD_API_BASE_URL;
import static app.getxray.xray.CommonCloud.authenticateXrayAPIKeyCredentials;
import static app.getxray.xray.CommonUtils.createHttpClient;
import static app.getxray.xray.CommonUtils.unzipContentsToFolder;

import java.io.IOException;
import org.apache.maven.plugin.logging.Log;

import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// https://docs.getxray.app/display/XRAYCLOUD/Exporting+Cucumber+Tests+-+REST+v2
// https://docs.getxray.app/display/XRAY/Exporting+Cucumber+Tests+-+REST

// define a custom exception for import errors
class XrayFeaturesExporterException extends Exception {
    public XrayFeaturesExporterException(String message) {
        super(message);
    }
}

public class XrayFeaturesExporter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_HEADER_PREFIX = "Bearer ";

    private String jiraBaseUrl;
    private String jiraUsername;
    private String jiraPassword;
    private String jiraPersonalAccessToken;

    private String clientId;
    private String clientSecret;

    private String issueKeys;
    private String filterId;

    private Boolean ignoreSslErrors = false;
    private Boolean useInternalTestProxy = false;
    private Integer timeout = 50;
    private Boolean verbose = false;
    private Log logger;

    private XrayFeaturesExporter(ServerDCBuilder builder) {
        this.jiraBaseUrl = builder.jiraBaseUrl;
        this.jiraUsername = builder.jiraUsername;
        this.jiraPassword = builder.jiraPassword;
        this.jiraPersonalAccessToken = builder.jiraPersonalAccessToken;

        this.issueKeys = builder.issueKeys;
        this.filterId = builder.filterId;

        this.ignoreSslErrors = builder.ignoreSslErrors;
        this.useInternalTestProxy = builder.useInternalTestProxy;
        this.timeout = builder.timeout;
        this.verbose = builder.verbose;
        this.logger = builder.logger;
    }

    private XrayFeaturesExporter(CloudBuilder builder) {
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret;

        this.issueKeys = builder.issueKeys;
        this.filterId = builder.filterId;

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

        private String issueKeys;
        private String filterId;

        private Boolean useInternalTestProxy = false;
        private Boolean ignoreSslErrors = false;
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

        public ServerDCBuilder withInternalTestProxy(Boolean useInternalTestProxy) {
            this.useInternalTestProxy = useInternalTestProxy;
            return this;
        }

        public ServerDCBuilder withIgnoreSslErrors(Boolean ignoreSslErrors) {
            this.ignoreSslErrors = ignoreSslErrors;
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

        public ServerDCBuilder withIssueKeys(String issueKeys) {
            this.issueKeys = issueKeys;
            return this;
        }

        public ServerDCBuilder withFilterId(String filterId) {
            this.filterId = filterId;
            return this;
        }

        public XrayFeaturesExporter build() {
            return new XrayFeaturesExporter(this);
        }

    }

    public static class CloudBuilder {

        private final String clientId;
        private final String clientSecret;

        private String issueKeys;
        private String filterId;

        private Boolean ignoreSslErrors = false;
        private Boolean useInternalTestProxy = false;
        private Integer timeout = 50;
        private Boolean verbose = false;
        private Log logger;

        public CloudBuilder(String clientId, String clientSecret) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
        }

        public CloudBuilder withInternalTestProxy(Boolean useInternalTestProxy) {
            this.useInternalTestProxy = useInternalTestProxy;
            return this;
        }

        public CloudBuilder withIgnoreSslErrors(Boolean ignoreSslErrors) {
            this.ignoreSslErrors = ignoreSslErrors;
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
    
        public CloudBuilder withIssueKeys(String issueKeys) {
            this.issueKeys = issueKeys;
            return this;
        }

        public CloudBuilder withFilterId(String filterId) {
            this.filterId = filterId;
            return this;
        }

        public XrayFeaturesExporter build() {
            return new XrayFeaturesExporter(this);
        }

    }

    public String submit(String outputPath) throws XrayFeaturesExporterException, IOException {
        if (clientId != null) {
            return submitStandardCloud(outputPath);
        } else {
            return submitStandardServerDC(outputPath);
        }
    }

    public String submitStandardServerDC(String outputPath) throws XrayFeaturesExporterException, IOException {
        OkHttpClient client = createHttpClient(this.useInternalTestProxy, this.ignoreSslErrors, this.timeout);

        String credentials;
        if (jiraPersonalAccessToken!= null) {
            credentials = BEARER_HEADER_PREFIX + jiraPersonalAccessToken;
        } else {
            credentials = Credentials.basic(jiraUsername, jiraPassword);
        }

        String endpointUrl = jiraBaseUrl + "/rest/raven/2.0/export/test";
        Request request;
        Response response = null;
        HttpUrl url = HttpUrl.get(endpointUrl);
        HttpUrl.Builder builder = url.newBuilder();

        builder.addQueryParameter("fz", "true");
        if (issueKeys != null) {
            builder.addQueryParameter("keys", this.issueKeys);
        }
        if (filterId != null) {
            builder.addQueryParameter("filter", this.filterId);
        }

        request = new Request.Builder().url(builder.build()).get().addHeader(AUTHORIZATION_HEADER, credentials).build();
        CommonUtils.logRequest(logger, request, this.verbose);
        try {
            response = client.newCall(request).execute();
            CommonUtils.logResponse(logger, response, this.verbose);
            if (response.isSuccessful()) {
                unzipContentsToFolder(response.body().byteStream(), outputPath);
                return ("ok");
            } else {
                throw new IOException("Unexpected HTTP code " + response);
            }
        } catch (IOException e) {
            logger.error(e);
            throw new XrayFeaturesExporterException(e.getMessage());
        }
    }

    public String submitStandardCloud(String outputPath) throws XrayFeaturesExporterException, IOException {
        OkHttpClient client = createHttpClient(this.useInternalTestProxy, this.ignoreSslErrors, this.timeout);
        try {
            String authToken = authenticateXrayAPIKeyCredentials(logger, verbose, client, clientId, clientSecret);
            String credentials = BEARER_HEADER_PREFIX + authToken;

            String endpointUrl = XRAY_CLOUD_API_BASE_URL + "/export/cucumber";
            HttpUrl url = HttpUrl.get(endpointUrl);
            HttpUrl.Builder builder = url.newBuilder();

            if (issueKeys != null) {
                builder.addQueryParameter("keys", this.issueKeys);
            }
            if (filterId != null) {
                builder.addQueryParameter("filter", this.filterId);
            }

            Request request = new Request.Builder().url(builder.build()).get().addHeader(AUTHORIZATION_HEADER, credentials).build();
            CommonUtils.logRequest(logger, request, this.verbose);

            Response response = client.newCall(request).execute();
            CommonUtils.logResponse(logger, response, this.verbose);
            if (response.isSuccessful()) {
                unzipContentsToFolder(response.body().byteStream(), outputPath);
                return ("ok");
            } else {
                throw new IOException("Unexpected HTTP code " + response);
            }
        } catch (IOException e) {
            logger.error(e);
            throw new XrayFeaturesExporterException(e.getMessage());
        }
    }

}
