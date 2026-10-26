package app.getxray.xray;

public interface Log {
    Log NOOP = new Log() {
        @Override
        public void debug(String message) {
            // intentionally does nothing; used when no logger is provided
        }

        @Override
        public void error(Throwable t) {
            // intentionally does nothing; used when no logger is provided
        }
    };

    void debug(String message);

    void error(Throwable t);
}
