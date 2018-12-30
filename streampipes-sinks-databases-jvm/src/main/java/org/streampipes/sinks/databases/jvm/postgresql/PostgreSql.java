/*
 * Copyright 2017 FZI Forschungszentrum Informatik
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
import org.streampipes.wrapper.runtime.EventSink;

import java.sql.SQLException;
import java.util.Map;

public class PostgreSql extends EventSink<PostgreSqlParameters> {

  private PostgreSqlClient postgreSqlClient;

  private static Logger LOG;

  public PostgreSql(PostgreSqlParameters params) {
    super(params);
  }

  @Override
  public void bind(PostgreSqlParameters parameters) throws SpRuntimeException {
    LOG = parameters.getGraph().getLogger(PostgreSql.class);

    this.postgreSqlClient = new PostgreSqlClient(
        parameters.getPostgreSqlHost(),
        parameters.getPostgreSqlPort(),
        parameters.getDatabaseName(),
        parameters.getTableName(),
        parameters.getUsername(),
        parameters.getPassword(),
        LOG
    );
  }

  @Override
  public void onEvent(Map<String, Object> event, String sourceInfo) {
    try {
      postgreSqlClient.save(event);
    } catch (SpRuntimeException e) {
      //TODO: error or warn?
      LOG.error(e.getMessage());
      //e.printStackTrace();
    }
  }

  @Override
  public void discard() throws SpRuntimeException {
    try {
      postgreSqlClient.stop();
    } catch (SQLException e) {
      LOG.warn(e.getMessage());
      //e.printStackTrace();
    }
  }
}
