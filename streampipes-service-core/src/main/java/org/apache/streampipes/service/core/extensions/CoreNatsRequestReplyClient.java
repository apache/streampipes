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

package org.apache.streampipes.service.core.extensions;

import io.nats.client.Connection;
import io.nats.client.ConnectionListener;
import io.nats.client.Message;
import io.nats.client.Nats;
import io.nats.client.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;

public class CoreNatsRequestReplyClient {

  private static final Logger LOG = LoggerFactory.getLogger(CoreNatsRequestReplyClient.class);

  private final String natsUrl;
  private final String natsToken;
  private final Duration timeout;
  private Connection natsConnection;

  public CoreNatsRequestReplyClient(String host, int port, String natsToken, Duration timeout) {
    this.natsUrl = "nats://" + host + ":" + port;
    this.natsToken = natsToken;
    this.timeout = timeout;
  }

  public synchronized byte[] request(String subject, byte[] payload) throws IOException {
    try {
      Message response = getConnection().request(subject, payload, timeout);
      if (response == null) {
        throw new IOException("No NATS response received for subject " + subject);
      }
      return response.getData();
    } catch (IllegalStateException e) {
      close();
      throw new IOException("NATS connection is not available for subject " + subject, e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("NATS request was interrupted for subject " + subject, e);
    }
  }

  private Connection getConnection() throws IOException {
    if (natsConnection == null || natsConnection.getStatus() == Connection.Status.CLOSED) {
      try {
        natsConnection = Nats.connect(buildOptions());
        LOG.info("Connected to NATS at {}", natsUrl);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new IOException("Could not connect to NATS at " + natsUrl, e);
      }
    }

    return natsConnection;
  }

  private Options buildOptions() {
    var optionsBuilder = Options.builder()
        .server(natsUrl)
        .maxReconnects(-1)
        .connectionListener(this::onConnectionEvent);

    if (natsToken != null && !natsToken.isBlank()) {
      optionsBuilder.token(natsToken);
    }

    return optionsBuilder.build();
  }

  private void onConnectionEvent(Connection connection, ConnectionListener.Events event) {
    if (event == ConnectionListener.Events.RECONNECTED || event == ConnectionListener.Events.CONNECTED) {
      LOG.info("NATS connection event for {}: {}", natsUrl, event);
    } else if (event == ConnectionListener.Events.DISCONNECTED
        || event == ConnectionListener.Events.CLOSED) {
      LOG.warn("NATS connection event for {}: {}", natsUrl, event);
    }
  }

  public synchronized void close() {
    if (natsConnection != null) {
      try {
        natsConnection.close();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        LOG.warn("Interrupted while closing NATS connection", e);
      } finally {
        natsConnection = null;
      }
    }
  }
}
