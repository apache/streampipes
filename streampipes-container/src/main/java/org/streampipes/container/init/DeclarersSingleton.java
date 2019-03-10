/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.container.init;

import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.container.declarer.Declarer;
import org.streampipes.container.declarer.PipelineTemplateDeclarer;
import org.streampipes.container.declarer.SemanticEventConsumerDeclarer;
import org.streampipes.container.declarer.SemanticEventProcessingAgentDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.dataformat.SpDataFormatFactory;
import org.streampipes.dataformat.SpDataFormatManager;
import org.streampipes.messaging.SpProtocolDefinitionFactory;
import org.streampipes.messaging.SpProtocolManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeclarersSingleton {
  private static DeclarersSingleton instance;

  private Map<String, SemanticEventProcessingAgentDeclarer> epaDeclarers;
  private Map<String, SemanticEventProducerDeclarer> producerDeclarers;
  private Map<String, SemanticEventConsumerDeclarer> consumerDeclarers;
  private Map<String, PipelineTemplateDeclarer> pipelineTemplateDeclarers;
  private Map<String, DataStreamDeclarer> streamDeclarers;

  private int port;
  private String route;
  private String hostName;


  private DeclarersSingleton() {
    this.epaDeclarers = new HashMap<>();
    this.producerDeclarers = new HashMap<>();
    this.consumerDeclarers = new HashMap<>();
    this.streamDeclarers = new HashMap<>();
    this.pipelineTemplateDeclarers = new HashMap<>();
    this.route = "/";
  }

  public static DeclarersSingleton getInstance() {
    if (DeclarersSingleton.instance == null) {
      DeclarersSingleton.instance = new DeclarersSingleton();
    }
    return DeclarersSingleton.instance;
  }

  public void addDeclarers(List<Declarer> allDeclarers) {
    for (Declarer d : allDeclarers) {
      add(d);
    }
  }

  public DeclarersSingleton add(Declarer d) {
    if (d instanceof SemanticEventProcessingAgentDeclarer) {
      addEpaDeclarer((SemanticEventProcessingAgentDeclarer) d);
    } else if (d instanceof SemanticEventProducerDeclarer) {
      addProducerDeclarer((SemanticEventProducerDeclarer) d);
    } else if (d instanceof SemanticEventConsumerDeclarer) {
      addConsumerDeclarer((SemanticEventConsumerDeclarer) d);
    } else if (d instanceof PipelineTemplateDeclarer) {
      addPipelineTemplateDeclarer((PipelineTemplateDeclarer) d);
    }

    return getInstance();
  }

  public Map<String, Declarer> getDeclarers() {
    Map<String, Declarer> result = new HashMap<>();
    result.putAll(epaDeclarers);
    result.putAll(producerDeclarers);
    result.putAll(consumerDeclarers);
    result.putAll(pipelineTemplateDeclarers);
    return result;
  }

  public void registerProtocol(SpProtocolDefinitionFactory<?> protocol) {
    SpProtocolManager.INSTANCE.register(protocol);
  }

  public void registerDataFormat(SpDataFormatFactory dataFormatDefinition) {
    SpDataFormatManager.INSTANCE.register(dataFormatDefinition);
  }

  private void addEpaDeclarer(SemanticEventProcessingAgentDeclarer epaDeclarer) {
    epaDeclarers.put(epaDeclarer.declareModel().getAppId(), epaDeclarer);
  }

  private void addProducerDeclarer(SemanticEventProducerDeclarer sourceDeclarer) {
    checkAndStartExecutableStreams(sourceDeclarer);
    producerDeclarers.put(sourceDeclarer.declareModel().getAppId(), sourceDeclarer);
    sourceDeclarer.getEventStreams().forEach(sd -> {
      streamDeclarers.put(sd.declareModel(sourceDeclarer.declareModel()).getAppId(), sd);
    });
  }

  private void addConsumerDeclarer(SemanticEventConsumerDeclarer consumerDeclarer) {
    consumerDeclarers.put(consumerDeclarer.declareModel().getAppId(), consumerDeclarer);
  }

  private void addPipelineTemplateDeclarer(PipelineTemplateDeclarer pipelineTemplateDeclarer) {
    pipelineTemplateDeclarers.put(pipelineTemplateDeclarer.declareModel().getAppId(),
            pipelineTemplateDeclarer);
  }

  public Map<String, SemanticEventProcessingAgentDeclarer> getEpaDeclarers() {
    return epaDeclarers;
  }

  public Map<String, SemanticEventProducerDeclarer> getProducerDeclarers() {
    return producerDeclarers;
  }

  public Map<String, SemanticEventConsumerDeclarer> getConsumerDeclarers() {
    return consumerDeclarers;
  }

  public Map<String, PipelineTemplateDeclarer> getPipelineTemplateDeclarers() {
    return pipelineTemplateDeclarers;
  }

  public Map<String, DataStreamDeclarer> getStreamDeclarers() {
    return streamDeclarers;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public void setHostName(String host) {
    this.hostName = host;
  }

  public void setRoute(String route) {
    this.route = "/" + route + "/";
  }

  public String getBaseUri() {
    return "http://" + hostName + ":" + port + route;
  }

  private void checkAndStartExecutableStreams(SemanticEventProducerDeclarer sourceDeclarer) {
    sourceDeclarer.getEventStreams()
            .stream()
            .filter(DataStreamDeclarer::isExecutable)
            .forEach(es -> {
              es.declareModel(sourceDeclarer.declareModel());
              es.executeStream();
            });
  }
}
