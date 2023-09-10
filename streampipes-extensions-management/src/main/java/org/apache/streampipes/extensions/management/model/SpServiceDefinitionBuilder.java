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
package org.apache.streampipes.extensions.management.model;

import org.apache.streampipes.dataformat.SpDataFormatFactory;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.declarer.IStreamPipesFunctionDeclarer;
import org.apache.streampipes.extensions.api.pe.IStreamPipesPipelineElement;
import org.apache.streampipes.extensions.api.pe.runtime.IStreamPipesRuntimeProvider;
import org.apache.streampipes.messaging.SpProtocolDefinitionFactory;
import org.apache.streampipes.model.extensions.configuration.ConfigItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class SpServiceDefinitionBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(SpServiceDefinitionBuilder.class);

  private SpServiceDefinition serviceDefinition;
  //private SpConfig config;

  private SpServiceDefinitionBuilder(String serviceGroup,
                                     String serviceName,
                                     String serviceDescription,
                                     Integer defaultPort) {
    this.serviceDefinition = new SpServiceDefinition();
    this.serviceDefinition.setServiceGroup(serviceGroup);
    this.serviceDefinition.setServiceName(serviceName);
    this.serviceDefinition.setServiceDescription(serviceDescription);
    this.serviceDefinition.setDefaultPort(defaultPort);
    //this.config = new ConsulSpConfig(serviceGroup);
  }

  public static SpServiceDefinitionBuilder create(String serviceGroup,
                                                  String serviceName,
                                                  String serviceDescription,
                                                  Integer defaultPort) {
    return new SpServiceDefinitionBuilder(serviceGroup, serviceName, serviceDescription, defaultPort);
  }

  public SpServiceDefinitionBuilder withHostname(String hostname) {

    return this;
  }

  public SpServiceDefinitionBuilder addConfig(String key, String defaultValue, String description) {
    this.serviceDefinition.addConfig(ConfigItem.from(key, defaultValue, description));
    return this;
  }

  public SpServiceDefinitionBuilder addConfig(String key, Integer defaultValue, String description) {
    this.serviceDefinition.addConfig(ConfigItem.from(key, defaultValue, description));
    return this;
  }

  public SpServiceDefinitionBuilder addConfig(String key, Boolean defaultValue, String description) {
    this.serviceDefinition.addConfig(ConfigItem.from(key, defaultValue, description));
    return this;
  }

  public SpServiceDefinitionBuilder addConfig(ConfigItem configItem) {
    this.serviceDefinition.addConfig(configItem);
    return this;
  }

  public SpServiceDefinitionBuilder addConfigs(List<ConfigItem> configItems) {
    configItems.stream().forEach(configItem -> this.serviceDefinition.addConfig(configItem));
    return this;
  }

  public SpServiceDefinitionBuilder registerPipelineElement(IStreamPipesPipelineElement<?> pipelineElement) {
    this.serviceDefinition.addDeclarer(pipelineElement);
    return this;
  }

  public SpServiceDefinitionBuilder registerPipelineElements(IStreamPipesPipelineElement<?>... declarers) {
    this.serviceDefinition.addDeclarers(Arrays.asList(declarers));
    return this;
  }

  public SpServiceDefinitionBuilder registerAdapter(StreamPipesAdapter adapter) {
    this.serviceDefinition.addAdapter(adapter);
    return this;
  }


  public SpServiceDefinitionBuilder registerMessagingFormat(SpDataFormatFactory dataFormatDefinition) {
    this.serviceDefinition.addDataFormatFactory(dataFormatDefinition);
    return this;
  }

  public SpServiceDefinitionBuilder registerMessagingFormats(SpDataFormatFactory... dataFormatDefinitions) {
    this.serviceDefinition.addDataFormatFactories(Arrays.asList(dataFormatDefinitions));
    return this;
  }

  public SpServiceDefinitionBuilder registerMessagingProtocol(SpProtocolDefinitionFactory<?> protocol) {
    this.serviceDefinition.addProtocolDefinitionFactory(protocol);
    return this;
  }

  public SpServiceDefinitionBuilder registerMessagingProtocols(SpProtocolDefinitionFactory<?>... protocols) {
    this.serviceDefinition.addProtocolDefinitionFactories(Arrays.asList(protocols));
    return this;
  }

  public SpServiceDefinitionBuilder merge(SpServiceDefinition other) {
    this.serviceDefinition.addDeclarers(other.getDeclarers());
    this.serviceDefinition.addAdapters(other.getAdapters());
    other.getKvConfigs().forEach(value -> {
      if (this.serviceDefinition.getKvConfigs().stream().anyMatch(c -> c.getKey().equals(value.getKey()))) {
        LOG.warn("Config key {} already exists and will be overridden by merge, which might lead to strange results.",
            value.getKey());
      }
      this.serviceDefinition.addConfig(value);
    });
    return this;
  }

  public SpServiceDefinitionBuilder registerFunction(IStreamPipesFunctionDeclarer function) {
    this.serviceDefinition.addStreamPipesFunction(function);
    return this;
  }

  public SpServiceDefinitionBuilder registerRuntimeProvider(IStreamPipesRuntimeProvider runtimeProvider) {
    this.serviceDefinition.addRuntimeProvider(runtimeProvider);
    return this;
  }

  public SpServiceDefinition build() {
    return this.serviceDefinition;
  }
}
