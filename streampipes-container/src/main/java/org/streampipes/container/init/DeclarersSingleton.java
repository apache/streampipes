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

import org.streampipes.container.declarer.*;
import org.streampipes.dataformat.SpDataFormatFactory;
import org.streampipes.dataformat.SpDataFormatManager;
import org.streampipes.messaging.SpProtocolDefinitionFactory;
import org.streampipes.messaging.SpProtocolManager;

import java.util.ArrayList;
import java.util.List;

public class DeclarersSingleton {
  private static DeclarersSingleton instance;

  private List<SemanticEventProcessingAgentDeclarer> epaDeclarers;
  private List<SemanticEventProducerDeclarer> producerDeclarers;
  private List<SemanticEventConsumerDeclarer> consumerDeclarers;
  private List<PipelineTemplateDeclarer> pipelineTemplateDeclarers;
  private List<DataStreamDeclarer> streamDeclarers;

  private int port;
  private String route;
  private String hostName;


  private DeclarersSingleton() {
    this.epaDeclarers = new ArrayList<>();
    this.producerDeclarers = new ArrayList<>();
    this.consumerDeclarers = new ArrayList<>();
    this.streamDeclarers = new ArrayList<>();
    this.pipelineTemplateDeclarers = new ArrayList<>();
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

  public List<Declarer> getDeclarers() {
    List<Declarer> result = new ArrayList<>();
    result.addAll(epaDeclarers);
    result.addAll(producerDeclarers);
    result.addAll(consumerDeclarers);
    result.addAll(pipelineTemplateDeclarers);
    return result;
  }

  public void registerProtocol(SpProtocolDefinitionFactory<?> protocol) {
    SpProtocolManager.INSTANCE.register(protocol);
  }

  public void registerDataFormat(SpDataFormatFactory dataFormatDefinition) {
    SpDataFormatManager.INSTANCE.register(dataFormatDefinition);
  }

  private void addEpaDeclarer(SemanticEventProcessingAgentDeclarer epaDeclarer) {
    epaDeclarers.add(epaDeclarer);
  }

  private void addProducerDeclarer(SemanticEventProducerDeclarer sourceDeclarer) {
    checkAndStartExecutableStreams(sourceDeclarer);
    producerDeclarers.add(sourceDeclarer);
    streamDeclarers.addAll(sourceDeclarer.getEventStreams());
  }

  private void addConsumerDeclarer(SemanticEventConsumerDeclarer consumerDeclarer) {
    consumerDeclarers.add(consumerDeclarer);
  }

  private void addPipelineTemplateDeclarer(PipelineTemplateDeclarer pipelineTemplateDeclarer) {
    pipelineTemplateDeclarers.add(pipelineTemplateDeclarer);
  }

  public List<SemanticEventProcessingAgentDeclarer> getEpaDeclarers() {
    return epaDeclarers;
  }

  public List<SemanticEventProducerDeclarer> getProducerDeclarers() {
    return producerDeclarers;
  }

  public List<SemanticEventConsumerDeclarer> getConsumerDeclarers() {
    return consumerDeclarers;
  }

  public List<PipelineTemplateDeclarer> getPipelineTemplateDeclarers() {
    return pipelineTemplateDeclarers;
  }

  public List<DataStreamDeclarer> getStreamDeclarers() {
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
