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

import org.apache.streampipes.model.schema.EventSchema;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for the implementation of the {@link StatementHandler} class.
 */
class StatementHandlerTest {

  private static final String TABLE_NAME = "sensor_data";
  private static final Map<String, Object> EVENT_TEMPERATURE = Map.of(
      "temperature", 21.5
  );
  private static final Map<String, Object> EVENT_PRESSURE = Map.of(
      "pressure", 1013.2
  );

  private StatementHandler statementHandler;
  private DbDescription dbDescription;
  private TableDescription tableDescription;
  private Connection connection;
  private PreparedStatement preparedStatement;

  @BeforeEach
  void setUp() throws SQLException {
    preparedStatement = mock(PreparedStatement.class);
    connection = mock(Connection.class);
    when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

    dbDescription = mock(DbDescription.class);
    when(dbDescription.getAllowedRegEx()).thenReturn("^[a-zA-Z_][a-zA-Z0-9_]*$");
    when(dbDescription.getEngine()).thenReturn(SupportedDbEngines.POSTGRESQL);
    when(dbDescription.isColumnNameQuoted()).thenReturn(false);

    tableDescription = new TableDescription(TABLE_NAME, new EventSchema());
    statementHandler = new StatementHandler(null, null);
  }

  @Test
  void testAddToBatch_threeEvents_setsPendingBatchCountToThree() throws SQLException {
    statementHandler.addToBatch(dbDescription, tableDescription, connection, EVENT_TEMPERATURE);
    statementHandler.addToBatch(dbDescription, tableDescription, connection, EVENT_TEMPERATURE);
    statementHandler.addToBatch(dbDescription, tableDescription, connection, EVENT_TEMPERATURE);

    verify(preparedStatement, times(3)).addBatch();
    assertEquals(3, statementHandler.getPendingBatchCount());
  }

  @Test
  void testAddToBatch_threeEvents_buildsOneInsertStatement() throws SQLException {
    statementHandler.addToBatch(dbDescription, tableDescription, connection, EVENT_TEMPERATURE);
    statementHandler.addToBatch(dbDescription, tableDescription, connection, EVENT_TEMPERATURE);
    statementHandler.addToBatch(dbDescription, tableDescription, connection, EVENT_TEMPERATURE);

    verify(connection, times(1)).prepareStatement(anyString());
  }

  @Test
  void testAddToBatch_threeEvents_clearsValuesOfPreviousEvent() throws SQLException {
    statementHandler.addToBatch(dbDescription, tableDescription, connection, EVENT_TEMPERATURE);
    statementHandler.addToBatch(dbDescription, tableDescription, connection, EVENT_TEMPERATURE);
    statementHandler.addToBatch(dbDescription, tableDescription, connection, EVENT_TEMPERATURE);

    verify(preparedStatement, times(2)).clearParameters();
  }

  @Test
  void testAddToBatch_eventsChange_doesNotLoseCollectedEvents() throws SQLException {
    statementHandler.addToBatch(dbDescription, tableDescription, connection, EVENT_TEMPERATURE);
    statementHandler.addToBatch(dbDescription, tableDescription, connection, EVENT_TEMPERATURE);
    statementHandler.addToBatch(dbDescription, tableDescription, connection, EVENT_PRESSURE);

    verify(preparedStatement, times(1)).executeBatch();
    verify(connection, times(2)).prepareStatement(anyString());
    assertEquals(1, statementHandler.getPendingBatchCount());
  }

  @Test
  void testExecuteBatch_threeEvents_setsPendingBatchCountToZero() throws SQLException {
    statementHandler.addToBatch(dbDescription, tableDescription, connection, EVENT_TEMPERATURE);
    statementHandler.addToBatch(dbDescription, tableDescription, connection, EVENT_TEMPERATURE);
    statementHandler.addToBatch(dbDescription, tableDescription, connection, EVENT_TEMPERATURE);

    statementHandler.executeBatch();

    verify(preparedStatement, times(1)).executeBatch();
    assertEquals(0, statementHandler.getPendingBatchCount());
  }

  @Test
  void testExecuteBatch_noCollectedEvents_doesNotSendAnything() throws SQLException {
    statementHandler.preparedStatement = preparedStatement;
    statementHandler.executeBatch();
    verify(preparedStatement, never()).executeBatch();
  }

  @Test
  void testExecuteBatch_noStatement_throwsNoException() {
    StatementHandler emptyHandler = new StatementHandler(null, null);
    assertDoesNotThrow(emptyHandler::executeBatch);
  }
}
