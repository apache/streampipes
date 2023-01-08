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
package org.apache.streampipes.integration.adapters;

import org.apache.streampipes.connect.iiot.protocol.stream.pulsar.PulsarProtocol;
import org.apache.streampipes.extensions.api.connect.exception.AdapterException;
import org.apache.streampipes.extensions.management.connect.adapter.format.json.object.JsonObjectFormat;
import org.apache.streampipes.integration.containers.PulsarContainer;
import org.apache.streampipes.integration.containers.PulsarDevContainer;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.rules.DebugSinkRuleDescription;
import org.apache.streampipes.sdk.builder.adapter.GenericDataStreamAdapterBuilder;
import org.apache.streampipes.sdk.builder.adapter.ProtocolDescriptionBuilder;
import org.apache.streampipes.sdk.helpers.Labels;

import com.google.common.collect.Maps;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.junit.Assert;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PulsarAdapterTester extends AdapterTesterBase {
  PulsarContainer pulsarContainer;
  private static final String TOPIC = "test-topic";

  @Override
  public void startAdapterService() {
    if (Objects.equals(System.getenv("TEST_MODE"), "dev")) {
      pulsarContainer = new PulsarDevContainer();
    } else {
      pulsarContainer = new PulsarContainer();
    }
    pulsarContainer.start();
  }

  @Override
  public AdapterDescription prepareAdapter() {

    return GenericDataStreamAdapterBuilder
        .create(PulsarProtocol.ID)
        .format(new JsonObjectFormat()
            .declareModel())
        .protocol(ProtocolDescriptionBuilder.create(PulsarProtocol.ID)
            .requiredTextParameter(
                Labels.withId(PulsarProtocol.PULSAR_BROKER_HOST),
                pulsarContainer.getBrokerHost())
            .requiredIntegerParameter(
                Labels.withId(PulsarProtocol.PULSAR_BROKER_PORT),
                pulsarContainer.getBrokerPort())
            .requiredTextParameter(
                Labels.withId(PulsarProtocol.PULSAR_TOPIC), TOPIC)
            .requiredTextParameter(
                Labels.withId(PulsarProtocol.PULSAR_SUBSCRIPTION_NAME), "test-sub")
            .build())
        .addRules(List.of(new DebugSinkRuleDescription()))
        .build();
  }

  @Override
  public List<Map<String, Object>> generateData() throws Exception {
    List<Map<String, Object>> result = new ArrayList<>();
    try (PulsarClient client = PulsarClient.builder().serviceUrl(
            String.format("pulsar://%s:%s", pulsarContainer.getBrokerHost(),
                pulsarContainer.getBrokerPort()))
        .build();
         Producer<byte[]> producer = client.newProducer().topic(TOPIC).create()) {
      ObjectMapper objectMapper = new ObjectMapper();

      for (int i = 0; i < 3; i++) {
        var dataMap = new HashMap<String, Object>();
        dataMap.put("timestamp", i);
        dataMap.put("value", "test-data");
        byte[] data = objectMapper.writeValueAsBytes(dataMap);
        producer.sendAsync(data);
        result.add(dataMap);
      }
      producer.flush();
    }
    return result;
  }

  @Override
  public void validateData(List<Map<String, Object>> expectedData) throws Exception {
    for (Map<String, Object> expected : expectedData) {
      Map<String, Object> actual = takeEvent();
      Assert.assertTrue(Maps.difference(expected, actual).areEqual());
    }
  }

  @Override
  public void close() {
    if (pulsarContainer != null) {
      pulsarContainer.stop();
    }
    try {
      stopAdapter();
    } catch (AdapterException e) {
      throw new RuntimeException(e);
    }
  }
}
