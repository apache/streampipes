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

import org.apache.streampipes.extensions.connectors.plc.cache.SpCachedPlcConnectionManager;
import org.apache.streampipes.extensions.connectors.plc.cache.SpConnectionContainer;
import org.apache.streampipes.extensions.connectors.plc.cache.SpLeasedPlcConnection;

import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcConnectionManager;
import org.apache.plc4x.java.api.authentication.PlcAuthentication;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.messages.PlcBrowseRequest;
import org.apache.plc4x.java.api.messages.PlcPingResponse;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.messages.PlcSubscriptionRequest;
import org.apache.plc4x.java.api.messages.PlcUnsubscriptionRequest;
import org.apache.plc4x.java.api.messages.PlcWriteRequest;
import org.apache.plc4x.java.api.metadata.PlcConnectionMetadata;
import org.apache.plc4x.java.api.model.PlcTag;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.apache.plc4x.java.api.value.PlcValue;
import org.apache.plc4x.java.utils.cache.exceptions.PlcConnectionManagerClosedException;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConnectionContainerReproTest {

  static class FlakyManager implements PlcConnectionManager {
    final AtomicInteger calls = new AtomicInteger();
    final PlcConnection c1 = new DummyConnection();
    final PlcConnection c2 = new DummyConnection();

    @Override
    public PlcConnection getConnection(String url) throws PlcConnectionException {
      int n = calls.getAndIncrement();
      if (n == 0) {
        return c1; // initial success
      }
      if (n == 1) {
        throw new PlcConnectionException("PLC down"); // reconnect fails once
      }
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

  static class BlockingCloseConnection extends DummyConnection {
    private final CountDownLatch closeStarted;
    private final CountDownLatch releaseClose;

    BlockingCloseConnection(CountDownLatch closeStarted,
                            CountDownLatch releaseClose) {
      this.closeStarted = closeStarted;
      this.releaseClose = releaseClose;
    }

    @Override
    public void close() {
      closeStarted.countDown();
      try {
        releaseClose.await(5, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  static class MutableConnection extends DummyConnection {
    private final AtomicBoolean connected;
    private final AtomicInteger closeCalls;

    MutableConnection(boolean connected) {
      this.connected = new AtomicBoolean(connected);
      this.closeCalls = new AtomicInteger();
    }

    @Override
    public boolean isConnected() {
      return connected.get();
    }

    @Override
    public void close() {
      closeCalls.incrementAndGet();
    }

    void setConnected(boolean connected) {
      this.connected.set(connected);
    }

    int closeCalls() {
      return closeCalls.get();
    }
  }

  static class RejectingReadConnection extends MutableConnection {

    RejectingReadConnection() {
      super(true);
    }

    @Override
    public PlcReadRequest.Builder readRequestBuilder() {
      return new PlcReadRequest.Builder() {
        @Override
        public PlcReadRequest build() {
          return new PlcReadRequest() {
            @Override
            public CompletableFuture<? extends PlcReadResponse> execute() {
              throw new RejectedExecutionException("transaction executor terminated");
            }

            @Override
            public int getNumberOfTags() {
              return 0;
            }

            @Override
            public LinkedHashSet<String> getTagNames() {
              return new LinkedHashSet<>();
            }

            @Override
            public PlcResponseCode getTagResponseCode(String tagName) {
              return null;
            }

            @Override
            public PlcTag getTag(String name) {
              return null;
            }

            @Override
            public List<PlcTag> getTags() {
              return List.of();
            }
          };
        }

        @Override
        public PlcReadRequest.Builder addTagAddress(String name,
                                                    String tagAddress) {
          return this;
        }

        @Override
        public PlcReadRequest.Builder addTag(String name,
                                             PlcTag tag) {
          return this;
        }
      };
    }
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

  @Test
  void replacesDisconnectedIdleConnection() throws Exception {
    var staleConnection = new MutableConnection(true);
    var managerCalls = new AtomicInteger();
    PlcConnectionManager manager = new PlcConnectionManager() {
      @Override
      public PlcConnection getConnection(String url) {
        if (managerCalls.incrementAndGet() == 1) {
          return staleConnection;
        }
        return new DummyConnection();
      }

      @Override
      public PlcConnection getConnection(String url,
                                         PlcAuthentication authentication) {
        return null;
      }
    };
    SpConnectionContainer connectionContainer = new SpConnectionContainer(
        manager,
        "mock://plc",
        Duration.ofSeconds(30),
        Duration.ofSeconds(30),
        url -> null
    );

    SpLeasedPlcConnection firstLease =
        (SpLeasedPlcConnection) connectionContainer.lease().get(500, TimeUnit.MILLISECONDS);
    connectionContainer.returnConnection(firstLease, false);

    staleConnection.setConnected(false);

    SpLeasedPlcConnection secondLease =
        (SpLeasedPlcConnection) connectionContainer.lease().get(500, TimeUnit.MILLISECONDS);
    assertNotNull(secondLease);
    assertEquals(2, managerCalls.get());
    assertEquals(1, staleConnection.closeCalls());
    connectionContainer.returnConnection(secondLease, false);
    connectionContainer.close();
  }

  @Test
  void doesNotEagerlyReplaceInvalidConnectionWithoutWaitingClient() throws Exception {
    var firstConnection = new MutableConnection(true);
    var secondConnection = new MutableConnection(true);
    var managerCalls = new AtomicInteger();
    PlcConnectionManager manager = new PlcConnectionManager() {
      @Override
      public PlcConnection getConnection(String url) {
        return managerCalls.incrementAndGet() == 1 ? firstConnection : secondConnection;
      }

      @Override
      public PlcConnection getConnection(String url,
                                         PlcAuthentication authentication) {
        return null;
      }
    };
    var connectionContainer = new SpConnectionContainer(
        manager,
        "mock://plc",
        Duration.ofSeconds(30),
        Duration.ofSeconds(30),
        url -> null
    );

    SpLeasedPlcConnection firstLease =
        (SpLeasedPlcConnection) connectionContainer.lease().get(500, TimeUnit.MILLISECONDS);
    connectionContainer.returnConnection(firstLease, true);

    assertEquals(1, managerCalls.get());
    assertEquals(1, firstConnection.closeCalls());

    SpLeasedPlcConnection secondLease =
        (SpLeasedPlcConnection) connectionContainer.lease().get(500, TimeUnit.MILLISECONDS);
    assertEquals(2, managerCalls.get());
    connectionContainer.returnConnection(secondLease, false);
    connectionContainer.close();
  }

  @Test
  void invalidatesLeaseWhenReadExecutionIsRejectedSynchronously() throws Exception {
    var firstConnection = new RejectingReadConnection();
    var managerCalls = new AtomicInteger();
    PlcConnectionManager manager = new PlcConnectionManager() {
      @Override
      public PlcConnection getConnection(String url) {
        return managerCalls.incrementAndGet() == 1 ? firstConnection : new DummyConnection();
      }

      @Override
      public PlcConnection getConnection(String url,
                                         PlcAuthentication authentication) {
        return null;
      }
    };
    var connectionContainer = new SpConnectionContainer(
        manager,
        "mock://plc",
        Duration.ofSeconds(30),
        Duration.ofSeconds(30),
        url -> null
    );

    SpLeasedPlcConnection firstLease =
        (SpLeasedPlcConnection) connectionContainer.lease().get(500, TimeUnit.MILLISECONDS);

    var readRequest = firstLease.readRequestBuilder().build();
    var exception = assertThrows(
        ExecutionException.class,
        () -> readRequest.execute().get(500, TimeUnit.MILLISECONDS)
    );
    assertTrue(exception.getCause() instanceof RejectedExecutionException);

    firstLease.close();

    PlcConnection secondLease = connectionContainer.lease().get(500, TimeUnit.MILLISECONDS);
    assertNotNull(secondLease);
    assertEquals(2, managerCalls.get());
    assertEquals(1, firstConnection.closeCalls());
    secondLease.close();
    connectionContainer.close();
  }

  @Test
  void removingSlowConnectionDoesNotBlockLeasesForOtherUrls() throws Exception {
    var closeStarted = new CountDownLatch(1);
    var releaseClose = new CountDownLatch(1);
    PlcConnectionManager manager = new PlcConnectionManager() {
      @Override
      public PlcConnection getConnection(String url) {
        if ("mock://slow".equals(url)) {
          return new BlockingCloseConnection(closeStarted, releaseClose);
        }
        return new DummyConnection();
      }

      @Override
      public PlcConnection getConnection(String url,
                                         PlcAuthentication authentication) {
        return null;
      }
    };

    var cachedConnectionManager = new SpCachedPlcConnectionManager(
        manager,
        Duration.ofSeconds(30),
        Duration.ofSeconds(30),
        Duration.ofSeconds(30)
    );

    cachedConnectionManager.getConnection("mock://slow");

    ExecutorService removeExecutor = Executors.newSingleThreadExecutor();
    ExecutorService leaseExecutor = Executors.newSingleThreadExecutor();
    try {
      Future<?> removeFuture = removeExecutor.submit(
          () -> cachedConnectionManager.removeCachedConnection("mock://slow"));
      assertTrue(closeStarted.await(500, TimeUnit.MILLISECONDS));

      Future<PlcConnection> otherLease = leaseExecutor.submit(
          () -> cachedConnectionManager.getConnection("mock://other"));
      assertNotNull(otherLease.get(500, TimeUnit.MILLISECONDS));

      releaseClose.countDown();
      removeFuture.get(500, TimeUnit.MILLISECONDS);
    } finally {
      releaseClose.countDown();
      removeExecutor.shutdownNow();
      leaseExecutor.shutdownNow();
    }
  }

  @Test
  void doesNotLeaseConnectionWhileIdleConnectionIsClosing() throws Exception {
    var closeStarted = new CountDownLatch(1);
    var releaseClose = new CountDownLatch(1);
    var removeCalled = new CountDownLatch(1);
    PlcConnectionManager manager = new PlcConnectionManager() {
      @Override
      public PlcConnection getConnection(String url) {
        return new BlockingCloseConnection(closeStarted, releaseClose);
      }

      @Override
      public PlcConnection getConnection(String url,
                                         PlcAuthentication authentication) {
        return null;
      }
    };
    var connectionContainer = new SpConnectionContainer(
        manager,
        "mock://idle",
        Duration.ofSeconds(30),
        Duration.ofMillis(10),
        url -> {
          removeCalled.countDown();
          return null;
        }
    );

    SpLeasedPlcConnection lease =
        (SpLeasedPlcConnection) connectionContainer.lease().get(500, TimeUnit.MILLISECONDS);
    connectionContainer.returnConnection(lease, false);

    try {
      assertTrue(closeStarted.await(500, TimeUnit.MILLISECONDS));

      ExecutionException exception = assertThrows(
          ExecutionException.class,
          () -> connectionContainer.lease().get(500, TimeUnit.MILLISECONDS)
      );
      assertTrue(exception.getCause() instanceof PlcConnectionManagerClosedException);

      releaseClose.countDown();
      assertTrue(removeCalled.await(500, TimeUnit.MILLISECONDS));
    } finally {
      releaseClose.countDown();
    }
  }

  @Test
  void idleCloseDoesNotRemoveReplacementContainer() throws Exception {
    var closeStarted = new CountDownLatch(1);
    var releaseClose = new CountDownLatch(1);
    var connectionAttempts = new AtomicInteger();
    PlcConnectionManager manager = new PlcConnectionManager() {
      @Override
      public PlcConnection getConnection(String url) {
        if (connectionAttempts.incrementAndGet() == 1) {
          return new BlockingCloseConnection(closeStarted, releaseClose);
        }
        return new DummyConnection();
      }

      @Override
      public PlcConnection getConnection(String url,
                                         PlcAuthentication authentication) {
        return null;
      }
    };

    var cachedConnectionManager = new SpCachedPlcConnectionManager(
        manager,
        Duration.ofSeconds(30),
        Duration.ofSeconds(30),
        Duration.ofMillis(10)
    );
    PlcConnection firstLease = cachedConnectionManager.getConnection("mock://idle");
    firstLease.close();

    try {
      assertTrue(closeStarted.await(500, TimeUnit.MILLISECONDS));

      PlcConnection replacementLease = cachedConnectionManager.getConnection("mock://idle");
      assertNotNull(replacementLease);
      assertTrue(cachedConnectionManager.getCachedConnections().contains("mock://idle"));

      releaseClose.countDown();
      assertTrue(cachedConnectionManager.getCachedConnections().contains("mock://idle"));
      replacementLease.close();
    } finally {
      releaseClose.countDown();
      cachedConnectionManager.removeCachedConnection("mock://idle");
    }
  }
}
