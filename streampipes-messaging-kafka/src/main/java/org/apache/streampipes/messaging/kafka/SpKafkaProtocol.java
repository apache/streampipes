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

package org.apache.streampipes.messaging.kafka;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.messaging.EventConsumer;
import org.apache.streampipes.messaging.EventProducer;
import org.apache.streampipes.messaging.SpProtocolDefinition;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SpKafkaProtocol implements SpProtocolDefinition<KafkaTransportProtocol> {

  @Override
  public void validateConnection(KafkaTransportProtocol transportProtocol) {
    var properties = new Properties();
    properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, transportProtocol.resolveBootstrapServers());
    var client = AdminClient.create(properties);
    try {
      if (client.describeCluster().nodes().get(10, TimeUnit.SECONDS).isEmpty()) {
        throw new SpRuntimeException("Internal Kafka broker is not ready");
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new SpRuntimeException("Interrupted while connecting to internal Kafka broker", e);
    } catch (ExecutionException | TimeoutException e) {
      throw new SpRuntimeException("Could not connect to internal Kafka broker", e);
    } finally {
      client.close(Duration.ofSeconds(1));
    }
  }

  @Override
  public EventConsumer getConsumer(KafkaTransportProtocol transportProtocol) {
    return new SpKafkaConsumer(transportProtocol);
  }

  @Override
  public EventProducer getProducer(KafkaTransportProtocol transportProtocol) {
    return new SpKafkaProducer(transportProtocol);
  }
}
