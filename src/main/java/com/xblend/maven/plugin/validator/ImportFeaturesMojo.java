package com.xblend.maven.plugin.validator;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import com.xblend.xray.XrayFeaturesExporter;
import com.xblend.xray.XrayFeaturesImporter;
import com.xblend.xray.XrayResultsImporter;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Counts the number of maven dependencies of a project.
 * 
 * It can be filtered by scope.
 *
 */
@Mojo(name = "import-features", defaultPhase = LifecyclePhase.COMPILE)
public class ImportFeaturesMojo extends AbstractMojo {

    // https://maven.apache.org/guides/mini/guide-configuring-plugins.html
    // 

    @Parameter(property = "xray.jiraBaseUrl", required = false)
    private String jiraBaseUrl;

    @Parameter(property = "xray.jiraUsername", required = false)
    private String jiraUsername;

    @Parameter(property = "xray.jiraPassword", required = false)
    private String jiraPassword;

    @Parameter(property = "xray.jiraToken", required = false)
    private String jiraToken;

    @Parameter(property = "xray.clientId", required = false)
    private String clientId;

    @Parameter(property = "xray.clientSecret", required = false)
    private String clientSecret;
    
    @Parameter(property = "xray.cloud", required = true)
    private Boolean cloud;



    @Parameter(property = "xray.projectKey", required = false)
    private String projectKey;

    @Parameter(property = "xray.projectId", required = false)
    private String projectId;

    @Parameter(property = "xray.source", required = false)
    private String source;

    @Parameter(property = "xray.testInfoJson", required = false)
    private String testInfoJson;

    @Parameter(property = "xray.precondInfoJson", required = false)
    private String precondInfoJson;

    @Parameter(property = "xray.inputFeatures", required = false)
    private String inputFeatures;

    @Parameter(property = "xray.abortOnError", required = false)
    private Boolean abortOnError;

    @Parameter(property = "xray.useInternalTestProxy", required = false)
    private Boolean useInternalTestProxy;

    @Parameter(property = "xray.ignoreSslErrors", required = false)
    private Boolean ignoreSslErrors;

    @Parameter(property = "xray.timeout", required = false, defaultValue = "50")
    private Integer timeout;

    /**
     * Scope to filter the dependencies.
     */
    @Parameter(property = "scope")
    String scope;

    /**
     * Gives access to the Maven project information.
     */
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    public void execute() throws MojoExecutionException, MojoFailureException {
        XrayFeaturesImporter xrayFeaturesImporter;
        JSONArray response = null;

		try {
            getLog().debug("cloud from config: " + cloud);
            getLog().debug("projectKey from config: " + projectKey);
            getLog().debug("projectId from config: " + projectId);
            getLog().debug("source from config: " + source);
            getLog().debug("inputFeatures from config: " + inputFeatures);

            if (cloud) {
                xrayFeaturesImporter = new XrayFeaturesImporter.CloudBuilder(clientId, clientSecret)
                    .withInternalTestProxy(useInternalTestProxy)
                    .withIgnoreSslErrors(ignoreSslErrors)
                    .withTimeout(timeout)
                    .withProjectKey(projectKey)
                    .withProjectId(projectId)
                    .withSource(source)
                    .build();
                response = xrayFeaturesImporter.importFrom(inputFeatures);
            } else {
                if (jiraToken != null) {
                    xrayFeaturesImporter = new XrayFeaturesImporter.ServerDCBuilder(jiraBaseUrl, jiraToken)
                    .withInternalTestProxy(useInternalTestProxy)
                    .withIgnoreSslErrors(ignoreSslErrors)
                    .withTimeout(timeout)
                    .withProjectKey(projectKey)
                    .build();                    
                } else {
                    xrayFeaturesImporter = new XrayFeaturesImporter.ServerDCBuilder(jiraBaseUrl, jiraUsername, jiraPassword)
                    .withInternalTestProxy(useInternalTestProxy)
                    .withIgnoreSslErrors(ignoreSslErrors)
                    .withTimeout(timeout)
                    .withProjectKey(projectKey)
                    .build();
                }

                response = xrayFeaturesImporter.importFrom(inputFeatures);
            }

            getLog().info("response: " + response);
        } catch (Exception ex) {
            getLog().error(ex.getMessage());
            if (abortOnError)
                System.exit(1);
        }

    }

}
