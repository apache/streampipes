/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.streampipes.sinks.databases.jvm.postgresql;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.logging.api.Logger;
import org.streampipes.sinks.databases.jvm.jdbcclient.JdbcClient;
import org.streampipes.wrapper.runtime.EventSink;

import java.sql.SQLException;
import java.util.Map;

public class PostgreSql extends EventSink<PostgreSqlParameters> {

  private JdbcClient jdbcClient;

  private static Logger LOG;

  public PostgreSql(PostgreSqlParameters params) {
    super(params);
  }

  @Override
  public void bind(PostgreSqlParameters parameters) throws SpRuntimeException {
    LOG = parameters.getGraph().getLogger(PostgreSql.class);

    // get(0) because it is the only input stream of the sink (and not two)
    // See (https://www.postgresql.org/docs/current/sql-syntax-lexical.html#SQL-SYNTAX-IDENTIFIERS)
    // for allowed postgres identifiers (for the regex)
    this.jdbcClient = new JdbcClient(
        parameters.getGraph().getInputStreams().get(0).getEventSchema().getEventProperties(),
        parameters.getPostgreSqlHost(),
        parameters.getPostgreSqlPort(),
        parameters.getDatabaseName(),
        parameters.getTableName(),
        parameters.getUsername(),
        parameters.getPassword(),
        "^[a-zA-Z_][a-zA-Z0-9_]*$",
        "org.postgresql.Driver",
        "postgresql",
        LOG
    );
  }

  @Override
  public void onEvent(Map<String, Object> event, String sourceInfo) {
    try {
      jdbcClient.save(event);
    } catch (SpRuntimeException e) {
      //TODO: error or warn?
      LOG.error(e.getMessage());
      //e.printStackTrace();
    }
  }

  @Override
  public void discard() throws SpRuntimeException {
    jdbcClient.closeAll();
  }
}
