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

import org.apache.streampipes.extensions.api.connect.DataSourceHealthCheckResult;

import com.hivemq.client.mqtt.datatypes.MqttQos;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class MqttHealthChecker extends MqttBase {

  private static final int TIMEOUT_SECONDS = 15;

  public MqttHealthChecker(MqttConfig mqttConfig) {
    super(mqttConfig);
  }

  @Override
  protected com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient setupMqttClient() throws Exception {
    return setupMqttClientBuilder().buildAsync();
  }

  public DataSourceHealthCheckResult check() {
    try {
      var client = setupMqttClient();
      var connectLatch = new CountDownLatch(1);
      var connectError = new AtomicReference<Throwable>();

      client.connectWith()
          .keepAlive(TIMEOUT_SECONDS)
          .send()
          .whenComplete((ack, error) -> {
            connectError.set(error);
            connectLatch.countDown();
          });

      if (!connectLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
        return DataSourceHealthCheckResult.unhealthyWithException(
            "MQTT connection timed out", 
            new java.util.concurrent.TimeoutException("Timed out waiting for broker response after " + TIMEOUT_SECONDS + "s")
        );
      }
      if (connectError.get() != null) {
        return DataSourceHealthCheckResult.unhealthyWithException("MQTT connection failed", connectError.get());
      }

      var subscribeLatch = new CountDownLatch(1);
      var subscribeError = new AtomicReference<Throwable>();

      client.subscribeWith()
          .topicFilter(mqttConfig.getTopic())
          .qos(MqttQos.AT_MOST_ONCE)
          .send()
          .whenComplete((ack, error) -> {
            subscribeError.set(error);
            subscribeLatch.countDown();
          });

      if (!subscribeLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
        client.disconnect();
        return DataSourceHealthCheckResult.unhealthyWithException(
            "MQTT subscription timed out",
            new java.util.concurrent.TimeoutException("Timed out waiting for subscription ACK after " + TIMEOUT_SECONDS + "s")
        );
      }
      if (subscribeError.get() != null) {
        client.disconnect();
        return DataSourceHealthCheckResult.unhealthyWithException(
            "Failed to subscribe to topic '" + mqttConfig.getTopic() + "'",
            subscribeError.get()
        );
      }

      client.disconnect();
      return DataSourceHealthCheckResult.healthy(
          "MQTT broker and topic '" + mqttConfig.getTopic() + "' are accessible"
      );
    } catch (Exception e) {
      return DataSourceHealthCheckResult.unhealthyWithException("MQTT health check failed: " + e.getMessage(), e);
    }
  }
}
