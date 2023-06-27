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
import org.apache.streampipes.connect.iiot.protocol.stream.pulsar.PulsarProtocol;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.integration.containers.PulsarContainer;
import org.apache.streampipes.integration.containers.PulsarDevContainer;
import org.apache.streampipes.integration.utils.Utils;
import org.apache.streampipes.manager.template.AdapterTemplateHandler;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.model.template.PipelineElementTemplate;
import org.apache.streampipes.model.template.PipelineElementTemplateConfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

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
  public IAdapterConfiguration prepareAdapter() {
    IAdapterConfiguration configuration = new PulsarProtocol().declareConfig();

    Map<String, PipelineElementTemplateConfig> configs = new HashMap<>();
    configs.put(PulsarProtocol.PULSAR_BROKER_HOST,
        new PipelineElementTemplateConfig(true, true, pulsarContainer.getBrokerHost()));
    configs.put(PulsarProtocol.PULSAR_BROKER_PORT,
        new PipelineElementTemplateConfig(true, false, pulsarContainer.getBrokerPort()));
    configs.put(PulsarProtocol.PULSAR_TOPIC,
        new PipelineElementTemplateConfig(true, false, TOPIC));
    configs.put(PulsarProtocol.PULSAR_SUBSCRIPTION_NAME,
        new PipelineElementTemplateConfig(true, false, "test-sub"));

    var template = new PipelineElementTemplate("name", "description", configs);

    var desc =
        new AdapterTemplateHandler(template,
            configuration.getAdapterDescription(),
            true)
            .applyTemplateOnPipelineElement();

    ((StaticPropertyAlternatives) (desc)
        .getConfig()
        .get(4))
        .getAlternatives()
        .get(0)
        .setSelected(true);

    return configuration;
  }

  @Override
  public StreamPipesAdapter getAdapterInstance() {
    return new PulsarProtocol();
  }

  @Override
  public List<Map<String, Object>> getTestEvents() {
    return Utils.getSimpleTestEvents();
  }

  @Override
  public void publishEvents(List<Map<String, Object>> events) {
    try (PulsarClient client = PulsarClient.builder().serviceUrl(
            String.format("pulsar://%s:%s", pulsarContainer.getBrokerHost(),
                pulsarContainer.getBrokerPort()))
        .build();
         Producer<byte[]> producer = client.newProducer().topic(TOPIC).create()) {
      var objectMapper = new ObjectMapper();

      events.forEach(event -> {
        try {
          var serializedEvent = objectMapper.writeValueAsBytes(event);
          producer.sendAsync(serializedEvent);
        } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
        }
      });

      producer.flush();
    } catch (PulsarClientException e) {
      throw new RuntimeException(e);
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
