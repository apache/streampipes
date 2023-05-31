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

package org.apache.streampipes.sinks.databases.jvm.postgresql;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.JdbcClient;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.model.SupportedDbEngines;

public class PostgreSql extends JdbcClient {

  private PostgreSqlParameters params;

  private Logger log;

  public void onInvocation(PostgreSqlParameters parameters)
      throws SpRuntimeException {

    this.params = parameters;
    this.log = parameters.getGraph().getLogger(PostgreSql.class);

    // get(0) because it is the only input stream of the sink (and not two)
    // See (https://www.postgresql.org/docs/current/sql-syntax-lexical.html#SQL-SYNTAX-IDENTIFIERS)
    // for allowed postgres identifiers (for the regex)
    initializeJdbc(
        parameters.getGraph().getInputStreams().get(0).getEventSchema(),
        parameters,
        SupportedDbEngines.POSTGRESQL,
        this.log);
  }

  @Override
  protected void extractTableInformation() {

    String query = "SELECT * FROM information_schema.columns WHERE table_name = ? ;";

    String[] queryParameter = new String[]{params.getDbTable()};

    this.tableDescription.extractTableInformation(this.statementHandler.preparedStatement, this.connection, query,
        queryParameter);
  }

  public void onEvent(Event event) {
    try {
      save(event);
    } catch (SpRuntimeException e) {
      //TODO: error or warn?
      log.error(e.getMessage());
      //e.printStackTrace();
    }
  }

  public void onDetach() throws SpRuntimeException {
    closeAll();
  }
}
