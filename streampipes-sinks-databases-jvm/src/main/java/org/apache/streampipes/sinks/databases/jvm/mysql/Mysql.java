/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.sinks.databases.jvm.mysql;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.schema.EventPropertyNested;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.JdbcClient;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.vocabulary.XSD;
import org.apache.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.apache.streampipes.wrapper.runtime.EventSink;
import org.apache.streampipes.model.schema.EventProperty;

import java.sql.*;
import java.util.*;
import java.util.Objects;


public class Mysql extends JdbcClient implements EventSink<MysqlParameters> {

    private MysqlParameters params;
    private static Logger LOG;
    private HashMap<String, Column> tableColumns;
    private List<String> timestampKeys;

    @Override
    public void onInvocation(MysqlParameters params, EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {

        this.params = params;
        this.timestampKeys = new ArrayList<>();
        LOG = params.getGraph().getLogger(Mysql.class);

        initializeJdbc(
                params.getGraph().getInputStreams().get(0).getEventSchema().getEventProperties(),
                params.getHost(),
                params.getPort(),
                params.getDB(),
                params.getTable(),
                params.getUser(),
                params.getPassword(),
                ".*",
                "com.mysql.cj.jdbc.Driver",
                "mysql",
                false,
                LOG);
    }


    @Override
    public void onEvent(Event inputEvent) {
        try {
            save(inputEvent);
        } catch (SpRuntimeException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDetach() throws SpRuntimeException {
        closeAll();
    }


    @Override
    protected void ensureDatabaseExists(String url, String databaseName) throws SpRuntimeException {
        checkRegEx(databaseName, "databasename");
        try {

            st = c.createStatement();
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS " + databaseName + ";");

        } catch (SQLException e) {
            throw new SpRuntimeException(e.getMessage());
        }
        closeAll();
    }


    @Override
    protected void validateTable() throws SpRuntimeException {
        checkConnected();
        extractTableInformation();

        for (EventProperty property : eventProperties) {
            if (this.tableColumns.get(property.getRuntimeName()) != null) {
                if (property instanceof EventPropertyPrimitive) {
                    Column col = this.tableColumns.get(property.getRuntimeName());
                    // Validate SQL-DateTime separately
                    if (property.getDomainProperties() != null && property.getDomainProperties().stream().anyMatch(x ->
                            SO.DateTime.equals(x.toString())) && col.getType().toString().equals("http://www.w3.org/2001/XMLSchema#long")) {
                        this.timestampKeys.add(property.getRuntimeName());
                        continue;
                    } else if (((EventPropertyPrimitive) property).getRuntimeType().equals(col.getType().toString())) {
                        continue;
                    } else {
                        throw new SpRuntimeException("Table '" + tableName + "' does not match the EventProperties");
                    }
                }
            } else {
                throw new SpRuntimeException("Table '" + tableName + "' does not match the EventProperties");
            }
        }
    }


    @Override
    protected void save(final Event event) throws SpRuntimeException {
        checkConnected();

        try {
            Statement statement;
            statement = c.createStatement();
            StringBuilder sb = new StringBuilder("INSERT INTO " + params.getTable() + " (");
            StringBuilder sb2 = new StringBuilder("Values (");

            for (String s : event.getRaw().keySet()) {
                sb.append(s).append(", ");
                if (event.getFieldByRuntimeName(s).getRawValue() instanceof String) {
                    sb2.append("\"").append(event.getFieldByRuntimeName(s).getRawValue().toString()).append("\", ");
                } else {
                    //
                    if (this.timestampKeys.contains(s)) {
                        java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(event.getFieldByRuntimeName(s).getAsPrimitive().getAsLong());
                        sb2.append("\"").append(sqlTimestamp).append("\", ");
                    } else {
                        sb2.append(event.getFieldByRuntimeName(s).getRawValue().toString()).append(", ");
                    }

                }
            }
            // Remove last comma
            sb.setLength(sb.length() - 2);
            sb2.setLength(sb2.length() - 2);

            sb.append(") ").append(sb2).append(")");
            statement.execute(sb.toString());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void createTable() throws SpRuntimeException {
        checkConnected();
        checkRegEx(tableName, "Tablename");

        StringBuilder statement = new StringBuilder("CREATE TABLE ");
        statement.append(tableName).append(" ( ");
        statement.append(extractEventProperties(eventProperties)).append(" )");

        try {
            st.executeUpdate(statement.toString());
        } catch (SQLException e) {
            throw new SpRuntimeException(e.getMessage());
        }
    }


    private void extractTableInformation() throws SpRuntimeException {

        ResultSet resultSet = null;
        tableColumns = new HashMap<String, Column>();

        String query = "SELECT COLUMN_NAME, DATA_TYPE, COLUMN_TYPE FROM "
                + "INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ? AND TABLE_SCHEMA = ? ORDER BY "
                + "ORDINAL_POSITION ASC;";

        try {

            ps = c.prepareStatement(query);
            ps.setString(1, params.getTable());
            ps.setString(2, params.getDB());

            resultSet = ps.executeQuery();

            if (resultSet.next()) {
                do {
                    String columnName = resultSet.getString("COLUMN_NAME");
                    String dataType = resultSet.getString("DATA_TYPE");
                    String columnType = resultSet.getString("COLUMN_TYPE");
                    tableColumns.put(columnName, new Column(dataType, columnType));
                } while (resultSet.next());
            } else {
                throw new SpRuntimeException("Database or Table does not exist.");
            }

        } catch (SQLException e) {
            throw new SpRuntimeException("SqlException: " + e.getMessage()
                    + ", Error code: " + e.getErrorCode()
                    + ", SqlState: " + e.getSQLState());
        } finally {
            try {
                resultSet.close();
            } catch (Exception e) {
            }
        }
    }


    private StringBuilder extractEventProperties(List<EventProperty> properties)
            throws SpRuntimeException {
        return extractEventProperties(properties, "");
    }


    private StringBuilder extractEventProperties(List<EventProperty> properties, String preProperty)
            throws SpRuntimeException {

        StringBuilder s = new StringBuilder();
        String pre = "";
        for (EventProperty property : properties) {

            // Protection against SQL-Injection
            checkRegEx(property.getRuntimeName(), "Column name");

            if (property instanceof EventPropertyNested) {

                // If is is a nested property, recursively extract the required properties
                StringBuilder tmp = extractEventProperties(((EventPropertyNested) property).getEventProperties(),
                        preProperty + property.getRuntimeName() + "_");
                if (tmp.length() > 0) {
                    s.append(pre).append(tmp);
                }
            } else {
                // Adding the name of the property (e.g. "randomString")
                s.append(pre).append(preProperty).append(property.getRuntimeName()).append(" ");

                // Adding the type of the property (e.g. "VARCHAR(255)")
                if (property instanceof EventPropertyPrimitive) {
                    // If domain property is a timestamp
                    if (property.getDomainProperties() != null && property.getDomainProperties().stream().anyMatch(x ->
                       SO.DateTime.equals(x.toString()))) {
                        s.append(SqlAttribute.DATETIME);
                        this.timestampKeys.add(property.getRuntimeName());
                    } else {
                        s.append(SqlAttribute.getFromUri(((EventPropertyPrimitive) property).getRuntimeType()));
                    }
                } else {
                    // Must be an EventPropertyList then
                    s.append(SqlAttribute.getFromUri(XSD._string.toString()));
                }
            }
            pre = ", ";
        }

        return s;
    }
}
