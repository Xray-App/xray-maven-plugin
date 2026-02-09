package app.getxray.xray;

import java.io.IOException;

public interface Log {
    void debug(String message);

    void error(Throwable t);
}
