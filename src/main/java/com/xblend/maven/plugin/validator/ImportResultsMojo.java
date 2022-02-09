package com.xblend.maven.plugin.validator;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

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
@Mojo(name = "import-results", defaultPhase = LifecyclePhase.COMPILE)
public class ImportResultsMojo extends AbstractMojo {

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

    @Parameter(property = "xray.reportFile", required = true)
    private String reportFile;

    @Parameter(property = "xray.reportFormat", required = true)
    private String reportFormat;

    @Parameter(property = "xray.projectKey", required = false)
    private String projectKey;

    @Parameter(property = "xray.testPlanKey", required = false)
    private String testPlanKey;

    @Parameter(property = "xray.testExecKey", required = false)
    private String testExecKey;

    @Parameter(property = "xray.version", required = false)
    private String version;

    @Parameter(property = "xray.revision", required = false)
    private String revision;

    @Parameter(property = "xray.testEnvironment", required = false)
    private String testEnvironment;

    @Parameter(property = "xray.testInfoJson", required = false)
    private String testInfoJson;

    @Parameter(property = "xray.testExecInfoJson", required = false)
    private String testExecInfoJson;

    @Parameter(property = "xray.abortOnError", required = false)
    private Boolean abortOnError;

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
		XrayResultsImporter xrayImporter;
		JSONObject testExecInfo;
		JSONObject testInfo;


        String response = null;
		try {
            getLog().debug("clientId from config: " + clientId);
            getLog().debug("clientSecret from config: " + clientSecret);
            getLog().debug("cloud from config: " + cloud);
            getLog().debug("testInfoJson from config: " + testInfoJson);

            if (cloud) {

                // if testInfo and testExecInfo are not present, then use the standard endpoint
                // all formats support params, except for cucumber
                

                com.xblend.xray.XrayResultsImporter.CloudBuilder xrayImporterBuilder = new XrayResultsImporter.CloudBuilder(clientId, clientSecret);
                if (testInfoJson==null  && testExecInfoJson==null) {       
                    if ("xray".equals(reportFormat) || "cucumber".equals(reportFormat)) {
                        xrayImporter = xrayImporterBuilder.build();
                    } else {
                        xrayImporter = xrayImporterBuilder
                            .withProjectKey(projectKey)
                            .withVersion(version)
                            .withRevision(revision)
                            .withTestPlanKey(testPlanKey)
                            .withTestExecKey(testExecKey)
                            .withTestEnvironment(testEnvironment)
                            .build();                     
                    }
                    response = xrayImporter.submit(reportFormat, reportFile);
                } else {
                    if (testInfoJson != null) {
                        testInfo = new JSONObject(new String(Files.readAllBytes(Paths.get(testInfoJson))));
                    } else {
                        testInfo = new JSONObject();
                    }
                    if (testExecInfoJson != null) {
                        testExecInfo = new JSONObject(new String(Files.readAllBytes(Paths.get(testExecInfoJson))));
                    } else {
                        testExecInfo = new JSONObject();
                    }
                    response = xrayImporterBuilder.build().submitMultipartCloud(reportFormat, reportFile, testExecInfo, testInfo);                  
                }

            } else {
                // server/DC

                com.xblend.xray.XrayResultsImporter.ServerDCBuilder xrayImporterBuilder;
                if (jiraToken != null) {
                    xrayImporterBuilder = new XrayResultsImporter.ServerDCBuilder(jiraBaseUrl, jiraToken);
                } else {
                    xrayImporterBuilder = new XrayResultsImporter.ServerDCBuilder(jiraBaseUrl, jiraUsername, jiraPassword);
                }

                if (testInfoJson==null  && testExecInfoJson==null) {       
                    if ("xray".equals(reportFormat) || "cucumber".equals(reportFormat) || "behave".equals(reportFormat)) {
                        xrayImporter = xrayImporterBuilder.build();
                    } else {
                        xrayImporter = xrayImporterBuilder
                            .withProjectKey(projectKey)
                            .withVersion(version)
                            .withRevision(revision)
                            .withTestPlanKey(testPlanKey)
                            .withTestExecKey(testExecKey)
                            .withTestEnvironment(testEnvironment)
                            .build();  
                    }
                    response = xrayImporter.submit(reportFormat, reportFile);   
                } else {
                    if (testInfoJson != null) {
                        testInfo = new JSONObject(new String(Files.readAllBytes(Paths.get(testInfoJson))));
                    } else {
                        testInfo = new JSONObject();
                    }
                    if (testExecInfoJson != null) {
                        testExecInfo = new JSONObject(new String(Files.readAllBytes(Paths.get(testExecInfoJson))));
                    } else {
                        testExecInfo = new JSONObject();
                    }
                    response = xrayImporterBuilder.build().submitMultipartServerDC(reportFormat, reportFile, testExecInfo, testInfo);                  
                }

            }

            getLog().info("response: " + response);
        } catch (Exception ex) {
            getLog().error(ex.getMessage());
            if (abortOnError)
                System.exit(1);
        }

    }

}
