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
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TableDescriptionTest {

  @Test
  public void testExtractTableInformationMapsColumnsAndClosesResources()
      throws SQLException, SpRuntimeException {
    var connection = mock(Connection.class);
    var preparedStatement = mock(PreparedStatement.class);
    var resultSet = mock(ResultSet.class);
    when(connection.prepareStatement("query")).thenReturn(preparedStatement);
    when(preparedStatement.executeQuery()).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(true, true, false);
    when(resultSet.getString("COLUMN_NAME")).thenReturn("temperature", "active");
    when(resultSet.getString("DATA_TYPE")).thenReturn("double precision", "boolean");

    var tableDescription = new TableDescription("measurements", new EventSchema());
    tableDescription.extractTableInformation(connection, "query", new String[]{"measurements"});

    verify(preparedStatement).setString(1, "measurements");
    verify(resultSet).close();
    verify(preparedStatement).close();
    assertEquals(DbDataTypes.DOUBLE_PRECISION, tableDescription.getDataTypesHashMap().get("temperature"));
    assertEquals(DbDataTypes.BOOLEAN, tableDescription.getDataTypesHashMap().get("active"));
  }

  @Test
  public void testExtractTableInformationThrowsWhenTableIsMissingAndClosesResources()
      throws SQLException {
    var connection = mock(Connection.class);
    var preparedStatement = mock(PreparedStatement.class);
    var resultSet = mock(ResultSet.class);
    when(connection.prepareStatement("query")).thenReturn(preparedStatement);
    when(preparedStatement.executeQuery()).thenReturn(resultSet);
    when(resultSet.next()).thenReturn(false);

    var tableDescription = new TableDescription("measurements", new EventSchema());

    assertThrows(SpRuntimeException.class,
        () -> tableDescription.extractTableInformation(connection, "query", new String[]{"measurements"}));
    verify(resultSet).close();
    verify(preparedStatement).close();
  }
}
