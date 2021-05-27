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
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.JdbcClient;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.model.SupportedDbEngines;
import org.apache.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.apache.streampipes.wrapper.runtime.EventSink;

import java.sql.*;
import java.util.*;


public class Mysql extends JdbcClient implements EventSink<MysqlParameters> {

    private MysqlParameters params;
    private List<String> timestampKeys;
    private final SupportedDbEngines dbEngine = SupportedDbEngines.MY_SQL;

    @Override
    public void onInvocation(MysqlParameters params, EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {

        this.params = params;
        this.timestampKeys = new ArrayList<>();
        Logger LOG = params.getGraph().getLogger(Mysql.class);

        initializeJdbc(
                params.getGraph().getInputStreams().get(0).getEventSchema(),
                params,
                dbEngine,
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
    protected void ensureDatabaseExists(String databaseName) throws SpRuntimeException {

        String createStatement = "CREATE DATABASE IF NOT EXISTS ";

        ensureDatabaseExists(createStatement, databaseName);

    }

    @Override
    protected void save(final Event event) throws SpRuntimeException {
        checkConnected();

        try {
            Statement statement;
            statement = c.createStatement();
            StringBuilder sb = new StringBuilder("INSERT INTO " + params.getDbTable() + " (");
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
    protected void extractTableInformation() throws SpRuntimeException {

        String query = "SELECT COLUMN_NAME, DATA_TYPE, COLUMN_TYPE FROM "
                + "INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ? AND TABLE_SCHEMA = ? ORDER BY "
                + "ORDINAL_POSITION ASC;";

        String[] queryParameter = new String[]{params.getDbTable(), params.getDbName()};

        extractTableInformation(query, queryParameter);
    }


}


