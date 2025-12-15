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

package org.apache.streampipes.extensions.connectors.plc.cache;

import io.netty.channel.ConnectTimeoutException;
import org.apache.plc4x.java.api.EventPlcConnection;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcConnectionManager;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.exceptions.PlcRuntimeException;
import org.apache.plc4x.java.api.listener.EventListener;
import org.apache.plc4x.java.utils.cache.exceptions.PlcConnectionManagerClosedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Function;

public class SpConnectionContainer {
  private static final Logger LOGGER = LoggerFactory.getLogger(SpConnectionContainer.class);
  private final PlcConnectionManager connectionManager;
  private final String connectionUrl;
  private final Duration maxLeaseTime;
  private final Duration maxIdleTime;
  private final Function<String, Void> closeConnectionHandler;
  private final Queue<CompletableFuture<PlcConnection>> queue;

  private PlcConnection connection;
  private SpLeasedPlcConnection leasedConnection;
  private Timer idleTimer;

  public SpConnectionContainer(PlcConnectionManager connectionManager, String connectionUrl,
                               Duration maxLeaseTime, Duration maxIdleTime,
                               Function<String, Void> closeConnectionHandler) {
    this.connectionManager = connectionManager;
    this.connectionUrl = connectionUrl;
    this.maxLeaseTime = maxLeaseTime;
    this.maxIdleTime = maxIdleTime;
    this.closeConnectionHandler = closeConnectionHandler;
    this.queue = new LinkedList<>();
    this.connection = null;
    this.leasedConnection = null;
  }

  public synchronized void close() {
    // Close all waiting clients exceptionally.
    queue.forEach(plcConnectionCompletableFuture ->
        plcConnectionCompletableFuture.completeExceptionally(new PlcConnectionManagerClosedException()));

    // Clear the queue.
    queue.clear();

    // Stop the idle timer.
    if (idleTimer != null) {
      idleTimer.cancel();
      idleTimer.purge();
      idleTimer = null;
    }

    // If the connection is currently used, close it.
    if (leasedConnection != null) {
      try {
        leasedConnection.closeConnection();
        leasedConnection = null;
      } catch (Exception e) {
        // Ignore this ...
      }
    } else {
      try {
        connection.close();
        connection = null;
      } catch (Exception e) {
        // Ignore this ...
      }
    }
  }

  public synchronized Future<PlcConnection> lease() {
    CompletableFuture<PlcConnection> connectionFuture = new CompletableFuture<>();

    // Try to get a new connection, if we haven't got one yet.
    if (connection == null) {
      try {
        connection = connectionManager.getConnection(connectionUrl);
      } catch (PlcConnectionException e) {
        Throwable root = rootCause(e);
        if (root instanceof ConnectTimeoutException) {
          // one-liner — no stacktrace at WARN
          LOGGER.warn("PLC connect timeout to {}: {}", connectionUrl, root.getMessage());
          // full detail only when debugging
          LOGGER.debug("Connect timeout details", e);
        } else {
          // concise by default, full at DEBUG
          LOGGER.warn("Failed to connect to {}: {}", connectionUrl,
              (root != null ? root.toString() : e.toString()));
          LOGGER.debug("Connection failure details", e);
        }
        connectionFuture.completeExceptionally(e);
        return connectionFuture;
      }
    }

    // If the connection is currently idle, return the connection immediately.
    if (leasedConnection == null) {
      leasedConnection = new SpLeasedPlcConnection(this, connection, maxLeaseTime);
      connectionFuture.complete(leasedConnection);
    } else {
      queue.add(connectionFuture);
    }

    // Stop the idle timer.
    if (idleTimer != null) {
      idleTimer.cancel();
      idleTimer.purge();
      idleTimer = null;
    }

    return connectionFuture;
  }

  public synchronized void returnConnection(SpLeasedPlcConnection returnedLeasedConnection, boolean invalidateConnection) {
    if (returnedLeasedConnection != leasedConnection) {
      LOGGER.error("Error trying to return lease from invalid connection: returned={} leased={}",
          returnedLeasedConnection, leasedConnection);
      throw new PlcRuntimeException("Error trying to return lease from invalid connection");
    }

    // If something happened while using the connection, invalidate this one and create a new connection.
    if (invalidateConnection) {
      // Close the old connection.
      try {
        connection.close();
      } catch (Exception e) {
        // We're ignoring this as we have no idea, what state the connection is in.
        // Nevertheless, it is polite to say something in logs about this situation.
        LOGGER.warn("Exception while closing connection", e);
      }

      // Try to get a new connection.
      try {
        connection = connectionManager.getConnection(connectionUrl);
      } catch (PlcConnectionException e) {
        // If something goes wrong, close all waiting futures exceptionally.
        LOGGER.warn("Can't get connection for {} complete queue items exceptionally", connectionUrl, e);
        queue.forEach(future -> future.completeExceptionally(e));
        queue.clear();
        leasedConnection = null;
        connection = null;
      }
    }

    // If the queue is empty, simply return.
    if (queue.isEmpty()) {
      leasedConnection = null;

      // Start a timer to invalidate this connection if it's idle for too long.
      idleTimer = new Timer("CC-Idle-Timer-" + Thread.currentThread().getId());
      idleTimer.schedule(new TimerTask() {
        @Override
        public void run() {
          if (connection != null) {
            try {
              connection.close();
            } catch (Exception e) {
              // Ignore ...
            }
          }
          closeConnectionHandler.apply(connectionUrl);
        }
      }, maxIdleTime.toMillis());
      return;
    }

    if (connection == null) {
      // Defensive: should not happen, but avoid creating a zombie.
      queue.forEach(f -> f.completeExceptionally(new PlcRuntimeException("No connection available")));
      queue.clear();
      leasedConnection = null;
      return;
    }

    // Create a new lease and complete the next future in the queue with this.
    leasedConnection = new SpLeasedPlcConnection(this, connection, maxLeaseTime);
    CompletableFuture<PlcConnection> leaseFuture = queue.poll();
    if (leaseFuture != null) {
      leaseFuture.complete(leasedConnection);
    }
  }


  public void addEventListener(EventListener listener) {
    if ((connection != null) && (connection instanceof EventPlcConnection)) {
      ((EventPlcConnection) connection).addEventListener(listener);
    }
  }

  public void removeEventListener(EventListener listener) {
    if ((connection != null) && (connection instanceof EventPlcConnection)) {
      ((EventPlcConnection) connection).removeEventListener(listener);
    }
  }

  private static Throwable rootCause(Throwable t) {
    Throwable c = t;
    while (c.getCause() != null && c.getCause() != c) {
      c = c.getCause();
    }
    return c;
  }


}

