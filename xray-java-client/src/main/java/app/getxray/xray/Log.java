package app.getxray.xray;

public interface Log {
    void debug(String message);

    void error(Throwable t);
}
