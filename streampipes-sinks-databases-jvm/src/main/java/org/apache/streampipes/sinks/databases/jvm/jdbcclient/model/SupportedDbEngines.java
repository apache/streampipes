package org.apache.streampipes.sinks.databases.jvm.jdbcclient.model;

public enum SupportedDbEngines {
    MY_SQL("mysql", "com.mysql.cj.jdbc.Driver", ".*"),
    IOT_DB("iotdb","org.apache.iotdb.jdbc.IoTDBDriver", ".*"),
    POSTGRESQL("postgresql", "org.postgresql.Driver", "^[a-zA-Z_][a-zA-Z0-9_]*$");

    private final String urlName;
    private final String driverName;
    private final String allowedRegex;

    SupportedDbEngines(String urlName, String driverName, String allowedRegex) {
        this.urlName = urlName;
        this.driverName = driverName;
        this.allowedRegex = allowedRegex;
    }

    public String getUrlName() {
        return urlName;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getAllowedRegex() {
        return allowedRegex;
    }
}