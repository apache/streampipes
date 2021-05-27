package org.apache.streampipes.sinks.databases.jvm.jdbcclient.model;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class StatementHandler {

    public Statement statement;
    public PreparedStatement preparedStatement;

    public StatementHandler(Statement statement, PreparedStatement preparedStatement) {
        this.statement = statement;
        this.preparedStatement = preparedStatement;
    }

    public PreparedStatement getPreparedStatement() {
        return preparedStatement;
    }

    public Statement getStatement() {
        return statement;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }
}
