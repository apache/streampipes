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

package org.apache.streampipes.sinks.databases.jvm.jdbcclient;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.model.DbDescription;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.model.StatementHandler;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.model.SupportedDbEngines;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.model.TableDescription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for the implementation of the {@link JdbcClient} class.
 */
class JdbcClientTest {

  private static final String ERROR_CODE_UNDEFINED_TABLE = "42P01";
  private static final String TABLE_NAME = "sensor_data";
  private static final Map<String, Object> EVENT = Map.of(
      "temperature", 21.5
  );

  private JdbcClient client;
  private StatementHandler statementHandler;
  private Statement statement;
  private Event event;

  @BeforeEach
  void setUp() {
    statement = mock(Statement.class);
    statementHandler = mock(StatementHandler.class);
    statementHandler.statement = statement;

    client = new JdbcClient();
    client.connection = mock(Connection.class);
    client.dbDescription = newDbDescription();
    client.tableDescription = new TableDescription(TABLE_NAME, new EventSchema());
    client.statementHandler = statementHandler;
    event = newEvent();
  }

  DbDescription newDbDescription() {
    DbDescription dbDescription = mock(DbDescription.class);
    when(dbDescription.getAllowedRegEx()).thenReturn("^[a-zA-Z_][a-zA-Z0-9_]*$");
    when(dbDescription.getEngine()).thenReturn(SupportedDbEngines.POSTGRESQL);
    when(dbDescription.isColumnNameQuoted()).thenReturn(false);
    return dbDescription;
  }

  Event newEvent() {
    Event event = mock(Event.class);
    when(event.getRaw()).thenReturn(EVENT);
    return event;
  }

  @Test
  void testSave_eventIsNull_throwsException() {
    try {
      client.save(null);
      fail("No exception on #save");
    } catch (SpRuntimeException e) {
      assertEquals("event is null", e.getMessage());
    }
  }

  @Test
  void testSave_connectionIsNull_throwsException() {
    client.connection = null;

    try {
      client.save(event);
      fail("No exception on #save");
    } catch (SpRuntimeException e) {
      assertEquals("Connection is not established.", e.getMessage());
    }
  }

  @Test
  void testSave_tableIsMissingAndCreationProhibited_throwsException() throws SQLException {
    client.allowNewTableCreation = false;

    try {
      client.save(event);
      fail("No exception on #save");
    } catch (SpRuntimeException e) {
      assertEquals("Table '" + TABLE_NAME + "' is not available.", e.getMessage());
    }
    verify(statement, never()).executeUpdate(anyString());
  }

  @Test
  void testSave_tableIsMissingAndCreationAllowed_createsTable() throws SQLException {
    client.allowNewTableCreation = true;

    client.save(event);

    verify(statement, times(1)).executeUpdate(anyString());
    verify(statementHandler, times(1)).executePreparedStatement(any(), any(), any(), anyMap());
  }

  @Test
  void testSave_tableExistsAndCreationProhibited_writesIntoExistingTable() throws SQLException {
    client.tableDescription.setTableExists();
    client.allowNewTableCreation = false;
    client.batchSize = 1;

    client.save(event);

    verify(statement, never()).executeUpdate(anyString());
    verify(statementHandler, times(1)).executePreparedStatement(any(), any(), any(), anyMap());
  }

  @Test
  void testSave_tableIsDeletedDuringRuntime_recreatesTableAndRetries() throws SQLException {
    client.tableDescription.setTableExists();
    client.allowNewTableCreation = true;
    client.batchSize = 1;

    doThrow(new SQLException("write failed", ERROR_CODE_UNDEFINED_TABLE))
        .doNothing()
        .when(statementHandler).executePreparedStatement(any(), any(), any(), anyMap());

    client.save(event);

    verify(statement, times(1)).executeUpdate(anyString());
    verify(statementHandler, times(2)).executePreparedStatement(any(), any(), any(), anyMap());
  }

  @Test
  void testSave_tableIsDeletedAndCreationProhibited_throwsException() throws SQLException {
    client.tableDescription.setTableExists();
    client.allowNewTableCreation = false;
    client.batchSize = 1;

    doThrow(new SQLException("write failed", ERROR_CODE_UNDEFINED_TABLE))
        .doNothing()
        .when(statementHandler).executePreparedStatement(any(), any(), any(), anyMap());

    try {
      client.save(event);
      fail("No exception on #save");
    } catch (SpRuntimeException e) {
      assertEquals("write failed", e.getMessage());
    }
    verify(statement, never()).executeUpdate(anyString());
  }

  @Test
  void testSave_tableIsDeletedWhileCollectingEvents_throwsException() throws SQLException {
    client.tableDescription.setTableExists();
    client.allowNewTableCreation = true;
    client.batchSize = 3;

    doThrow(new SQLException("write failed", ERROR_CODE_UNDEFINED_TABLE))
        .when(statementHandler).addToBatch(any(), any(), any(), anyMap());

    try {
      client.save(event);
      fail("No exception on #save");
    } catch (SpRuntimeException e) {
      assertEquals("write failed", e.getMessage());
    }
    verify(statement, never()).executeUpdate(anyString());
  }

  @Test
  void testSave_batchSizeIsOne_sendsOneStatementPerEvent() throws SQLException {
    client.tableDescription.setTableExists();
    client.batchSize = 1;

    client.save(event);

    verify(statementHandler, times(1)).executePreparedStatement(any(), any(), any(), anyMap());
    verify(statementHandler, never()).addToBatch(any(), any(), any(), anyMap());
  }

  @Test
  void testSave_batchIsNotFull_collectsEvent() throws SQLException {
    client.tableDescription.setTableExists();
    client.batchSize = 3;
    when(statementHandler.getPendingBatchCount()).thenReturn(1);

    client.save(event);

    verify(statementHandler, times(1)).addToBatch(any(), any(), any(), anyMap());
    verify(statementHandler, never()).executeBatch();
    verify(statementHandler, never()).executePreparedStatement(any(), any(), any(), anyMap());
  }

  @Test
  void testSave_batchIsFull_sendsCollectedEvents() throws SQLException {
    client.tableDescription.setTableExists();
    client.batchSize = 3;
    when(statementHandler.getPendingBatchCount()).thenReturn(3);

    client.save(event);

    verify(statementHandler, times(1)).addToBatch(any(), any(), any(), anyMap());
    verify(statementHandler, times(1)).executeBatch();
  }
}
