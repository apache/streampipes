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


public class Mysql extends JdbcClient implements EventSink<MysqlParameters> {

    private MysqlParameters params;
    private final SupportedDbEngines dbEngine = SupportedDbEngines.MY_SQL;

    @Override
    public void onInvocation(MysqlParameters params, EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {

        this.params = params;
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
    protected void extractTableInformation() throws SpRuntimeException {

        String query = "SELECT COLUMN_NAME, DATA_TYPE, COLUMN_TYPE FROM "
                + "INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ? AND TABLE_SCHEMA = ? ORDER BY "
                + "ORDINAL_POSITION ASC;";

        String[] queryParameter = new String[]{params.getDbTable(), params.getDbName()};

        this.tableDescription.extractTableInformation(
                this.statementHandler.preparedStatement, this.connection,
                query, queryParameter);
    }


}


