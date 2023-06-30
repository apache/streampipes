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

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.connect.iiot.protocol.stream.MqttProtocol;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.integration.containers.MosquittoContainer;
import org.apache.streampipes.integration.containers.MosquittoDevContainer;
import org.apache.streampipes.integration.utils.Utils;
import org.apache.streampipes.manager.template.AdapterTemplateHandler;
import org.apache.streampipes.messaging.mqtt.MqttPublisher;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.model.template.PipelineElementTemplate;
import org.apache.streampipes.model.template.PipelineElementTemplateConfig;
import org.apache.streampipes.pe.shared.config.mqtt.MqttConnectUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MqttAdapterTester extends AdapterTesterBase {

  MosquittoContainer mosquittoContainer;

  private static final String TOPIC = "testtopic";

  @Override
  public void startAdapterService() {
    if (Objects.equals(System.getenv("TEST_MODE"), "dev")) {
      mosquittoContainer = new MosquittoDevContainer();
    } else {
      mosquittoContainer = new MosquittoContainer();
    }

    mosquittoContainer.start();
  }

  @Override
  public IAdapterConfiguration prepareAdapter() {

    IAdapterConfiguration configuration = new MqttProtocol().declareConfig();

    Map<String, PipelineElementTemplateConfig> configs = new HashMap<>();
    configs.put(MqttConnectUtils.TOPIC,
        new PipelineElementTemplateConfig(true, true, TOPIC));
    configs.put(MqttConnectUtils.BROKER_URL,
        new PipelineElementTemplateConfig(true, false, mosquittoContainer.getBrokerUrl()));

    var template = new PipelineElementTemplate("name", "description", configs);

    var desc =
        new AdapterTemplateHandler(template,
            configuration.getAdapterDescription(),
            true)
            .applyTemplateOnPipelineElement();

    // Set authentication mode to unauthenticated
    ((StaticPropertyAlternatives) desc
        .getConfig()
        .get(1))
        .getAlternatives()
        .get(0)
        .setSelected(true);

    // Set format to Json
    ((StaticPropertyAlternatives) (desc)
        .getConfig()
        .get(3))
        .getAlternatives()
        .get(0)
        .setSelected(true);

    return configuration;
  }

  @Override
  public StreamPipesAdapter getAdapterInstance() {
    return new MqttProtocol();
  }

  @Override
  public List<Map<String, Object>> getTestEvents() {
    return Utils.getSimpleTestEvents();
  }


  @Override
  public void publishEvents(List<Map<String, Object>> events) {
    var publisher = getMqttPublisher();
    var objectMapper = new ObjectMapper();

    events.forEach(event -> {
      try {
        var serializedEvent = objectMapper.writeValueAsBytes(event);
        publisher.publish(serializedEvent);
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    });

    publisher.disconnect();
  }

  @NotNull
  private MqttPublisher getMqttPublisher() {
    MqttTransportProtocol mqttSettings = new MqttTransportProtocol(
        mosquittoContainer.getBrokerHost(),
        mosquittoContainer.getBrokerPort(),
        TOPIC);
    MqttPublisher publisher = new MqttPublisher(mqttSettings);
    publisher.connect();
    return publisher;
  }

  @Override
  public void close() {
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
