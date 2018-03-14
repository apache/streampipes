package org.streampipes.logging.api;

public interface Logger {

    void info(String s);

    void trace(String s);

    void debug(String s);

    void error(String s);

    void warn(String s);
}
