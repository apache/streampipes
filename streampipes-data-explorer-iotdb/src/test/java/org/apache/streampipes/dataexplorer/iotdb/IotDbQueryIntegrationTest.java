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

package org.apache.streampipes.dataexplorer.iotdb;

import org.apache.streampipes.dataexplorer.param.RestQuerySpecMapper;
import org.apache.streampipes.model.dataset.DatasetMetadata;
import org.apache.streampipes.model.dataset.SpQueryResult;
import org.apache.streampipes.model.dataset.param.ProvidedRestQueryParams;

import org.apache.iotdb.session.pool.SessionPool;
import org.apache.tsfile.enums.TSDataType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_COLUMNS;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_END_DATE;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_FILTER;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_FILTER_EXPRESSION;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_LIMIT;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_ORDER;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_PAGE;
import static org.apache.streampipes.model.dataset.param.SupportedRestQueryParams.QP_START_DATE;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** Run against an isolated IoTDB 2.0.11 server with -Diotdb.integration=true. */
@EnabledIfSystemProperty(named = "iotdb.integration", matches = "true")
class IotDbQueryIntegrationTest {
  private SessionPool pool;
  private IotDbAdministrationBackend administration;
  private String dataset;

  @BeforeEach
  void prepare() throws Exception {
    pool = new SessionPool.Builder().host(System.getProperty("iotdb.host", "127.0.0.1"))
        .port(Integer.getInteger("iotdb.port", 16667)).user("root").password("root").maxSize(1).build();
    administration = new IotDbAdministrationBackend(pool);
    dataset = "query_test_" + UUID.randomUUID().toString().replace("-", "");
    var path = IotDbQueryCompiler.datasetPath(dataset);
    pool.executeNonQueryStatement("INSERT INTO " + path + "(time,value,enabled,label) VALUES "
        + "(100,10,true,'first'),(200,20,false,'second'),(300,30,true,'third')");
  }

  @AfterEach
  void cleanup() {
    if (pool != null) {
      try {
        if (dataset != null) {
          administration.delete(metadata());
        }
      } finally {
        pool.close();
      }
    }
  }

  @Test
  void readsRawRowsWithExclusiveTimeBounds() {
    var result = query(Map.of(QP_COLUMNS, "value,label", QP_START_DATE, "100", QP_END_DATE, "300"));
    assertEquals(List.of("time", "value", "label"), result.getHeaders());
    assertEquals(1, result.getTotal());
    assertEquals(200L, result.getAllDataSeries().getFirst().getRows().getFirst().getFirst());
    assertEquals("second", result.getAllDataSeries().getFirst().getRows().getFirst().get(2));
  }

  @Test
  void aggregatesAndAliasesAreNormalized() {
    var result = query(Map.of(QP_COLUMNS, "[value;MEAN;average],[value;COUNT;count]"));
    assertEquals(List.of("time", "average", "count"), result.getHeaders());
    assertEquals(20.0, ((Number) result.getAllDataSeries().getFirst().getRows().getFirst().get(1)).doubleValue());
    assertEquals(3L, ((Number) result.getAllDataSeries().getFirst().getRows().getFirst().get(2)).longValue());
  }

  @Test
  void filtersOrdersAndPaginates() {
    var result = query(Map.of(QP_COLUMNS, "value", QP_FILTER, "[enabled;=;true]", QP_ORDER, "DESC", QP_LIMIT, "1"));
    assertEquals(300L, result.getAllDataSeries().getFirst().getRows().getFirst().getFirst());
    // With a one-session pool this also checks that the first query returned its session.
    assertEquals(200L, query(Map.of(QP_COLUMNS, "value", QP_LIMIT, "1", QP_PAGE, "1"))
        .getAllDataSeries().getFirst().getRows().getFirst().getFirst());
  }

  @Test
  void stringsWithQuotesAndNestedFiltersRemainLiteral() throws Exception {
    var labels = List.of("O'Reilly", "C:\\data\\O'Reilly", "quote' OR true --", "末尾\\");
    for (int i = 0; i < labels.size(); i++) {
      var label = labels.get(i);
      pool.insertRecord(IotDbQueryCompiler.datasetPath(dataset), 400L + i,
          List.of("label"), List.of(TSDataType.TEXT), List.of(label));
      var result = query(Map.of(QP_COLUMNS, "label", QP_FILTER, "[label;=;" + label + "]"));
      assertEquals(1, result.getTotal());
      assertEquals(label, result.getAllDataSeries().getFirst().getRows().getFirst().get(1));
    }
    var nested = query(Map.of(QP_COLUMNS, "value", QP_FILTER_EXPRESSION, """
        {"type":"group","operator":"OR","children":[
          {"type":"condition","field":"value","operator":"=","condition":10},
          {"type":"group","operator":"AND","children":[
            {"type":"condition","field":"value","operator":">","condition":20},
            {"type":"condition","field":"enabled","operator":"=","condition":true}
          ]}
        ]}
        """));
    assertEquals(2, nested.getTotal());
  }

  @Test
  void rangeDeletionUsesTheNonQueryApiAndPreservesBounds() {
    org.junit.jupiter.api.Assertions.assertTrue(administration.deleteRange(metadata(), 100L, 300L));
    var rows = query(Map.of(QP_COLUMNS, "value")).getAllDataSeries().getFirst().getRows();
    assertEquals(List.of(100L, 300L), rows.stream().map(List::getFirst).toList());
  }

  private DatasetMetadata metadata() {
    var metadata = new DatasetMetadata();
    metadata.setMeasureName(dataset);
    return metadata;
  }

  private SpQueryResult query(Map<String, String> values) {
    var params = RestQuerySpecMapper.parse(new ProvidedRestQueryParams(dataset, values));
    var metadata = new DatasetMetadata();
    metadata.setElementId("test-dataset-id");
    metadata.setMeasureName(dataset);
    var result = new SpQueryResult();
    try (var cursor = new IotDbQueryBackend(pool).open(metadata, params)) {
      while (cursor.hasNext()) {
        var batch = cursor.next();
        result.setHeaders(batch.columns());
        result.addDataResult(new org.apache.streampipes.model.dataset.DataSeries(batch.rows().size(),
            batch.rows(), batch.columns(), batch.tags()));
        result.setTotal(result.getTotal() + batch.rows().size());
      }
    }
    return result;
  }
}
