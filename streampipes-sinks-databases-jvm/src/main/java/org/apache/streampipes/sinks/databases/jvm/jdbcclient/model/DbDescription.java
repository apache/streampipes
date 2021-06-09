package org.apache.streampipes.sinks.databases.jvm.jdbcclient.model;

public class DbDescription {

    private final JdbcConnectionParameters connectionParameters;
    protected final SupportedDbEngines dbEngine;

    public DbDescription(JdbcConnectionParameters connectionParameters,
                            SupportedDbEngines dbEngine) {
        this.connectionParameters = connectionParameters;
        this.dbEngine = dbEngine;
    }


    public String getAllowedRegEx() {
        return dbEngine.getAllowedRegex();
    }

    public JdbcConnectionParameters getConnectionParameters() {
        return connectionParameters;
    }

    public SupportedDbEngines getEngine() {
        return dbEngine;
    }

    public String getDriverName(){
        return dbEngine.getDriverName();
    }

    public String getHost(){
        return connectionParameters.getDbHost();
    }

    public int getPort(){
        return connectionParameters.getDbPort();
    }

    public String getName(){
        return connectionParameters.getDbName();
    }

    public String getUsername(){
        return connectionParameters.getUsername();
    }

    public String getPassword(){
        return connectionParameters.getPassword();
    }

    public String getSslFactory(){
        return connectionParameters.getSslFactory();
    }

    public boolean isColumnNameQuoted(){
        return connectionParameters.isColumnNameQuoted();
    }

    public boolean isSslEnabled(){
        return connectionParameters.isSslEnabled();
    }
}
