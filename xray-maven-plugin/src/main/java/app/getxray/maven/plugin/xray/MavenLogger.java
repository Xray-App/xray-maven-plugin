package app.getxray.maven.plugin.xray;

import app.getxray.xray.Log;

public class MavenLogger implements Log {
    private final org.apache.maven.plugin.logging.Log mavenLogger;

    public MavenLogger(org.apache.maven.plugin.logging.Log mavenLogger) {
        this.mavenLogger = mavenLogger;
    }

    @Override
    public void debug(String message) {
        mavenLogger.debug(message);
    }

    @Override
    public void error(Throwable e) {
        mavenLogger.error(e);
    }
}
