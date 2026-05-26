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

package org.apache.streampipes.sinks.databases.jvm.jdbcclient.model;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.schema.EventSchema;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StatementHandlerTest {

  @Test
  public void testGeneratePreparedStatementSortsAndQuotesColumns() throws SQLException, SpRuntimeException {
    var connection = mock(Connection.class);
    var preparedStatement = mock(PreparedStatement.class);
    when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

    var handler = new StatementHandler(null, null);
    handler.executePreparedStatement(dbDescription(), tableDescription(), connection, event(
        "b", "value",
        "a", 1
    ));

    verify(connection).prepareStatement("INSERT INTO measurements ( \"a\", \"b\" ) VALUES ( ?, ? );");
    verify(preparedStatement).setInt(1, 1);
    verify(preparedStatement).setString(2, "value");
    verify(preparedStatement).executeUpdate();
  }

  @Test
  public void testGeneratePreparedStatementUsesFlattenedNestedNames() throws SQLException, SpRuntimeException {
    var connection = mock(Connection.class);
    var preparedStatement = mock(PreparedStatement.class);
    when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

    var nested = event("temperature", 21.5d);
    var handler = new StatementHandler(null, null);
    handler.executePreparedStatement(dbDescription(), tableDescription(), connection, event("device", nested));

    verify(connection).prepareStatement("INSERT INTO measurements ( \"device_temperature\" ) VALUES ( ? );");
    verify(preparedStatement).setDouble(1, 21.5d);
    verify(preparedStatement).executeUpdate();
  }

  @Test
  public void testGeneratePreparedStatementRejectsInvalidColumnNames() throws SQLException {
    var connection = mock(Connection.class);

    var handler = new StatementHandler(null, null);

    assertThrows(SpRuntimeException.class, () -> handler.executePreparedStatement(
        dbDescription(),
        tableDescription(),
        connection,
        event("1invalid", "value")));
  }

  @Test
  public void testExecutePreparedStatementRegeneratesWhenEventShapeChanges()
      throws SQLException, SpRuntimeException {
    var connection = mock(Connection.class);
    var firstPreparedStatement = mock(PreparedStatement.class);
    var secondPreparedStatement = mock(PreparedStatement.class);
    when(connection.prepareStatement(anyString())).thenReturn(firstPreparedStatement, secondPreparedStatement);

    var handler = new StatementHandler(null, null);
    handler.executePreparedStatement(dbDescription(), tableDescription(), connection, event("a", 1));
    handler.executePreparedStatement(dbDescription(), tableDescription(), connection, event(
        "a", 1,
        "b", 2
    ));

    verify(connection).prepareStatement("INSERT INTO measurements ( \"a\" ) VALUES ( ? );");
    verify(connection).prepareStatement("INSERT INTO measurements ( \"a\", \"b\" ) VALUES ( ?, ? );");
    verify(firstPreparedStatement).close();
    verify(secondPreparedStatement).setInt(1, 1);
    verify(secondPreparedStatement).setInt(2, 2);
  }

  private DbDescription dbDescription() {
    return new DbDescription(new JdbcConnectionParameters(
        null,
        "localhost",
        5432,
        "database",
        "user",
        "password",
        "measurements",
        false,
        "",
        true), SupportedDbEngines.POSTGRESQL);
  }

  private TableDescription tableDescription() {
    return new TableDescription("measurements", new EventSchema());
  }

  private Map<String, Object> event(Object... keyValues) {
    Map<String, Object> event = new LinkedHashMap<>();
    for (int i = 0; i < keyValues.length; i += 2) {
      event.put((String) keyValues[i], keyValues[i + 1]);
    }
    return event;
  }
}
