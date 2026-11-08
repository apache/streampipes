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

package org.apache.streampipes.manager.runtime;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RateLimitedRuntimeInfoProviderTest {

  private final DataStreamRuntimeInfoProvider fetcher = mock(DataStreamRuntimeInfoProvider.class);
  private final ClientDisconnectCallback cleanup = mock(ClientDisconnectCallback.class);
  private final RateLimitedRuntimeInfoProvider provider = new RateLimitedRuntimeInfoProvider(fetcher, cleanup, 3, 0);

  @Test
  void disconnectStopsStreamingAndCleansUpOnce() throws Exception {
    when(fetcher.getLatestEvents()).thenReturn(Map.of("adapter", Map.of("value", 42)));
    var flusher = mock(Flushable.class);
    doThrow(new IOException("Disconnected")).when(flusher).flush();

    provider.streamOutput(new ByteArrayOutputStream(), flusher);

    verify(fetcher).getLatestEvents();
    verify(flusher).flush();
    verify(fetcher).close();
    verify(cleanup).onClientDisconnect();
  }

  @Test
  void normalExpiryCleansUpEvenWithoutEvents() {
    when(fetcher.getLatestEvents()).thenReturn(Map.of());

    provider.streamOutput(new ByteArrayOutputStream());

    verify(fetcher, times(3)).getLatestEvents();
    verify(fetcher).close();
    verify(cleanup).onClientDisconnect();
  }

  @Test
  void startupFailureCleansUp() {
    var failure = new IllegalStateException("Connection failed");
    doThrow(failure).when(fetcher).startConsuming();

    assertSame(failure, assertThrows(IllegalStateException.class,
        () -> provider.streamOutput(new ByteArrayOutputStream())));

    verify(fetcher).close();
    verify(cleanup).onClientDisconnect();
  }

  @Test
  void eventFailureCleansUp() {
    when(fetcher.getLatestEvents()).thenThrow(new IllegalStateException("Invalid event"));

    assertThrows(IllegalStateException.class, () -> provider.streamOutput(new ByteArrayOutputStream()));

    verify(fetcher).close();
    verify(cleanup).onClientDisconnect();
  }

  @Test
  void consumerCloseFailureStillReleasesPipeline() {
    doThrow(new IllegalStateException("Close failed")).when(fetcher).close();

    assertThrows(IllegalStateException.class, () -> provider.streamOutput(new ByteArrayOutputStream()));

    verify(cleanup).onClientDisconnect();
  }

  @Test
  void interruptionCleansUpAndPreservesInterruptFlag() {
    Thread.currentThread().interrupt();
    try {
      provider.streamOutput(new ByteArrayOutputStream());
      assertTrue(Thread.currentThread().isInterrupted());
      verify(fetcher).close();
      verify(cleanup).onClientDisconnect();
    } finally {
      Thread.interrupted();
    }
  }

  @Test
  void stopsStreamingOnceThePreviewIsNoLongerActive() {
    when(fetcher.getLatestEvents()).thenReturn(Map.of("adapter", Map.of("value", 42)));
    var activeChecks = new AtomicInteger();
    var stoppingProvider = new RateLimitedRuntimeInfoProvider(
        fetcher, cleanup, () -> activeChecks.incrementAndGet() <= 1, 3, 0);

    stoppingProvider.streamOutput(new ByteArrayOutputStream());

    verify(fetcher, times(1)).getLatestEvents();
    verify(fetcher).close();
    verify(cleanup).onClientDisconnect();
  }
}
