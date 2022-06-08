package app.getxray.maven.plugin.xray;

import static app.getxray.xray.CommonUtils.isTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;
import org.json.JSONObject;

import app.getxray.xray.XrayResultsImporter;

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

    private void abortWithError(String message) {
        getLog().error(message);
        System.err.println(message);
        System.exit(1);
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
		XrayResultsImporter xrayImporter;
		JSONObject testExecInfo = null;
		JSONObject testInfo = null;

        // 
        String[] reportFiles;
        File tempReportFile = new File(reportFile);
        if (tempReportFile.isFile()) {
            // common case: import a given report file
            reportFiles = new String[] { reportFile };
        } else if (tempReportFile.isDirectory()){
            // if directory, then for legacy reasons and because most used formats are XML based,
            // try to import all .xml files there
            DirectoryScanner scanner = new DirectoryScanner();
            scanner.setIncludes(new String[]{"*.xml"});
            scanner.setBasedir(reportFile);
            scanner.setCaseSensitive(false);
            scanner.scan();
            reportFiles = scanner.getIncludedFiles();
            for (int i = 0; i < reportFiles.length; i++) {
                reportFiles[i] = scanner.getBasedir() + File.separator + reportFiles[i];
                getLog().debug(reportFiles[i]);
            }
        } else {
            // regex can be used to import files, as long as it is *within* the current working directory
            DirectoryScanner scanner = new DirectoryScanner();
            scanner.setIncludes(new String[]{reportFile});
            scanner.setBasedir(".");
            scanner.setCaseSensitive(false);
            scanner.scan();
            reportFiles = scanner.getIncludedFiles();
            for (int i = 0; i < reportFiles.length; i++) {
                reportFiles[i] = scanner.getBasedir() + File.separator + reportFiles[i];
                getLog().debug(reportFiles[i]);
            }
        }
        
        if (reportFiles.length == 0) {
            abortWithError("no test report file(s) found: " + reportFile);
        }
        String response = null;

        // submit one or more report files
        for (int i = 0; i < reportFiles.length; i++) {
            String reportFile = reportFiles[i];

            try {
                getLog().debug("cloud from config: " + cloud);
                getLog().debug("jiraBaseUrl from config: " + jiraBaseUrl);
                getLog().debug("reportFile: " + reportFile);
                getLog().debug("testInfoJson from config: " + testInfoJson);
                getLog().debug("useInternalTestProxy from config: " + useInternalTestProxy);

                if (cloud) {

                    // if testInfo and testExecInfo are not present, then use the standard endpoint
                    // all formats support params, except for cucumber
                    
                    app.getxray.xray.XrayResultsImporter.CloudBuilder xrayImporterBuilder = new XrayResultsImporter.CloudBuilder(clientId, clientSecret)
                        .withInternalTestProxy(useInternalTestProxy)
                        .withIgnoreSslErrors(ignoreSslErrors)
                        .withTimeout(timeout);
                    if (testInfoJson==null  && testExecInfoJson==null) {       
                        if (XrayResultsImporter.XRAY_FORMAT.equals(reportFormat) || XrayResultsImporter.CUCUMBER_FORMAT.equals(reportFormat) || XrayResultsImporter.BEHAVE_FORMAT.equals(reportFormat)) {
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
                            if ((new File(testInfoJson)).isFile()) {
                                testInfo = new JSONObject(new String(Files.readAllBytes(Paths.get(testInfoJson))));
                            } else {
                                abortWithError("file doesnt exist: " + testInfoJson);
                            }
                        } else {
                            testInfo = new JSONObject();
                        }
                        if (testExecInfoJson != null) {
                            if ((new File(testExecInfoJson)).isFile()) {
                                testExecInfo = new JSONObject(new String(Files.readAllBytes(Paths.get(testExecInfoJson))));
                            } else {
                                abortWithError("file doesnt exist: " + testExecInfoJson);
                            }
                        } else {
                            testExecInfo = new JSONObject();
                        }
                        response = xrayImporterBuilder.build().submitMultipartCloud(reportFormat, reportFile, testExecInfo, testInfo);                  
                    }

                } else {
                    // server/DC

                    app.getxray.xray.XrayResultsImporter.ServerDCBuilder xrayImporterBuilder;
                    if (jiraToken != null) {
                        xrayImporterBuilder = new XrayResultsImporter.ServerDCBuilder(jiraBaseUrl, jiraToken)
                            .withInternalTestProxy(useInternalTestProxy)
                            .withIgnoreSslErrors(ignoreSslErrors)
                            .withTimeout(timeout);
                    } else {
                        xrayImporterBuilder = new XrayResultsImporter.ServerDCBuilder(jiraBaseUrl, jiraUsername, jiraPassword)
                            .withInternalTestProxy(useInternalTestProxy)
                            .withIgnoreSslErrors(ignoreSslErrors)
                            .withTimeout(timeout);
                    }

                    if (testInfoJson==null  && testExecInfoJson==null) {       
                        if (XrayResultsImporter.XRAY_FORMAT.equals(reportFormat) || XrayResultsImporter.CUCUMBER_FORMAT.equals(reportFormat) || XrayResultsImporter.BEHAVE_FORMAT.equals(reportFormat)) {
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
                            if ((new File(testInfoJson)).isFile()) {
                                testInfo = new JSONObject(new String(Files.readAllBytes(Paths.get(testInfoJson))));
                            } else {
                                abortWithError("file doesnt exist: " + testInfoJson);
                            }
                        } else {
                            testInfo = new JSONObject();
                        }
                        if (testExecInfoJson != null) {
                            if ((new File(testExecInfoJson)).isFile()) {
                                testExecInfo = new JSONObject(new String(Files.readAllBytes(Paths.get(testExecInfoJson))));
                            } else {
                                abortWithError("file doesnt exist: " + testExecInfoJson);
                            }
                        } else {
                            testExecInfo = new JSONObject();
                        }
                        response = xrayImporterBuilder.build().submitMultipartServerDC(reportFormat, reportFile, testExecInfo, testInfo);                  
                    }

                }

                getLog().info("response: " + response);
            } catch (Exception ex) {
                getLog().error(ex.getMessage());
                ex.printStackTrace();
                if (isTrue(abortOnError))
                    System.exit(1);
            }

        }

    }

}
