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
import org.apache.streampipes.connect.iiot.protocol.stream.KafkaProtocol;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.integration.containers.KafkaContainer;
import org.apache.streampipes.integration.containers.KafkaDevContainer;
import org.apache.streampipes.manager.template.AdapterTemplateHandler;
import org.apache.streampipes.messaging.kafka.SpKafkaProducer;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.model.template.PipelineElementTemplate;
import org.apache.streampipes.model.template.PipelineElementTemplateConfig;
import org.apache.streampipes.pe.shared.config.kafka.KafkaConnectUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class KafkaAdapterTester extends AdapterTesterBase {

  KafkaContainer kafkaContainer;

  private static final String TOPIC = "test-topic";

  @Override
  public void startAdapterService() throws Exception {
    if (Objects.equals(System.getenv("TEST_MODE"), "dev")) {
      kafkaContainer = new KafkaDevContainer();
    } else {
      kafkaContainer = new KafkaContainer();
    }

    kafkaContainer.start();
  }

  @Override
  public IAdapterConfiguration prepareAdapter() throws AdapterException {
    IAdapterConfiguration configuration = new KafkaProtocol().declareConfig();
    List<Option> list = new ArrayList<>();
    list.add(new Option(TOPIC));
    ((RuntimeResolvableOneOfStaticProperty) configuration.getAdapterDescription()
            .getConfig()
            .get(4))
            .setOptions(list);
    Map<String, PipelineElementTemplateConfig> configs = new HashMap<>();
    configs.put(KafkaConnectUtils.HOST_KEY,
      new PipelineElementTemplateConfig(true, false, kafkaContainer.getBrokerHost()));
    configs.put(KafkaConnectUtils.PORT_KEY,
      new PipelineElementTemplateConfig(true, false, kafkaContainer.getBrokerPort()));
    configs.put(KafkaConnectUtils.TOPIC_KEY,
      new PipelineElementTemplateConfig(true, true, TOPIC));
    var template = new PipelineElementTemplate("name", "description", configs);


    var desc =
        new AdapterTemplateHandler(template,
        configuration.getAdapterDescription(),
        true)
        .applyTemplateOnPipelineElement();

    // Set authentication mode to UnauthenticatedPlain
    ((StaticPropertyAlternatives) (desc)
        .getConfig()
        .get(0))
        .getAlternatives()
        .get(0)
        .setSelected(true);

    // Set AUTO_OFFSET_RESET_CONFIG configuration to Earliest option
    ((StaticPropertyAlternatives) (desc)
        .getConfig()
        .get(5))
        .getAlternatives()
        .get(0)
        .setSelected(true);

    ((StaticPropertyAlternatives) (desc)
         .getConfig()
         .get(5))
         .getAlternatives()
         .get(1)
         .setSelected(false);

    // Set format to Json
    ((StaticPropertyAlternatives) (desc)
         .getConfig()
         .get(6))
         .getAlternatives()
         .get(0)
         .setSelected(true);

    return configuration;
  }

  @Override
  public StreamPipesAdapter getAdapterInstance() {
    return new KafkaProtocol();
  }

  @Override
  public List<Map<String, Object>> getTestEvents() {
    List<Map<String, Object>> result = new ArrayList<>();

    for (int i = 0; i < 3; i++) {
      result.add(
          Map.of(
          "timestamp", i,
          "value", "test-data")
      );
    }

    return result;
  }

  @Override
  public void publishEvents(List<Map<String, Object>> events) throws Exception {
    var publisher = getSpKafkaProducer();
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
  private SpKafkaProducer getSpKafkaProducer() {
    KafkaTransportProtocol kafkaSettings = new KafkaTransportProtocol(
        kafkaContainer.getBrokerHost(),
        kafkaContainer.getBrokerPort(),
        TOPIC);
    SpKafkaProducer publisher = new SpKafkaProducer(kafkaSettings);
    publisher.connect();
    return publisher;
  }

  @Override
  public void close() throws Exception {
    if (kafkaContainer != null) {
      kafkaContainer.stop();
    }
    try {
      stopAdapter();
    } catch (AdapterException e) {
      throw new RuntimeException(e);
    }
  }
}
