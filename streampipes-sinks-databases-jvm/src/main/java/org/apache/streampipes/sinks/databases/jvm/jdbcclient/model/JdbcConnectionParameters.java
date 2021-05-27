package org.apache.streampipes.sinks.databases.jvm.jdbcclient.model;

import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.wrapper.params.binding.EventSinkBindingParams;

public class JdbcConnectionParameters extends EventSinkBindingParams {

    private String dbHost;
    private Integer dbPort;
    private String dbName;
    private String username;
    private String password;
    private String dbTable;
    private boolean sslEnabled;
    private String sslFactory;

    public JdbcConnectionParameters(DataSinkInvocation graph,
                                    String dbHost,
                                    Integer dbPort,
                                    String dbName,
                                    String username,
                                    String password,
                                    String dbTable,
                                    boolean sslEnabled,
                                    String sslFactory) {
        super(graph);
        this.dbHost = dbHost;
        this.dbPort = dbPort;
        this.dbName = dbName;
        this.username = username;
        this.password = password;
        this.dbTable = dbTable;
        this.sslEnabled = sslEnabled;
        this.sslFactory = sslFactory;
    }


    public String getDbHost() {
        return dbHost;
    }

    public Integer getDbPort() {
        return dbPort;
    }

    public String getDbName() {
        return dbName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDbTable() {
        return dbTable;
    }

    public boolean isSslEnabled() {
        return sslEnabled;
    }

    public String getSslFactory() {
        return sslFactory;
    }
}
