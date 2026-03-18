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
import io.nats.client.Message;
import io.nats.client.Nats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;

public class CoreNatsRequestReplyClient {

  private static final Logger LOG = LoggerFactory.getLogger(CoreNatsRequestReplyClient.class);

  private final String natsUrl;
  private final Duration timeout;
  private Connection natsConnection;

  public CoreNatsRequestReplyClient(String host, int port, Duration timeout) {
    this.natsUrl = "nats://" + host + ":" + port;
    this.timeout = timeout;
  }

  public synchronized byte[] request(String subject, byte[] payload) throws IOException {
    try {
      Message response = getConnection().request(subject, payload, timeout);
      if (response == null) {
        throw new IOException("No NATS response received for subject " + subject);
      }
      return response.getData();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("NATS request was interrupted for subject " + subject, e);
    }
  }

  private Connection getConnection() throws IOException {
    if (natsConnection == null || natsConnection.getStatus() != Connection.Status.CONNECTED) {
      try {
        natsConnection = Nats.connect(natsUrl);
        LOG.info("Connected to NATS at {}", natsUrl);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new IOException("Could not connect to NATS at " + natsUrl, e);
      }
    }

    return natsConnection;
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
