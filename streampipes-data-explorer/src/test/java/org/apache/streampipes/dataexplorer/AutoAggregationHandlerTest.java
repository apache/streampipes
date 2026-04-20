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

package org.apache.streampipes.dataexplorer;

import org.apache.streampipes.dataexplorer.api.IDataExplorerQueryManagement;
import org.apache.streampipes.model.datalake.DataSeries;
import org.apache.streampipes.model.datalake.SpQueryResult;
import org.apache.streampipes.model.datalake.param.ProvidedRestQueryParams;
import org.apache.streampipes.model.datalake.param.SupportedRestQueryParams;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AutoAggregationHandlerTest {

  @Test
  public void testAutoAggregationAddsOneMillisecondBucketsWhenRawRowsFitClientLimit() {
    IDataExplorerQueryManagement queryManagement = mock(IDataExplorerQueryManagement.class);
    ArgumentCaptor<ProvidedRestQueryParams> queryCaptor = ArgumentCaptor.forClass(ProvidedRestQueryParams.class);

    when(queryManagement.getData(any(ProvidedRestQueryParams.class), eq(false)))
        .thenReturn(makeSingleEventResult(1000L))
        .thenReturn(makeSingleEventResult(3001L))
        .thenReturn(makeRowsResult(3));

    ProvidedRestQueryParams result = new AutoAggregationHandler(makeAggregateParams(), queryManagement, false)
        .makeAutoAggregationQueryParams();

    verify(queryManagement, times(3)).getData(queryCaptor.capture(), eq(false));

    List<ProvidedRestQueryParams> capturedQueries = queryCaptor.getAllValues();
    ProvidedRestQueryParams oldestQuery = capturedQueries.get(0);
    assertEquals("1", oldestQuery.getAsString(SupportedRestQueryParams.QP_LIMIT));
    assertNull(oldestQuery.getAsString(SupportedRestQueryParams.QP_ORDER));
    assertEquals("density,mass_flow,sensor_fault_flags",
        oldestQuery.getAsString(SupportedRestQueryParams.QP_COLUMNS));
    assertFalse(oldestQuery.has(SupportedRestQueryParams.QP_AUTO_AGGREGATE));
    assertFalse(oldestQuery.has(SupportedRestQueryParams.QP_GROUP_BY));
    assertFalse(oldestQuery.has(SupportedRestQueryParams.QP_TIME_INTERVAL));
    assertFalse(oldestQuery.has(SupportedRestQueryParams.QP_PAGE));
    assertFalse(oldestQuery.has(SupportedRestQueryParams.QP_OFFSET));

    ProvidedRestQueryParams newestQuery = capturedQueries.get(1);
    assertEquals("DESC", newestQuery.getAsString(SupportedRestQueryParams.QP_ORDER));

    ProvidedRestQueryParams sampleQuery = capturedQueries.get(2);
    assertEquals("10001", sampleQuery.getAsString(SupportedRestQueryParams.QP_LIMIT));

    assertFalse(result.has(SupportedRestQueryParams.QP_AUTO_AGGREGATE));
    assertEquals("1ms", result.getAsString(SupportedRestQueryParams.QP_TIME_INTERVAL));
  }

  @Test
  public void testAutoAggregationComputesSmallestBoundedBucketSizeAboveLimit() {
    IDataExplorerQueryManagement queryManagement = mock(IDataExplorerQueryManagement.class);
    ArgumentCaptor<ProvidedRestQueryParams> queryCaptor = ArgumentCaptor.forClass(ProvidedRestQueryParams.class);

    when(queryManagement.getData(any(ProvidedRestQueryParams.class), eq(true)))
        .thenReturn(makeSingleEventResult(1_000L))
        .thenReturn(makeSingleEventResult(10_000L))
        .thenReturn(makeRowsResult(6));

    ProvidedRestQueryParams result = new AutoAggregationHandler(makeAggregateParamsWithClientLimit(5),
        queryManagement,
        true).makeAutoAggregationQueryParams();

    verify(queryManagement, times(3)).getData(queryCaptor.capture(), eq(true));
    assertEquals("6", queryCaptor.getAllValues().get(2).getAsString(SupportedRestQueryParams.QP_LIMIT));
    assertEquals("1801ms", result.getAsString(SupportedRestQueryParams.QP_TIME_INTERVAL));
    assertFalse(result.has(SupportedRestQueryParams.QP_AUTO_AGGREGATE));
  }

  @Test
  public void testAutoAggregationLeavesSimpleColumnQueryUnchanged() {
    IDataExplorerQueryManagement queryManagement = mock(IDataExplorerQueryManagement.class);

    when(queryManagement.getData(any(ProvidedRestQueryParams.class), eq(true)))
        .thenReturn(makeSingleEventResult(1_000L))
        .thenReturn(makeSingleEventResult(10_000L));

    ProvidedRestQueryParams result = new AutoAggregationHandler(makeSimpleParams(), queryManagement, true)
        .makeAutoAggregationQueryParams();

    assertFalse(result.has(SupportedRestQueryParams.QP_AUTO_AGGREGATE));
    assertFalse(result.has(SupportedRestQueryParams.QP_TIME_INTERVAL));
    verify(queryManagement, times(2)).getData(any(ProvidedRestQueryParams.class), eq(true));
  }

  private ProvidedRestQueryParams makeAggregateParams() {
    return makeAggregateParamsWithClientLimit(10_000);
  }

  private ProvidedRestQueryParams makeAggregateParamsWithClientLimit(int maxEvents) {
    var params = new HashMap<String, String>();
    params.put(SupportedRestQueryParams.QP_START_DATE, "1000");
    params.put(SupportedRestQueryParams.QP_END_DATE, "10000");
    params.put(SupportedRestQueryParams.QP_COLUMNS,
        "[density;MEAN;mean_density],[mass_flow;MEAN;mean_mass_flow],[sensor_fault_flags;MODE;mode_sensor_fault_flags]");
    params.put(SupportedRestQueryParams.QP_AUTO_AGGREGATE, "true");
    params.put(SupportedRestQueryParams.QP_GROUP_BY, "plant");
    params.put(SupportedRestQueryParams.QP_LIMIT, "50");
    params.put(SupportedRestQueryParams.QP_PAGE, "2");
    params.put(SupportedRestQueryParams.QP_OFFSET, "100");
    params.put(SupportedRestQueryParams.QP_ORDER, "DESC");
    params.put(SupportedRestQueryParams.QP_MAXIMUM_AMOUNT_OF_EVENTS, String.valueOf(maxEvents));
    return new ProvidedRestQueryParams("Test", params);
  }

  private ProvidedRestQueryParams makeSimpleParams() {
    var params = new HashMap<String, String>();
    params.put(SupportedRestQueryParams.QP_START_DATE, "1000");
    params.put(SupportedRestQueryParams.QP_END_DATE, "10000");
    params.put(SupportedRestQueryParams.QP_COLUMNS, "density,mass_flow");
    params.put(SupportedRestQueryParams.QP_AUTO_AGGREGATE, "true");
    return new ProvidedRestQueryParams("Test", params);
  }

  private SpQueryResult makeSingleEventResult(long timestamp) {
    SpQueryResult result = new SpQueryResult();
    result.setHeaders(List.of("time", "density"));
    result.addDataResult(new DataSeries(1,
        List.of(List.of(timestamp, 42.0)),
        List.of("time", "density"),
        null));
    result.setTotal(1);
    return result;
  }

  private SpQueryResult makeRowsResult(int rows) {
    SpQueryResult result = new SpQueryResult();
    result.setHeaders(List.of("time", "density"));
    result.addDataResult(new DataSeries(rows,
        java.util.stream.IntStream.range(0, rows)
            .mapToObj(index -> List.of((Object) (long) index, 42.0))
            .toList(),
        List.of("time", "density"),
        null));
    result.setTotal(rows);
    return result;
  }
}
