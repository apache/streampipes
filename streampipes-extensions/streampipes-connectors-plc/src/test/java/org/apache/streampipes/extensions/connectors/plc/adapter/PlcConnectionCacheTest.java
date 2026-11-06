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

package org.apache.streampipes.extensions.connectors.plc.adapter;

import org.apache.streampipes.extensions.connectors.plc.adapter.generic.connection.OneTimePlcRequestReader;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.connection.PlcRequestProvider;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xConnectionSettings;

import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcConnectionFactory;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.metadata.PlcConnectionMetadata;
import org.apache.plc4x.java.utils.cache.PlcConnectionCache;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PlcConnectionCacheTest {

  @Test
  void oneTimeReadsReturnTheirLeaseAndReuseThePhysicalConnection() throws Exception {
    var factory = mock(PlcConnectionFactory.class);
    var connection = mock(PlcConnection.class);
    var metadata = mock(PlcConnectionMetadata.class);
    when(connection.isConnected()).thenReturn(true);
    when(connection.getMetadata()).thenReturn(metadata);
    when(metadata.isReadSupported()).thenReturn(true);
    when(factory.getConnection("test://plc")).thenReturn(connection);
    var request = mock(PlcReadRequest.class);
    when(request.execute()).thenAnswer(invocation -> CompletableFuture.completedFuture(mock(PlcReadResponse.class)));
    var provider = mock(PlcRequestProvider.class);
    when(provider.makeReadRequest(any(), eq(Map.of()))).thenReturn(request);
    try (var cache = PlcConnectionCache.getBuilder().withConnectionFactory(factory)
        .withMaxWaitTime(1, TimeUnit.SECONDS).build()) {
      var reader = new OneTimePlcRequestReader(cache, new Plc4xConnectionSettings("test://plc", 1000, Map.of()), provider);
      assertEquals(Map.of(), reader.readPlcDataSynchronized());
      assertEquals(0, cache.getActiveLeaseCount());
      assertEquals(Map.of(), reader.readPlcDataSynchronized());
      assertEquals(0, cache.getActiveLeaseCount());
      assertEquals(1, cache.getCachedConnectionCount());
      when(provider.makeReadRequest(any(), eq(Map.of()))).thenThrow(new IllegalArgumentException("Invalid tag"));
      assertThrows(IllegalArgumentException.class, reader::readPlcDataSynchronized);
      assertEquals(0, cache.getActiveLeaseCount());
      verify(factory).getConnection("test://plc");
      verify(connection, never()).close();
    }
    verify(connection).close();
  }
}
