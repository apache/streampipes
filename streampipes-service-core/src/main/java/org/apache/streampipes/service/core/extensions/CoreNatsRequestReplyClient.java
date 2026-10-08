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

import org.apache.streampipes.messaging.nats.NatsConnectionLog;

import io.nats.client.Connection;
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
  private NatsConnectionLog connectionLog;

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
    if (connectionLog == null) {
      connectionLog = new NatsConnectionLog("core management");
    }
    var optionsBuilder = Options.builder()
        .server(natsUrl)
        .maxReconnects(-1)
        .errorListener(connectionLog)
        .connectionListener(connectionLog);

    if (natsToken != null && !natsToken.isBlank()) {
      optionsBuilder.token(natsToken);
    }

    return optionsBuilder.build();
  }

  public synchronized void close() {
    if (connectionLog != null) {
      connectionLog.stop();
      connectionLog = null;
    }
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
