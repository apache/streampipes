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

import org.apache.streampipes.dataexplorer.influx.utils.ProvidedQueryParameterBuilder;
import org.apache.streampipes.dataexplorer.param.ProvidedRestQueryParamConverter;
import org.apache.streampipes.dataexplorer.param.SelectQueryParams;
import org.apache.streampipes.model.datalake.param.ProvidedRestQueryParams;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.apache.streampipes.model.datalake.param.SupportedRestQueryParams.QP_END_DATE;
import static org.apache.streampipes.model.datalake.param.SupportedRestQueryParams.QP_LIMIT;
import static org.apache.streampipes.model.datalake.param.SupportedRestQueryParams.QP_MISSING_VALUE_BEHAVIOUR;
import static org.apache.streampipes.model.datalake.param.SupportedRestQueryParams.QP_ORDER;
import static org.apache.streampipes.model.datalake.param.SupportedRestQueryParams.QP_START_DATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SelectQueryParamsTest {

  @Test
  public void testLatestEventTimestampQuery() {
    var params = new ProvidedRestQueryParams(
        "abc",
        Map.of(
            QP_START_DATE, "0",
            QP_END_DATE, "100",
            QP_LIMIT, "1",
            QP_ORDER, "DESC",
            QP_MISSING_VALUE_BEHAVIOUR, "empty"
        )
    );

    SelectQueryParams qp = ProvidedRestQueryParamConverter.getSelectQueryParams(params);

    String query = qp.toQuery(DataLakeInfluxQueryBuilder.create("abc")).getCommand();

    assertEquals("SELECT * FROM \"abc\" WHERE (time < 100000000 AND time > 0) ORDER BY time DESC LIMIT 1;", query);
  }

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
        + " time > 1000000) GROUP BY \"sensorId\";", query);
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
        + " time > 1000000) GROUP BY \"sensorId\",\"sensorId2\";", query);
  }

  @Test
  public void testGroupByTime() {
    var params = ProvidedQueryParameterBuilder.create("abc")
        .withSimpleColumns(List.of("value"))
        .withTimeInterval("1h")
        .build();

    SelectQueryParams qp = ProvidedRestQueryParamConverter.getSelectQueryParams(params);

    String query = qp.toQuery(DataLakeInfluxQueryBuilder.create("abc")).getCommand();

    assertEquals("SELECT value FROM \"abc\" GROUP BY time(1h) fill(none);", query);
  }

  @Test
  public void testGroupByTimeAndTags() {
    var params = ProvidedQueryParameterBuilder.create("abc")
        .withSimpleColumns(List.of("value"))
        .withTimeInterval("1ms")
        .withGroupBy(List.of("sensorId"))
        .build();

    SelectQueryParams qp = ProvidedRestQueryParamConverter.getSelectQueryParams(params);

    String query = qp.toQuery(DataLakeInfluxQueryBuilder.create("abc")).getCommand();

    assertEquals("SELECT value FROM \"abc\" GROUP BY time(1ms),\"sensorId\" fill(none);", query);
  }

  @Test
  public void testGroupByTimeWithPreviousFill() {
    var params = ProvidedQueryParameterBuilder.create("abc")
        .withSimpleColumns(List.of("value"))
        .withTimeInterval("1h")
        .withFill("previous")
        .build();

    SelectQueryParams qp = ProvidedRestQueryParamConverter.getSelectQueryParams(params);

    String query = qp.toQuery(DataLakeInfluxQueryBuilder.create("abc")).getCommand();

    assertEquals("SELECT value FROM \"abc\" GROUP BY time(1h) fill(previous);", query);
  }

  @Test
  public void testGroupByTimeWithLinearFill() {
    var params = ProvidedQueryParameterBuilder.create("abc")
        .withSimpleColumns(List.of("value"))
        .withTimeInterval("1h")
        .withFill("linear")
        .build();

    SelectQueryParams qp = ProvidedRestQueryParamConverter.getSelectQueryParams(params);

    String query = qp.toQuery(DataLakeInfluxQueryBuilder.create("abc")).getCommand();

    assertEquals("SELECT value FROM \"abc\" GROUP BY time(1h) fill(linear);", query);
  }

  @Test
  public void testGroupByTimeWithNullFill() {
    var params = ProvidedQueryParameterBuilder.create("abc")
        .withSimpleColumns(List.of("value"))
        .withTimeInterval("1h")
        .withFill("null")
        .build();

    SelectQueryParams qp = ProvidedRestQueryParamConverter.getSelectQueryParams(params);

    String query = qp.toQuery(DataLakeInfluxQueryBuilder.create("abc")).getCommand();

    assertEquals("SELECT value FROM \"abc\" GROUP BY time(1h) fill(null);", query);
  }

  @Test
  public void testGroupByTimeWithNumericFill() {
    var params = ProvidedQueryParameterBuilder.create("abc")
        .withSimpleColumns(List.of("value"))
        .withTimeInterval("1h")
        .withFill("12.5")
        .build();

    SelectQueryParams qp = ProvidedRestQueryParamConverter.getSelectQueryParams(params);

    String query = qp.toQuery(DataLakeInfluxQueryBuilder.create("abc")).getCommand();

    assertEquals("SELECT value FROM \"abc\" GROUP BY time(1h) fill(12.5);", query);
  }

  @Test
  public void testGroupByTimeRejectsUnsafeInterval() {
    var params = ProvidedQueryParameterBuilder.create("abc")
        .withSimpleColumns(List.of("value"))
        .withTimeInterval("1h); SHOW MEASUREMENTS --")
        .build();

    assertThrows(IllegalArgumentException.class,
        () -> ProvidedRestQueryParamConverter.getSelectQueryParams(params));
  }

  @Test
  public void testGroupByTimeRejectsUnsafeFill() {
    var params = ProvidedQueryParameterBuilder.create("abc")
        .withSimpleColumns(List.of("value"))
        .withTimeInterval("1h")
        .withFill("previous); DROP MEASUREMENT foo --")
        .build();

    assertThrows(IllegalArgumentException.class,
        () -> ProvidedRestQueryParamConverter.getSelectQueryParams(params));
  }

  @Test
  public void testGroupByRejectsUnsafeIdentifier() {
    var params = ProvidedQueryParameterBuilder.create("abc")
        .withSimpleColumns(List.of("value"))
        .withGroupBy(List.of("sensorId;SHOW"))
        .build();

    assertThrows(IllegalArgumentException.class,
        () -> ProvidedRestQueryParamConverter.getSelectQueryParams(params));
  }

  @Test
  public void testGroupByTimeAndTagsRejectUnsafeInterval() {
    var params = ProvidedQueryParameterBuilder.create("abc")
        .withSimpleColumns(List.of("value"))
        .withGroupBy(List.of("sensorId"))
        .withTimeInterval("1h) INVALID_TOKEN --")
        .build();

    assertThrows(IllegalArgumentException.class,
        () -> ProvidedRestQueryParamConverter.getSelectQueryParams(params));
  }

  @Test
  public void testFilterExpressionOr() {
    var params = ProvidedQueryParameterBuilder.create("abc")
        .withStartDate(1)
        .withEndDate(2)
        .withSimpleColumns(Arrays.asList("p1", "p2"))
        .withFilterExpression(
            "{\"type\":\"group\",\"operator\":\"OR\",\"children\":["
                + "{\"type\":\"condition\",\"field\":\"p1\",\"operator\":\"=\",\"condition\":1},"
                + "{\"type\":\"condition\",\"field\":\"p2\",\"operator\":\"=\",\"condition\":2}"
                + "]}")
        .build();

    SelectQueryParams qp = ProvidedRestQueryParamConverter.getSelectQueryParams(params);

    String query = qp.toQuery(DataLakeInfluxQueryBuilder.create("abc")).getCommand();

    assertEquals("SELECT p1,p2 FROM \"abc\" WHERE (time < 2000000 AND time > 1000000) "
        + "AND (p1 = 1 OR p2 = 2);", query);
  }

  @Test
  public void testFilterExpressionTakesPrecedenceOverLegacyFilter() {
    var params = ProvidedQueryParameterBuilder.create("abc")
        .withStartDate(1)
        .withEndDate(2)
        .withSimpleColumns(Arrays.asList("p1", "p2"))
        .withFilter("[p1;=;1]")
        .withFilterExpression(
            "{\"type\":\"group\",\"operator\":\"OR\",\"children\":["
                + "{\"type\":\"condition\",\"field\":\"p1\",\"operator\":\"=\",\"condition\":1},"
                + "{\"type\":\"condition\",\"field\":\"p2\",\"operator\":\"=\",\"condition\":2}"
                + "]}")
        .build();

    SelectQueryParams qp = ProvidedRestQueryParamConverter.getSelectQueryParams(params);

    String query = qp.toQuery(DataLakeInfluxQueryBuilder.create("abc")).getCommand();

    assertEquals("SELECT p1,p2 FROM \"abc\" WHERE (time < 2000000 AND time > 1000000) "
        + "AND (p1 = 1 OR p2 = 2);", query);
  }

  @Test
  public void testFilterExpressionStringBooleanIsParsedAsBoolean() {
    var params = ProvidedQueryParameterBuilder.create("abc")
        .withStartDate(1)
        .withEndDate(2)
        .withSimpleColumns(Arrays.asList("p1", "p2"))
        .withFilterExpression(
            "{\"type\":\"group\",\"operator\":\"AND\",\"children\":["
                + "{\"type\":\"condition\",\"field\":\"p1\",\"operator\":\"=\",\"condition\":\"true\"}"
                + "]}")
        .build();

    SelectQueryParams qp = ProvidedRestQueryParamConverter.getSelectQueryParams(params);

    String query = qp.toQuery(DataLakeInfluxQueryBuilder.create("abc")).getCommand();

    assertEquals("SELECT p1,p2 FROM \"abc\" WHERE (time < 2000000 AND time > 1000000) "
        + "AND (p1 = true);", query);
  }

  @Test
  public void testFilterExpressionStringNumberIsParsedAsNumber() {
    var params = ProvidedQueryParameterBuilder.create("abc")
        .withStartDate(1)
        .withEndDate(2)
        .withSimpleColumns(Arrays.asList("p1", "p2"))
        .withFilterExpression(
            "{\"type\":\"group\",\"operator\":\"AND\",\"children\":["
                + "{\"type\":\"condition\",\"field\":\"p1\",\"operator\":\"=\",\"condition\":\"1\"}"
                + "]}")
        .build();

    SelectQueryParams qp = ProvidedRestQueryParamConverter.getSelectQueryParams(params);

    String query = qp.toQuery(DataLakeInfluxQueryBuilder.create("abc")).getCommand();

    assertEquals("SELECT p1,p2 FROM \"abc\" WHERE (time < 2000000 AND time > 1000000) "
        + "AND (p1 = 1.0);", query);
  }

}
