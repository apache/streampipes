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

package org.apache.streampipes.dataexplorer.influx.client;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InfluxClientProviderTest {

  private InfluxDB influxDBMock;
  private InfluxClientProvider influxClientProvider;

  private static final String DATABASE_NAME = "testDB";

  @BeforeEach
  public void setUp() {
    influxDBMock = Mockito.mock(InfluxDB.class);
    influxClientProvider = new InfluxClientProvider();
  }

  @Test
  public void createDatabaseSuccess() {
    var expectedQuery = new Query("CREATE DATABASE test", "");

    influxClientProvider.createDatabase(influxDBMock, "test");

    var pointArgumentCaptor = ArgumentCaptor.forClass(Query.class);
    Mockito.verify(influxDBMock).query(pointArgumentCaptor.capture());
    var actualQuery = pointArgumentCaptor.getValue();

    Assertions.assertEquals(actualQuery.getDatabase(), expectedQuery.getDatabase());
  }

  @Test
  public void createDatabaseIlligalName() {
    assertThrows(SpRuntimeException.class, () -> influxClientProvider.createDatabase(influxDBMock, "test%^$"));
  }

  @Test
  public void dataBaseDoesExist() {
    var actualResult = testDataBaseExists(List.of(DATABASE_NAME));
    assertTrue(actualResult);
  }

  @Test
  public void dataBaseDoesNotExist() {
    var actualResult = testDataBaseExists(List.of());
    assertFalse(actualResult);
  }

  private boolean testDataBaseExists(List<Object> databaseNames) {
    var queryResult = getQueryResultWithDatabaseNames(databaseNames);

    Mockito.when(influxDBMock.query(ArgumentMatchers.any())).thenReturn(queryResult);

    return influxClientProvider.databaseExists(influxDBMock, DATABASE_NAME);
  }

  private QueryResult getQueryResultWithDatabaseNames(List<Object> databaseNames) {
    var queryResult = Mockito.mock(QueryResult.class);
    var result = Mockito.mock(QueryResult.Result.class);
    Mockito.when(queryResult.getResults()).thenReturn(List.of(result));
    var series = Mockito.mock(QueryResult.Series.class);
    Mockito.when(result.getSeries()).thenReturn(List.of(series));

    List<List<Object>> values = List.of(databaseNames);
    Mockito.when(series.getValues()).thenReturn(values);
    return queryResult;
  }
}