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

package org.apache.streampipes.dataexplorer.commons.influx;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class InfluxClientProviderTest {

  private InfluxDB influxDBMock;
  private InfluxClientProvider influxClientProvider;

  private static final String DATABASE_NAME = "testDB";

  @Before
  public void setUp() {
    influxDBMock = mock(InfluxDB.class);
    influxClientProvider = new InfluxClientProvider();
  }

  @Test
  public void createDatabaseSuccess() {
    var expectedQuery = new Query("CREATE DATABASE test", "");

    influxClientProvider.createDatabase(influxDBMock, "test");

    var pointArgumentCaptor = ArgumentCaptor.forClass(Query.class);
    verify(influxDBMock).query(pointArgumentCaptor.capture());
    var actualQuery = pointArgumentCaptor.getValue();

    assertEquals(actualQuery.getDatabase(), expectedQuery.getDatabase());
  }

  @Test(expected = SpRuntimeException.class)
  public void createDatabaseIlligalName() {
    influxClientProvider.createDatabase(influxDBMock, "test%^$");
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

    when(influxDBMock.query(any())).thenReturn(queryResult);

    return influxClientProvider.databaseExists(influxDBMock, DATABASE_NAME);
  }

  private QueryResult getQueryResultWithDatabaseNames(List<Object> databaseNames) {
    var queryResult = mock(QueryResult.class);
    var result = mock(QueryResult.Result.class);
    when(queryResult.getResults()).thenReturn(List.of(result));
    var series = mock(QueryResult.Series.class);
    when(result.getSeries()).thenReturn(List.of(series));

    List<List<Object>> values = List.of(databaseNames);
    when(series.getValues()).thenReturn(values);
    return queryResult;
  }
}