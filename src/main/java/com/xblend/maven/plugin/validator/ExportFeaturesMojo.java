package com.xblend.maven.plugin.validator;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import com.xblend.xray.XrayFeaturesExporter;
import com.xblend.xray.XrayResultsImporter;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.json.JSONObject;

/**
 * Counts the number of maven dependencies of a project.
 * 
 * It can be filtered by scope.
 *
 */
@Mojo(name = "export-features", defaultPhase = LifecyclePhase.COMPILE)
public class ExportFeaturesMojo extends AbstractMojo {

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

    @Parameter(property = "xray.issueKeys", required = false)
    private String issueKeys;

    @Parameter(property = "xray.filterId", required = false)
    private String filterId;

    @Parameter(property = "xray.outputDir", required = false)
    private String outputDir;

    @Parameter(property = "xray.abortOnError", required = false)
    private Boolean abortOnError;

    @Parameter(property = "xray.useInternalTestProxy", required = false)
    private Boolean useInternalTestProxy;

    @Parameter(property = "xray.ignoreSslErrors", required = false)
    private Boolean ignoreSslErrors;

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
        XrayFeaturesExporter xrayFeaturesExporter;
        String response = null;
		try {
            getLog().debug("cloud from config: " + cloud);
            getLog().debug("issueKeys from config: " + issueKeys);
            getLog().debug("filterId from config: " + filterId);

            if (cloud) {
                xrayFeaturesExporter = new XrayFeaturesExporter.CloudBuilder(clientId, clientSecret).withInternalTestProxy(useInternalTestProxy).withIgnoreSslErrors(ignoreSslErrors)
                    .withIssueKeys(issueKeys)
                    .withFilterId(filterId)
                    .build();
                response = xrayFeaturesExporter.submit(outputDir);
            } else {
                if (jiraToken != null) {
                    xrayFeaturesExporter = new XrayFeaturesExporter.ServerDCBuilder(jiraBaseUrl, jiraToken).withInternalTestProxy(useInternalTestProxy).withIgnoreSslErrors(ignoreSslErrors)
                        .withIssueKeys(issueKeys)
                        .withFilterId(filterId)
                        .build();
                } else {
                    xrayFeaturesExporter = new XrayFeaturesExporter.ServerDCBuilder(jiraBaseUrl, jiraUsername, jiraPassword).withInternalTestProxy(useInternalTestProxy).withIgnoreSslErrors(ignoreSslErrors)
                        .withIssueKeys(issueKeys)
                        .withFilterId(filterId)
                        .build();
                }

                response = xrayFeaturesExporter.submit(outputDir);
            }

            getLog().info("response: " + response);
        } catch (Exception ex) {
            getLog().error(ex.getMessage());
            if (abortOnError)
                System.exit(1);
        }

    }

}
