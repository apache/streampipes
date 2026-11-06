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

package org.apache.streampipes.extensions.connectors.plc.adapter.generic.connection;

import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.monitoring.IExtensionsLogger;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xConnectionSettings;

import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcConnectionFactory;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.apache.plc4x.java.api.value.PlcValue;
import org.apache.plc4x.java.tools.eventpump.TagBatch;
import org.apache.plc4x.java.tools.eventpump.triggers.Trigger;
import org.apache.plc4x.java.utils.cache.PlcConnectionCache;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ContinuousPlcRequestReaderTest {

  private static final String URL = "test://plc";
  private static final Map<String, String> NODES = Map.of("value", "%DB1:0:INT");
  private final IEventCollector collector = mock(IEventCollector.class);
  private final IExtensionsLogger logger = mock(IExtensionsLogger.class);
  private final Trigger trigger = mock(Trigger.class);
  private final PlcConnectionFactory factory = mock(PlcConnectionFactory.class);

  @Test
  void rebuildsRequestsForEveryLeaseIncludingAfterReconnection() throws Exception {
    var firstRequest = request(CompletableFuture.completedFuture(response()));
    var secondRequest = request(CompletableFuture.completedFuture(response()));
    var first = connection(firstRequest);
    var second = connection(secondRequest);
    when(factory.getConnection(URL)).thenReturn(first, second);
    try (var cache = cache(); var reader = reader(cache)) {
      var listener = start(reader);
      listener.onTrigger(trigger);
      listener.onTrigger(trigger);
      assertEquals(0, cache.getActiveLeaseCount());
      verify(first, times(2)).readRequestBuilder();
      verify(firstRequest, times(2)).execute();
      verify(factory).getConnection(URL);

      cache.removeCachedConnection(URL);
      listener.onTrigger(trigger);
      verify(secondRequest).execute();
      verify(collector, times(3)).collect(Map.of("value", 42));
      assertEquals(0, cache.getActiveLeaseCount());
      reader.close();
      reader.close();
      verify(trigger).close();
      verify(second, never()).close();
    }
    verify(second).close();
  }

  @Test
  void skipsOverlappingFetchesAndSuppressesResponsesAfterStop() throws Exception {
    var pending = new CompletableFuture<PlcReadResponse>();
    var request = request(pending);
    useConnection(request);
    try (var cache = cache(); var reader = reader(cache)) {
      var listener = start(reader);
      listener.onTrigger(trigger);
      listener.onTrigger(trigger);
      listener.onTrigger(trigger);
      verify(request).execute();
      verify(logger).warn(anyString(), anyString());
      assertEquals(1, cache.getActiveLeaseCount());
      reader.close();
      pending.complete(response());
      assertEquals(0, cache.getActiveLeaseCount());
      verify(collector, never()).collect(any());
      listener.onTrigger(trigger);
      verify(factory).getConnection(URL);
    }
  }

  @Test
  void reportsFailuresOnceAndReportsRecoveryWithoutExposingCredentials() throws Exception {
    var request = request(CompletableFuture.completedFuture(response()));
    useConnection(request);
    try (var cache = cache(); var reader = reader(cache)) {
      var listener = start(reader);
      var batch = mock(TagBatch.class);
      reader.onError(batch, new IllegalStateException("test://plc?password=secret"));
      reader.onError(batch, new IllegalStateException("test://plc?password=secret"));
      var error = ArgumentCaptor.forClass(Exception.class);
      verify(logger).error(error.capture());
      assertFalse(error.getValue().getMessage().contains("password"));
      assertFalse(error.getValue().getMessage().contains("secret"));
      listener.onTrigger(trigger);
      verify(logger).info(anyString(), anyString());
      verify(collector).collect(Map.of("value", 42));
    }
  }

  @Test
  void boundsStalledReadsAndReturnsTheirLease() throws Exception {
    var pending = new CompletableFuture<PlcReadResponse>();
    useConnection(request(pending));
    try (var cache = cache(); var reader = reader(cache)) {
      start(reader).onTrigger(trigger);
      verify(logger, timeout(6500)).error(any(Exception.class));
      // Acquiring the next lease proves the timed-out fetch returned its lease.
      try (var connection = cache.getConnection(URL)) {
        assertTrue(connection.isConnected());
      }
      assertEquals(0, cache.getActiveLeaseCount());
      pending.complete(response());
      verify(collector, never()).collect(any());
    }
  }

  @Test
  void forwardsFetchFailuresAndBacksOffBeforeRetrying() throws Exception {
    var request = request(CompletableFuture.failedFuture(new IllegalStateException("Read failed")));
    useConnection(request);
    try (var cache = cache(); var reader = reader(cache)) {
      var listener = start(reader);
      listener.onTrigger(trigger);
      verify(logger).error(any(Exception.class));
      assertEquals(0, cache.getActiveLeaseCount());
      listener.onTrigger(trigger);
      verify(factory).getConnection(URL);
      verify(collector, never()).collect(any());
    }
  }

  @Test
  void timerPollsAtTheConfiguredIntervalAndStopsOnClose() throws Exception {
    useConnection(request(CompletableFuture.completedFuture(response())));
    var received = new CountDownLatch(2);
    doAnswer(invocation -> {
      received.countDown();
      return null;
    }).when(collector).collect(any());
    try (var cache = cache(); var reader = new ContinuousPlcRequestReader(cache,
        new Plc4xConnectionSettings(URL, 20, NODES), collector, "adapter-id", logger)) {
      reader.start();
      assertTrue(received.await(2, TimeUnit.SECONDS));
      reader.close();
      org.mockito.Mockito.clearInvocations(collector);
      verify(collector, after(100).never()).collect(any());
    }
  }

  @Test
  void propagatesStartupFailureAndClosesTrigger() {
    doThrow(new IllegalStateException("Cannot start")).when(trigger).start(any());
    try (var reader = reader(factory)) {
      assertThrows(IllegalStateException.class, reader::start);
      verify(trigger).close();
    }
  }

  private ContinuousPlcRequestReader reader(PlcConnectionFactory connectionFactory) {
    return new ContinuousPlcRequestReader(connectionFactory, new Plc4xConnectionSettings(URL, 100, NODES),
        collector, "adapter-id", logger, trigger);
  }

  private Trigger.TriggerListener start(ContinuousPlcRequestReader reader) {
    reader.start();
    var listener = ArgumentCaptor.forClass(Trigger.TriggerListener.class);
    verify(trigger).start(listener.capture());
    return listener.getValue();
  }

  private PlcConnectionCache cache() {
    return PlcConnectionCache.getBuilder().withConnectionFactory(factory)
        .withIdlePingThreshold(1, TimeUnit.DAYS)
        .withMaxWaitTime(1, TimeUnit.SECONDS).build();
  }

  private void useConnection(PlcReadRequest request) throws Exception {
    var connection = connection(request);
    when(factory.getConnection(URL)).thenReturn(connection);
  }

  private PlcConnection connection(PlcReadRequest request) {
    var connection = mock(PlcConnection.class);
    var builder = mock(PlcReadRequest.Builder.class);
    when(connection.isConnected()).thenReturn(true);
    when(connection.readRequestBuilder()).thenReturn(builder);
    when(builder.addTagAddress("value", NODES.get("value"))).thenReturn(builder);
    when(builder.build()).thenReturn(request);
    return connection;
  }

  private PlcReadRequest request(CompletableFuture<PlcReadResponse> future) {
    var request = mock(PlcReadRequest.class);
    when(request.execute()).thenAnswer(invocation -> future);
    return request;
  }

  private PlcReadResponse response() {
    var response = mock(PlcReadResponse.class);
    var values = mock(PlcValue.class);
    when(response.getResponseCode("value")).thenReturn(PlcResponseCode.OK);
    when(response.getAsPlcValue()).thenReturn(values);
    when(values.getValue("value")).thenReturn(mock(PlcValue.class));
    when(response.getObject("value")).thenReturn(42);
    return response;
  }
}
