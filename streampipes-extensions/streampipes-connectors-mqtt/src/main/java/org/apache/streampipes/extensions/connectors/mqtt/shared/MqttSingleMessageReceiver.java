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
package org.apache.streampipes.extensions.connectors.mqtt.shared;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MqttSingleMessageReceiver extends MqttBase {

  private static final Logger LOG = LoggerFactory.getLogger(MqttSingleMessageReceiver.class);
  private final int timeoutInSeconds;

  public MqttSingleMessageReceiver(MqttConfig mqttConfig, int timeoutInSeconds) {
    super(mqttConfig);
    this.timeoutInSeconds = timeoutInSeconds;
  }

  /**
   * Receives a single MQTT message and blocks until a message arrives or the timeout elapses.
   *
   * @return the payload bytes of the first received message
   * @throws AdapterException when receiving fails or the thread is interrupted
   */
  public byte[] receiveSingleMessage() throws AdapterException {
    try {
      return receiveSingleMessageAsync().get();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new AdapterException("Interrupted while waiting for an MQTT message", e);
    } catch (ExecutionException e) {
      if (e.getCause() instanceof TimeoutException) {
        throw new AdapterException(
            String.format("Timed out after %d seconds while waiting for an MQTT message", timeoutInSeconds),
            e.getCause()
        );
      }
      throw new AdapterException("Failed to receive MQTT message", e.getCause());
    } catch (Exception e) {
      throw new AdapterException("Failed to receive MQTT message", e);
    }
  }

  private CompletableFuture<byte[]> receiveSingleMessageAsync() {
    var result = new CompletableFuture<byte[]>();
    var client = setupClientOrFail(result);
    if (client == null) {
      return result;
    }
    registerCleanup(result, client);
    subscribeForSingleMessage(result, client);
    applyTimeout(result);
    return result;
  }

  private Mqtt3AsyncClient setupClientOrFail(CompletableFuture<byte[]> result) {
    try {
      return super.setupMqttClient();
    } catch (Exception e) {
      result.completeExceptionally(e);
      return null;
    }
  }

  private void registerCleanup(CompletableFuture<byte[]> result, Mqtt3AsyncClient client) {
    result.whenComplete((payload, throwable) -> disconnectQuietly(client));
  }

  private void subscribeForSingleMessage(CompletableFuture<byte[]> result, Mqtt3AsyncClient client) {
    client.connect()
          .thenCompose(v -> client.subscribeWith()
                                  .topicFilter(mqttConfig.getTopic())
                                  .qos(MqttQos.AT_LEAST_ONCE)
                                  .callback(publish -> completeIfEmpty(result, publish.getPayloadAsBytes()))
                                  .send()
          )
          .exceptionally(ex -> {
            result.completeExceptionally(ex);
            return null;
          });
  }

  private void completeIfEmpty(CompletableFuture<byte[]> result, byte[] payload) {
    if (!result.isDone()) {
      result.complete(payload);
    }
  }

  private void applyTimeout(CompletableFuture<byte[]> result) {
    if (this.timeoutInSeconds > 0) {
      result.orTimeout(this.timeoutInSeconds, TimeUnit.SECONDS);
    }
  }

  private void disconnectQuietly(Mqtt3AsyncClient client) {
    try {
      client.disconnect().whenComplete((result, throwable) -> {
        if (throwable != null) {
          LOG.warn("Failed to disconnect MQTT client after receiving sample", throwable);
        }
      });
    } catch (Exception e) {
      LOG.warn("Failed to disconnect MQTT client after receiving sample", e);
    }
  }
}
