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
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link StatementHandler}.
 */
class StatementHandlerTest {

  private DbDescription dbDescription;
  private Connection connection;
  private PreparedStatement preparedStatement;
  private TableDescription tableDescription;
  private StatementHandler statementHandler;
  private Map<String, Object> event;

  @BeforeEach
  void setUp() throws SQLException {
    dbDescription = mock(DbDescription.class);
    when(dbDescription.getAllowedRegEx()).thenReturn("^[a-zA-Z_][a-zA-Z0-9_]*$");
    when(dbDescription.getEngine()).thenReturn(SupportedDbEngines.POSTGRESQL);
    when(dbDescription.isColumnNameQuoted()).thenReturn(false);

    preparedStatement = mock(PreparedStatement.class);
    connection = mock(Connection.class);
    when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

    tableDescription = new TableDescription("sensor_data", new EventSchema());
    statementHandler = new StatementHandler(null, null);

    event = new LinkedHashMap<>();
    event.put("temperature", 21.5);
    event.put("humidity", 60L);
  }

  private void addSampleEventTimes(int count) throws SQLException {
    for (int i = 0; i < count; i++) {
      statementHandler.addToBatch(dbDescription, tableDescription, connection, event);
    }
  }

  @Test
  void testAddToBatch_threeEvents_addsEachToTheBatchAndCountsThem() throws SQLException {
    addSampleEventTimes(3);

    verify(preparedStatement, times(3)).addBatch();
    verify(preparedStatement, never()).executeBatch();

    int expected = 3;
    int actual = statementHandler.getPendingBatchCount();
    assertEquals(expected, actual, "all three buffered rows should be counted");
  }

  @Test
  void testAddToBatch_threeEvents_preparesTheInsertStatementOnlyOnce() throws SQLException {
    addSampleEventTimes(3);
    verify(connection, times(1)).prepareStatement(anyString());
  }

  @Test
  void testExecuteBatch_threeBufferedRows_sendsThemInOneCallAndResetsTheCount() throws SQLException {
    addSampleEventTimes(3);

    statementHandler.executeBatch();
    verify(preparedStatement, times(1)).executeBatch();

    int expected = 0;
    int actual = statementHandler.getPendingBatchCount();

    assertEquals(expected, actual, "the buffer count should be reset after sending");
  }

  @Test
  void testExecuteBatch_withNothingBuffered_doesNotTouchTheDatabase() throws SQLException {
    statementHandler.preparedStatement = preparedStatement;

    statementHandler.executeBatch();

    verify(preparedStatement, never()).executeBatch();

    int expected = 0;
    int actual = statementHandler.getPendingBatchCount();
    assertEquals(expected, actual, "an empty buffer should stay at zero");
  }
}