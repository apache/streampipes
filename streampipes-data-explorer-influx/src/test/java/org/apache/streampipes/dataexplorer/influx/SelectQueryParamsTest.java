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

package org.apache.streampipes.dataexplorer.influx;

import org.apache.streampipes.dataexplorer.param.ProvidedRestQueryParamConverter;
import org.apache.streampipes.dataexplorer.param.SelectQueryParams;
import org.apache.streampipes.dataexplorer.influx.utils.ProvidedQueryParameterBuilder;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SelectQueryParamsTest {

  @Test
  public void testWildcardTimeBoundQuery() {
    var params = ProvidedQueryParameterBuilder.create("abc")
        .withStartDate(1)
        .withEndDate(2)
        .build();

    SelectQueryParams qp = ProvidedRestQueryParamConverter.getSelectQueryParams(params);

    String query = qp.toQuery(DataLakeInfluxQueryBuilder.create("abc")).getCommand();

    assertEquals("SELECT * FROM \"abc\" WHERE (time < 2000000 AND time > 1000000);", query);
  }

  @Test
  public void testSimpleColumnQuery() {
    var params = ProvidedQueryParameterBuilder.create("abc")
        .withStartDate(1)
        .withEndDate(2)
        .withSimpleColumns(Arrays.asList("p1", "p2"))
        .build();

    SelectQueryParams qp = ProvidedRestQueryParamConverter.getSelectQueryParams(params);

    String query = qp.toQuery(DataLakeInfluxQueryBuilder.create("abc")).getCommand();

    assertEquals("SELECT p1,p2 FROM \"abc\" WHERE (time < 2000000 AND time > 1000000);", query);
  }

  @Test
  public void testSimpleColumnQueryWithBooleanFilter() {
    var params = ProvidedQueryParameterBuilder.create("abc")
        .withStartDate(1)
        .withEndDate(2)
        .withSimpleColumns(Arrays.asList("p1", "p2"))
        .withFilter("[p1;=;true]")
        .build();

    SelectQueryParams qp = ProvidedRestQueryParamConverter.getSelectQueryParams(params);

    String query = qp.toQuery(DataLakeInfluxQueryBuilder.create("abc")).getCommand();

    assertEquals("SELECT p1,p2 FROM \"abc\" WHERE (time < 2000000 AND time > 1000000 AND p1 = true);", query);
  }

  @Test
  public void testSimpleColumnQueryWithStringFilter() {
    var params = ProvidedQueryParameterBuilder.create("abc")
        .withStartDate(1)
        .withEndDate(2)
        .withSimpleColumns(Arrays.asList("p1", "p2"))
        .withFilter("[p1;=;def]")
        .build();

    SelectQueryParams qp = ProvidedRestQueryParamConverter.getSelectQueryParams(params);

    String query = qp.toQuery(DataLakeInfluxQueryBuilder.create("abc")).getCommand();

    assertEquals("SELECT p1,p2 FROM \"abc\" WHERE (time < 2000000 AND time > 1000000 AND p1 = 'def');", query);
  }

  @Test
  public void testSimpleColumnQueryWithIntFilter() {
    var params = ProvidedQueryParameterBuilder.create("abc")
        .withStartDate(1)
        .withEndDate(2)
        .withSimpleColumns(Arrays.asList("p1", "p2"))
        .withFilter("[p1;=;1]")
        .build();

    SelectQueryParams qp = ProvidedRestQueryParamConverter.getSelectQueryParams(params);

    String query = qp.toQuery(DataLakeInfluxQueryBuilder.create("abc")).getCommand();

    assertEquals("SELECT p1,p2 FROM \"abc\" WHERE (time < 2000000 AND time > 1000000 AND p1 = 1.0);", query);
  }

  @Test
  public void testSimpleColumnQueryWithFloatFilter() {
    var params = ProvidedQueryParameterBuilder.create("abc")
        .withStartDate(1)
        .withEndDate(2)
        .withSimpleColumns(Arrays.asList("p1", "p2"))
        .withFilter("[p1;>;1.0]")
        .build();

    SelectQueryParams qp = ProvidedRestQueryParamConverter.getSelectQueryParams(params);

    String query = qp.toQuery(DataLakeInfluxQueryBuilder.create("abc")).getCommand();

    assertEquals("SELECT p1,p2 FROM \"abc\" WHERE (time < 2000000 AND time > 1000000 AND p1 > 1.0);", query);
  }

  @Test
  public void testSimpleColumnQueryWithTwoFilters() {
    var params = ProvidedQueryParameterBuilder.create("abc")
        .withStartDate(1)
        .withEndDate(2)
        .withSimpleColumns(Arrays.asList("p1", "p2"))
        .withFilter("[p1;>;1.0],[p2;<;2]")
        .build();

    SelectQueryParams qp = ProvidedRestQueryParamConverter.getSelectQueryParams(params);

    String query = qp.toQuery(DataLakeInfluxQueryBuilder.create("abc")).getCommand();

    assertEquals("SELECT p1,p2 FROM \"abc\" WHERE (time < 2000000 AND time > 1000000 AND p1 > 1.0 AND"
        + " p2 < 2.0);", query);
  }

  @Test
  public void testAggregatedColumn() {
    var params = ProvidedQueryParameterBuilder.create("abc")
        .withStartDate(1)
        .withEndDate(2)
        .withQueryColumns(List.of("[p1;MEAN;p1_mean]"))
        .build();

    SelectQueryParams qp = ProvidedRestQueryParamConverter.getSelectQueryParams(params);

    String query = qp.toQuery(DataLakeInfluxQueryBuilder.create("abc")).getCommand();

    assertEquals("SELECT MEAN(p1) AS p1_mean FROM \"abc\" WHERE (time < 2000000 AND time > 1000000);", query);
  }

  @Test
  public void testAggregatedColumns() {
    var params = ProvidedQueryParameterBuilder.create("abc")
        .withStartDate(1)
        .withEndDate(2)
        .withQueryColumns(Arrays.asList("[p1;MEAN;p1_mean]", "[p2;COUNT;p2_count]"))
        .build();

    SelectQueryParams qp = ProvidedRestQueryParamConverter.getSelectQueryParams(params);

    String query = qp.toQuery(DataLakeInfluxQueryBuilder.create("abc")).getCommand();

    assertEquals("SELECT MEAN(p1) AS p1_mean,COUNT(p2) AS p2_count FROM \"abc\" WHERE (time < 2000000 AND"
        + " time > 1000000);", query);
  }

  @Test
  public void testGroupByTag() {
    var params = ProvidedQueryParameterBuilder.create("abc")
        .withStartDate(1)
        .withEndDate(2)
        .withQueryColumns(Arrays.asList("[p1;MEAN;p1_mean]", "[p2;COUNT;p2_count]"))
        .withGroupBy(List.of("sensorId"))
        .build();

    SelectQueryParams qp = ProvidedRestQueryParamConverter.getSelectQueryParams(params);

    String query = qp.toQuery(DataLakeInfluxQueryBuilder.create("abc")).getCommand();

    assertEquals("SELECT MEAN(p1) AS p1_mean,COUNT(p2) AS p2_count FROM \"abc\" WHERE (time < 2000000 AND"
        + " time > 1000000) GROUP BY sensorId;", query);
  }

  @Test
  public void testGroupByTags() {
    var params = ProvidedQueryParameterBuilder.create("abc")
        .withStartDate(1)
        .withEndDate(2)
        .withQueryColumns(Arrays.asList("[p1;MEAN;p1_mean]", "[p2;COUNT;p2_count]"))
        .withGroupBy(Arrays.asList("sensorId", "sensorId2"))
        .build();

    SelectQueryParams qp = ProvidedRestQueryParamConverter.getSelectQueryParams(params);

    String query = qp.toQuery(DataLakeInfluxQueryBuilder.create("abc")).getCommand();

    assertEquals("SELECT MEAN(p1) AS p1_mean,COUNT(p2) AS p2_count FROM \"abc\" WHERE (time < 2000000 AND"
        + " time > 1000000) GROUP BY sensorId,sensorId2;", query);
  }

}
