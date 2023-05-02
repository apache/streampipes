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

import org.apache.streampipes.connect.iiot.protocol.stream.MqttProtocol;
import org.apache.streampipes.extensions.api.connect.exception.AdapterException;
import org.apache.streampipes.extensions.management.connect.adapter.format.json.object.JsonObjectFormat;
import org.apache.streampipes.integration.containers.MosquittoContainer;
import org.apache.streampipes.integration.containers.MosquittoDevContainer;
import org.apache.streampipes.messaging.mqtt.MqttPublisher;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.rules.DebugSinkRuleDescription;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.apache.streampipes.pe.shared.config.mqtt.MqttConnectUtils;
import org.apache.streampipes.sdk.builder.adapter.GenericDataStreamAdapterBuilder;
import org.apache.streampipes.sdk.builder.adapter.ProtocolDescriptionBuilder;
import org.apache.streampipes.sdk.helpers.Labels;

import com.google.common.collect.Maps;
import org.junit.Assert;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MqttAdapterTester extends AdapterTesterBase {

  MosquittoContainer mosquittoContainer;

  private static final String TOPIC = "testtopic";

  @Override
  public void startAdapterService() throws Exception {
    if (Objects.equals(System.getenv("TEST_MODE"), "dev")) {
      mosquittoContainer = new MosquittoDevContainer();
    } else {
      mosquittoContainer = new MosquittoContainer();
    }
    mosquittoContainer.start();
  }

  @Override
  public AdapterDescription prepareAdapter() throws Exception {
    return GenericDataStreamAdapterBuilder
        .create(MqttProtocol.ID)
        .format(new JsonObjectFormat()
            .declareModel())
        .protocol(ProtocolDescriptionBuilder.create(MqttProtocol.ID)
            .requiredTextParameter(
                Labels.withId(MqttConnectUtils.BROKER_URL),
                mosquittoContainer.getBrokerUrl())
            .requiredTextParameter(
                Labels.withId(MqttConnectUtils.TOPIC), TOPIC)
            .requiredAlternatives(
                MqttConnectUtils.getAccessModeLabel(),
                MqttConnectUtils.getAlternativesOne(true),
                MqttConnectUtils.getAlternativesTwo())
            .build())
        .addRules(List.of(new DebugSinkRuleDescription()))
        .build();
  }

  @Override
  public List<Map<String, Object>> generateData() throws Exception {
    TimeUnit.SECONDS.sleep(2);
    List<Map<String, Object>> result = new ArrayList<>();
    MqttTransportProtocol mqttSettings = makeMqttSettings();
    MqttPublisher publisher = new MqttPublisher();
    publisher.connect(mqttSettings);

    ObjectMapper objectMapper = new ObjectMapper();

    for (int i = 0; i < 3; i++) {
      var dataMap = new HashMap<String, Object>();
      dataMap.put("timestamp", i);
      dataMap.put("value", "test-data");
      byte[] data = objectMapper.writeValueAsBytes(dataMap);
      publisher.publish(data);
      result.add(dataMap);
    }

    publisher.disconnect();
    return result;
  }

  private MqttTransportProtocol makeMqttSettings() {
    return new MqttTransportProtocol(mosquittoContainer.getBrokerHost(), mosquittoContainer.getBrokerPort(), TOPIC);
  }

  @Override
  public void validateData(List<Map<String, Object>> expectedData) throws Exception {
    for (Map<String, Object> expected : expectedData) {
      Map<String, Object> actual = takeEvent();
      Assert.assertTrue(Maps.difference(expected, actual).areEqual());
    }
  }

  @Override
  public void close() throws Exception {
    if (mosquittoContainer != null) {
      mosquittoContainer.stop();
    }
    try {
      stopAdapter();
    } catch (AdapterException e) {
      throw new RuntimeException(e);
    }
  }
}
