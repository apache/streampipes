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

package org.apache.streampipes.dataexplorer.management;

import org.apache.streampipes.dataexplorer.api.IDatasetMetadataManagement;
import org.apache.streampipes.dataexplorer.api.query.DatasetId;
import org.apache.streampipes.dataexplorer.api.query.DatasetQuery;
import org.apache.streampipes.dataexplorer.api.query.DatasetQueryBackend;
import org.apache.streampipes.dataexplorer.api.query.DatasetQueryCursor;
import org.apache.streampipes.dataexplorer.api.query.QueryBatch;
import org.apache.streampipes.dataexplorer.api.query.QueryExecutionOptions;
import org.apache.streampipes.dataexplorer.api.query.QuerySpec;
import org.apache.streampipes.dataexplorer.api.query.UnsupportedQueryException;
import org.apache.streampipes.dataexplorer.export.ConfiguredOutputWriter;
import org.apache.streampipes.dataexplorer.influx.InfluxQueryCompiler;
import org.apache.streampipes.dataexplorer.iotdb.IotDbQueryCompiler;
import org.apache.streampipes.model.dataset.AggregationFunction;
import org.apache.streampipes.model.dataset.DataLakeQueryOrdering;
import org.apache.streampipes.model.dataset.DatasetMetadata;
import org.apache.streampipes.model.dataset.SpQueryStatus;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DatasetQueryServiceTest {
  private final IDatasetMetadataManagement catalog = mock(IDatasetMetadataManagement.class);
  private final DatasetQueryBackend backend = mock(DatasetQueryBackend.class, org.mockito.Mockito.CALLS_REAL_METHODS);
  private final DatasetMetadata dataset = dataset();

  private DatasetQueryService service() {
    when(catalog.getById("catalog-id")).thenReturn(dataset);
    when(catalog.getExistingMeasureByName("physical")).thenReturn(Optional.of(dataset));
    when(backend.capabilities()).thenReturn(new InfluxQueryCompiler("db").capabilities());
    return new DatasetQueryService(catalog, backend);
  }

  @Test
  void closingApplicationServicesReleasesTheBackend() {
    var services = new DatasetServices(service(), null, null);
    services.close();
    verify(backend).close();
  }

  @Test
  void resolvesCatalogIdentityAndMergesChunksWithoutMergingDifferentSeries() {
    var service = service();
    var cursor = new Cursor(List.of(batch("a", 3, 3), batch("b", 3), batch("a", 2)));
    when(backend.open(eq(dataset), any())).thenReturn(cursor);
    var query = QuerySpec.builder().select("value").orderBy(DataLakeQueryOrdering.DESC).build();
    var result = service.query(new DatasetQuery(new DatasetId("catalog-id"), query), QueryExecutionOptions.defaults());
    assertEquals(4, result.getTotal());
    assertEquals(2, result.getAllDataSeries().size());
    assertEquals(Map.of("machine", "a"), result.getAllDataSeries().getFirst().getTags());
    assertEquals(Map.of("machine", "b"), result.getAllDataSeries().get(1).getTags());
    assertEquals(List.of(List.of(3L, 1), List.of(3L, 1), List.of(2L, 1)),
        result.getAllDataSeries().getFirst().getRows());
    assertTrue(cursor.closed);
    verify(catalog).getById("catalog-id");
    verify(backend).open(dataset, query);
  }

  @Test
  void ungroupedChunksReturnNullTagsForTheChartFacingResponse() {
    var service = service();
    var cursor = new Cursor(List.of(
        new QueryBatch(List.of("time", "value"), null, List.of(List.of(1L, 10))),
        new QueryBatch(List.of("time", "value"), Map.of(), List.of(List.of(2L, 20)))));
    when(backend.open(eq(dataset), any())).thenReturn(cursor);

    var result = service.queryByName("physical", QuerySpec.builder().select("value").build(),
        QueryExecutionOptions.defaults());

    assertEquals(2, result.getTotal());
    assertEquals(1, result.getAllDataSeries().size());
    assertNull(result.getAllDataSeries().getFirst().getTags());
    assertEquals(List.of(List.of(1L, 10), List.of(2L, 20)), result.getAllDataSeries().getFirst().getRows());
    assertTrue(cursor.closed);
  }

  @Test
  void globalMaximumStopsBeforeReadingFurtherBatchesEvenWithPerSeriesLimit() {
    var service = service();
    var cursor = new Cursor(List.of(batch("a", 1, 2), batch("b", 3, 4), batch("c", 5)));
    when(backend.open(eq(dataset), any())).thenReturn(cursor);
    var result = service.queryByName("physical", QuerySpec.builder().select("*").limit(2).build(),
        new QueryExecutionOptions(false, OptionalInt.of(2), false));
    assertEquals(SpQueryStatus.TOO_MUCH_DATA, result.getSpQueryStatus());
    assertEquals(3, result.getTotal());
    assertEquals(2, cursor.read);
    assertTrue(cursor.closed);
  }

  @Test
  void preservesNullsOrFiltersRowsBeforeCountingTheLimit() {
    var service = service();
    var rows = new QueryBatch(List.of("time", "value"), Map.of(),
        List.of(Arrays.asList(1L, null), List.of(2L, 3)));
    when(backend.open(eq(dataset), any())).thenAnswer(invocation -> new Cursor(List.of(rows)));
    var spec = QuerySpec.builder().select("value").build();
    assertNull(service.queryByName("physical", spec, QueryExecutionOptions.defaults())
        .getAllDataSeries().getFirst().getRows().getFirst().get(1));
    assertEquals(1, service.queryByName("physical", spec,
        new QueryExecutionOptions(true, OptionalInt.of(1), false)).getTotal());
  }

  @Test
  void plansAggregationThroughTypedQueriesAndRetainsUserPagination() {
    var service = service();
    when(backend.open(eq(dataset), any())).thenReturn(new Cursor(List.of(batch("", 1000))),
        new Cursor(List.of(batch("", 10000))), new Cursor(List.of(batch("", 1, 2, 3, 4, 5, 6))),
        new Cursor(List.of(batch("", 1000))));
    var original = QuerySpec.builder().aggregate("value", AggregationFunction.MEAN, "average")
        .groupBy("machine").limit(50).offset(100).orderBy(DataLakeQueryOrdering.DESC).build();
    service.queryByName("physical", original, new QueryExecutionOptions(false, OptionalInt.of(5), true,
        Optional.of(new QuerySpec.Fill(QuerySpec.FillMode.PREVIOUS, Optional.empty()))));
    var captor = ArgumentCaptor.forClass(QuerySpec.class);
    verify(backend, times(4)).open(eq(dataset), captor.capture());
    var calls = captor.getAllValues();
    assertEquals(1, calls.get(0).limit().orElseThrow());
    assertEquals(DataLakeQueryOrdering.DESC, calls.get(1).ordering().orElseThrow());
    assertEquals(6, calls.get(2).limit().orElseThrow());
    assertTrue(calls.getFirst().dimensions().isEmpty());
    assertTrue(calls.getFirst().offset().isEmpty());
    assertTrue(calls.getFirst().projections().getFirst().aggregation().isEmpty());
    var planned = calls.getLast();
    assertEquals("1801ms", planned.timeBucket().orElseThrow().interval().value());
    assertEquals(QuerySpec.FillMode.PREVIOUS, planned.fill().orElseThrow().mode());
    assertEquals(original.limit(), planned.limit());
    assertEquals(original.offset(), planned.offset());
    assertEquals(original.dimensions(), planned.dimensions());
    assertTrue(original.timeBucket().isEmpty());
  }

  @Test
  void rejectsUnsupportedAutoAggregationBeforeOpeningDatabaseCursor() {
    var service = service();
    when(backend.capabilities()).thenReturn(new IotDbQueryCompiler().capabilities());
    assertThrows(UnsupportedQueryException.class, () -> service.queryByName("physical",
        QuerySpec.builder().aggregate("value", AggregationFunction.MEAN).build(),
        new QueryExecutionOptions(false, OptionalInt.empty(), true)));
    verify(backend, never()).open(any(), any());
  }

  @Test
  void rawQueriesDoNotIssueAutoAggregationHelperQueries() {
    var service = service();
    when(backend.open(eq(dataset), any())).thenReturn(new Cursor(List.of()));
    service.queryByName("physical", QuerySpec.builder().select("*").build(),
        new QueryExecutionOptions(false, OptionalInt.empty(), true));
    verify(backend).open(eq(dataset), any());
  }

  @Test
  void latestTimestampsUseBackendBatchingAndOnlyFallbackForMissingDatasets() {
    var service = service();
    when(backend.latestTimestamps(List.of(dataset))).thenReturn(Map.of("catalog-id", 123L));
    assertEquals(Map.of("physical", 123L), service.getLatestTimestamps(List.of("physical")));
    verify(backend, never()).open(any(), any());
    var dimension = new EventPropertyPrimitive();
    dimension.setRuntimeName("machine");
    dimension.setPropertyScope(PropertyScope.DIMENSION_PROPERTY.name());
    var value = new EventPropertyPrimitive();
    value.setRuntimeName("value");
    when(dataset.getEventSchema()).thenReturn(new EventSchema(List.of(dimension, value)));
    when(backend.latestTimestamps(List.of(dataset))).thenReturn(Map.of());
    when(backend.open(eq(dataset), any())).thenReturn(new Cursor(List.of(batch("a", 7), batch("b", 9))));
    long before = System.currentTimeMillis();
    assertEquals(Map.of("physical", 9L), service.getLatestTimestamps(List.of("physical")));
    long after = System.currentTimeMillis();
    var captured = ArgumentCaptor.forClass(QuerySpec.class);
    verify(backend).open(eq(dataset), captured.capture());
    var spec = captured.getValue();
    assertEquals(List.of(new QuerySpec.Projection("value", Optional.empty(), Optional.empty())), spec.projections());
    assertEquals(new QuerySpec.TimestampComparison(QuerySpec.Operator.GT, 0L), spec.predicates().getFirst());
    var upper = (QuerySpec.TimestampComparison) spec.predicates().get(1);
    assertEquals(QuerySpec.Operator.LT, upper.operator());
    assertTrue(upper.epochMillis() >= before && upper.epochMillis() <= after);
    assertEquals(OptionalInt.of(1), spec.limit());
    assertEquals(Optional.of(DataLakeQueryOrdering.DESC), spec.ordering());
  }

  @Test
  void latestTimestampWithoutAStoredFieldDoesNotFallBackToWildcard() {
    var service = service();
    when(backend.latestTimestamps(List.of(dataset))).thenReturn(Map.of());
    assertEquals(Map.of("physical", 0L), service.getLatestTimestamps(List.of("physical")));
    verify(backend, never()).open(any(), any());
  }

  @Test
  void exportWritesAllSeriesAndDuplicateTimestampsInCursorOrder() throws Exception {
    var service = service();
    var cursor = new Cursor(List.of(batch("a", 3, 3), batch("b", 3), batch("a", 2)));
    when(backend.open(eq(dataset), any())).thenReturn(cursor);
    var writer = mock(ConfiguredOutputWriter.class);
    var output = new ByteArrayOutputStream();
    var spec = QuerySpec.builder().select("value").orderBy(DataLakeQueryOrdering.DESC).limit(10).build();
    new DatasetExportService(service).exportByName("physical", spec, QueryExecutionOptions.defaults(), d -> writer, output);
    var rows = ArgumentCaptor.forClass(List.class);
    verify(writer, times(4)).writeItem(eq(output), rows.capture(), eq(List.of("timestamp", "value", "machine")), anyBoolean());
    assertEquals(List.of(List.of(3L, 1, "a"), List.of(3L, 1, "a"), List.of(3L, 1, "b"),
        List.of(2L, 1, "a")), rows.getAllValues());
    verify(backend).open(dataset, spec);
    verify(writer).afterLastItem(output);
    assertTrue(cursor.closed);
  }

  @Test
  void exportClosesCursorWhenTheClientDisconnects() throws Exception {
    var service = service();
    var cursor = new Cursor(List.of(batch("a", 3), batch("a", 2)));
    when(backend.open(eq(dataset), any())).thenReturn(cursor);
    var writer = mock(ConfiguredOutputWriter.class);
    doThrow(new IOException("Disconnected")).when(writer).writeItem(any(), any(), any(), anyBoolean());
    assertThrows(IOException.class, () -> new DatasetExportService(service).exportByName("physical",
        QuerySpec.builder().select("*").build(), QueryExecutionOptions.defaults(), d -> writer,
        new ByteArrayOutputStream()));
    assertTrue(cursor.closed);
    assertEquals(1, cursor.read);
  }

  @Test
  void unknownProviderFailsInsteadOfSilentlySelectingInflux() {
    assertThrows(IllegalArgumentException.class, () -> new DataExplorerDispatcher("typo", Map.of()));
  }

  private static DatasetMetadata dataset() {
    var dataset = mock(DatasetMetadata.class);
    when(dataset.getElementId()).thenReturn("catalog-id");
    when(dataset.getMeasureName()).thenReturn("physical");
    when(dataset.getTimestampFieldName()).thenReturn("timestamp");
    return dataset;
  }

  private static QueryBatch batch(String machine, long... times) {
    List<List<Object>> rows = Arrays.stream(times).mapToObj(time -> List.<Object>of(time, 1)).toList();
    return new QueryBatch(List.of("time", "value"), Map.of("machine", machine), rows);
  }

  private static final class Cursor implements DatasetQueryCursor {
    private final Iterator<QueryBatch> batches;
    private boolean closed;
    private int read;

    private Cursor(List<QueryBatch> batches) {
      this.batches = batches.iterator();
    }

    @Override
    public boolean hasNext() {
      return batches.hasNext();
    }

    @Override
    public QueryBatch next() {
      read++;
      return batches.next();
    }

    @Override
    public void close() {
      closed = true;
    }
  }
}
