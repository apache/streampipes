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

import org.apache.streampipes.extensions.connectors.plc.cache.SpConnectionContainer;
import org.apache.streampipes.extensions.connectors.plc.cache.SpLeasedPlcConnection;

import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcConnectionManager;
import org.apache.plc4x.java.api.authentication.PlcAuthentication;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.messages.PlcBrowseRequest;
import org.apache.plc4x.java.api.messages.PlcPingResponse;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcSubscriptionRequest;
import org.apache.plc4x.java.api.messages.PlcUnsubscriptionRequest;
import org.apache.plc4x.java.api.messages.PlcWriteRequest;
import org.apache.plc4x.java.api.metadata.PlcConnectionMetadata;
import org.apache.plc4x.java.api.model.PlcTag;
import org.apache.plc4x.java.api.value.PlcValue;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConnectionContainerReproTest {

  static class FlakyManager implements PlcConnectionManager {
    final AtomicInteger calls = new AtomicInteger();
    final PlcConnection c1 = new DummyConnection();
    final PlcConnection c2 = new DummyConnection();

    @Override
    public PlcConnection getConnection(String url) throws PlcConnectionException {
      int n = calls.getAndIncrement();
      if (n == 0) return c1;                           // initial success
      if (n == 1) throw new PlcConnectionException("PLC down"); // reconnect fails once
      return c2;                                       // would succeed later
    }

    @Override
    public PlcConnection getConnection(String s, PlcAuthentication plcAuthentication) throws PlcConnectionException {
      return null;
    }
  }

  static class DummyConnection implements PlcConnection {
    @Override
    public void connect() {
    }

    @Override
    public boolean isConnected() {
      return true;
    }

    @Override
    public void close() {
    }

    @Override
    public Optional<PlcTag> parseTagAddress(String s) {
      return Optional.empty();
    }

    @Override
    public Optional<PlcValue> parseTagValue(PlcTag plcTag, Object... objects) {
      return Optional.empty();
    }

    @Override
    public PlcConnectionMetadata getMetadata() {
      return null;
    }

    @Override
    public CompletableFuture<? extends PlcPingResponse> ping() {
      return null;
    }

    @Override
    public PlcReadRequest.Builder readRequestBuilder() {
      return null;
    }

    @Override
    public PlcWriteRequest.Builder writeRequestBuilder() {
      return null;
    }

    @Override
    public PlcSubscriptionRequest.Builder subscriptionRequestBuilder() {
      return null;
    }

    @Override
    public PlcUnsubscriptionRequest.Builder unsubscriptionRequestBuilder() {
      return null;
    }

    @Override
    public PlcBrowseRequest.Builder browseRequestBuilder() {
      return null;
    }
    // implement other methods as no-ops if your interface requires them
  }

  @Test
  void recoversAfterFailedReconnectAndServesNewLeases() throws Exception {
    FlakyManager mgr = new FlakyManager();
    SpConnectionContainer cc = new SpConnectionContainer(
        mgr, "mock://plc",
        Duration.ofSeconds(30), Duration.ofSeconds(30),
        url -> null // closeConnectionHandler
    );

    // 1) First caller gets a lease immediately.
    SpLeasedPlcConnection lease1 =
        (SpLeasedPlcConnection) cc.lease().get(500, TimeUnit.MILLISECONDS);

    // 2) Second caller queues up (does not complete yet).
    Future<PlcConnection> queued = cc.lease();

    // 3) Return with invalidate=true while reconnect will THROW.
    cc.returnConnection(lease1, true);

    // 3a) The queued future should have been completed exceptionally (queue drained).
    assertThrows(ExecutionException.class, () -> queued.get(200, TimeUnit.MILLISECONDS));

    // 4) Now a new lease should succeed quickly (manager will succeed on next call).
    PlcConnection lease2 = cc.lease().get(500, TimeUnit.MILLISECONDS);
    assertNotNull(lease2, "Expected a fresh lease after recovery");
    assertEquals(3, mgr.calls.get(), "Expected 3 getConnection() calls: success, fail, success");

    // 5) Return normally and ensure subsequent leasing still works.
    cc.returnConnection((SpLeasedPlcConnection) lease2, false);
    PlcConnection lease3 = cc.lease().get(500, TimeUnit.MILLISECONDS);
    assertNotNull(lease3);
  }
}
