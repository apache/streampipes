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
package org.apache.streampipes.container.model;

import org.apache.streampipes.config.SpConfig;
import org.apache.streampipes.config.consul.ConsulSpConfig;
import org.apache.streampipes.connect.api.Connector;
import org.apache.streampipes.connect.api.IAdapter;
import org.apache.streampipes.connect.api.IProtocol;
import org.apache.streampipes.container.declarer.Declarer;
import org.apache.streampipes.dataformat.SpDataFormatFactory;
import org.apache.streampipes.messaging.SpProtocolDefinitionFactory;

import java.util.Arrays;

public class SpServiceDefinitionBuilder {

  private SpServiceDefinition serviceDefinition;
  private SpConfig config;

  public static SpServiceDefinitionBuilder create(String serviceId,
                                                  String serviceName,
                                                  String serviceDescription,
                                                  Integer defaultPort) {
    return new SpServiceDefinitionBuilder(serviceId, serviceName, serviceDescription, defaultPort);
  }

  private SpServiceDefinitionBuilder(String serviceId,
                                     String serviceName,
                                     String serviceDescription,
                                     Integer defaultPort) {
    this.serviceDefinition = new SpServiceDefinition();
    this.serviceDefinition.setServiceId(serviceId);
    this.serviceDefinition.setServiceName(serviceName);
    this.serviceDefinition.setServiceDescription(serviceDescription);
    this.serviceDefinition.setDefaultPort(defaultPort);
    this.config = new ConsulSpConfig(serviceId);
  }

  public SpServiceDefinitionBuilder withHostname(String hostname) {

    return this;
  }

  public SpServiceDefinitionBuilder addConfig(String key, String defaultValue, String description) {
    this.config.register(key, defaultValue, description);
    return this;
  }

  public SpServiceDefinitionBuilder addConfig(String key, Integer defaultValue, String description) {
    this.config.register(key, defaultValue, description);
    return this;
  }

  public SpServiceDefinitionBuilder addConfig(String key, Boolean defaultValue, String description) {
    this.config.register(key, defaultValue, description);
    return this;
  }

  public SpServiceDefinitionBuilder registerPipelineElement(Declarer<?> declarer) {
    this.serviceDefinition.addDeclarer(declarer);
    return this;
  }

  public SpServiceDefinitionBuilder registerPipelineElements(Declarer<?>... declarers) {
    this.serviceDefinition.addDeclarers(Arrays.asList(declarers));
    return this;
  }

  public SpServiceDefinitionBuilder registerAdapter(Connector protocolOrAdapter) {
    if (protocolOrAdapter instanceof IProtocol) {
      this.serviceDefinition.addAdapterProtocol((IProtocol) protocolOrAdapter);
    } else if (protocolOrAdapter instanceof IAdapter<?>) {
      this.serviceDefinition.addSpecificAdapter((IAdapter<?>) protocolOrAdapter);
    }
    return this;
  }

  public SpServiceDefinitionBuilder registerAdapters(Connector... protocolOrAdapter) {
    Arrays.asList(protocolOrAdapter).forEach(this::registerAdapter);
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
    return this;
  }

  public SpServiceDefinition build() {
    return this.serviceDefinition;
  }
}
