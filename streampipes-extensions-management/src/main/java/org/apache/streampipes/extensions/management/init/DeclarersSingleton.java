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

package org.apache.streampipes.extensions.management.init;

import org.apache.streampipes.dataformat.SpDataFormatFactory;
import org.apache.streampipes.dataformat.SpDataFormatManager;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.declarer.IStreamPipesFunctionDeclarer;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataProcessor;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataSink;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataStream;
import org.apache.streampipes.extensions.api.pe.IStreamPipesPipelineElement;
import org.apache.streampipes.extensions.api.pe.runtime.IStreamPipesRuntimeProvider;
import org.apache.streampipes.extensions.management.model.SpServiceDefinition;
import org.apache.streampipes.messaging.SpProtocolDefinitionFactory;
import org.apache.streampipes.messaging.SpProtocolManager;
import org.apache.streampipes.model.grounding.TransportFormat;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.model.util.Cloner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DeclarersSingleton implements IDeclarersSingleton {

  private static final Logger LOG = LoggerFactory.getLogger(DeclarersSingleton.class);
  private static final String Http = "http://";
  private static final String Colon = ":";
  private static final String Slash = "/";
  private static DeclarersSingleton instance;
  private SpServiceDefinition serviceDefinition;

  private final Map<String, IStreamPipesDataProcessor> dataProcessors;
  private final Map<String, IStreamPipesDataSink> dataSinks;
  private final Map<String, IStreamPipesDataStream> dataStreams;
  private final Map<String, IStreamPipesFunctionDeclarer> functions;

  private final Map<String, TransportProtocol> supportedProtocols;
  private final Map<String, TransportFormat> supportedFormats;

  private final Map<String, StreamPipesAdapter> adapters;

  private List<IStreamPipesRuntimeProvider> runtimeProviders;

  private String serviceId;
  private String serviceGroup;

  private int port;
  private String route;
  private String hostName;


  private DeclarersSingleton() {
    this.dataProcessors = new HashMap<>();
    this.dataSinks = new HashMap<>();
    this.dataStreams = new HashMap<>();
    this.supportedProtocols = new HashMap<>();
    this.supportedFormats = new HashMap<>();
    this.adapters = new HashMap<>();
    this.functions = new HashMap<>();
    this.runtimeProviders = new ArrayList<>();
    this.route = "/";
  }

  public static DeclarersSingleton getInstance() {
    if (DeclarersSingleton.instance == null) {
      DeclarersSingleton.instance = new DeclarersSingleton();
    }
    return DeclarersSingleton.instance;
  }


  public void populate(String host, Integer port, SpServiceDefinition serviceDef) {
    this.serviceDefinition = serviceDef;
    this.setHostName(host);
    this.setPort(port);
    this.addDeclarers(serviceDef.getDeclarers());
    this.serviceId = serviceDef.getServiceId();
    this.serviceGroup = serviceDef.getServiceGroup();
    this.registerProtocols(serviceDef.getProtocolDefinitionFactories());
    this.registerDataFormats(serviceDef.getDataFormatFactories());
    this.runtimeProviders = serviceDef.getRuntimeProviders();
    serviceDef.getAdapters().forEach(a -> this.adapters.put(a.declareConfig().getAdapterDescription().getAppId(), a));
    serviceDef.getFunctions().forEach(f -> this.functions.put(f.getFunctionConfig().getFunctionId().getId(), f));
  }

  public void addDeclarers(List<IStreamPipesPipelineElement<?>> allPipelineElements) {
    allPipelineElements.forEach(this::add);
  }

  @Deprecated
  /**
   * @Deprecated Use ServiceDefinitionBuilder instead
   */
  public DeclarersSingleton add(IStreamPipesPipelineElement<?> d) {
    if (d instanceof IStreamPipesDataProcessor) {
      addDataProcessor((IStreamPipesDataProcessor) d);
    } else if (d instanceof IStreamPipesDataStream) {
      addDataStream((IStreamPipesDataStream) d);
    } else if (d instanceof IStreamPipesDataSink) {
      addDataSink((IStreamPipesDataSink) d);
    }

    return getInstance();
  }

  public Map<String, IStreamPipesPipelineElement<?>> getDeclarers() {
    Map<String, IStreamPipesPipelineElement<?>> result = new HashMap<>();
    result.putAll(dataProcessors);
    result.putAll(dataStreams);
    result.putAll(dataSinks);
    //result.putAll(pipelineTemplateDeclarers);
    return result;
  }

  public void registerProtocol(SpProtocolDefinitionFactory<?> protocol) {
    SpProtocolManager.INSTANCE.register(protocol);
    this.supportedProtocols.put(protocol.getTransportProtocolClass(),
        protocol.getTransportProtocol());
  }

  public void registerProtocols(List<SpProtocolDefinitionFactory<?>> protocols) {
    protocols.forEach(this::registerProtocol);
  }

  public void registerDataFormat(SpDataFormatFactory dataFormatDefinition) {
    SpDataFormatManager.INSTANCE.register(dataFormatDefinition);
    this.supportedFormats.put(dataFormatDefinition.getTransportFormatRdfUri(),
        dataFormatDefinition.getTransportFormat());
  }

  public void registerDataFormats(SpDataFormatFactory... dataFormatDefinitions) {
    registerDataFormats(Arrays.asList(dataFormatDefinitions));
  }

  public void registerDataFormats(List<SpDataFormatFactory> dataFormatDefinitions) {
    dataFormatDefinitions.forEach(this::registerDataFormat);
  }

  private void addDataProcessor(IStreamPipesDataProcessor dataProcessor) {
    dataProcessors.put(dataProcessor.declareConfig().getDescription().getAppId(), dataProcessor);
  }

  private void addDataStream(IStreamPipesDataStream dataStream) {
    dataStreams.put(dataStream.declareConfig().getDescription().getAppId(), dataStream);
    checkAndStartExecutableStreams(dataStream);
  }

  private void addDataSink(IStreamPipesDataSink dataSink) {
    dataSinks.put(dataSink.declareConfig().getDescription().getAppId(), dataSink);
  }

  public Map<String, IStreamPipesDataProcessor> getDataProcessors() {
    return dataProcessors;
  }

  public Map<String, IStreamPipesDataStream> getDataStreams() {
    return dataStreams;
  }

  public Map<String, IStreamPipesDataSink> getDataSinks() {
    return dataSinks;
  }

  public Collection<TransportProtocol> getSupportedProtocols() {
    return this.supportedProtocols
        .values()
        .stream()
        .map(p -> new Cloner().protocol(p))
        .collect(Collectors.toList());
  }

  public Collection<TransportFormat> getSupportedFormats() {
    return this.supportedFormats.values()
        .stream()
        .map(TransportFormat::new)
        .collect(Collectors.toList());
  }

  public int getPort() {
    return this.port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getHostName() {
    return this.hostName;
  }

  public void setHostName(String host) {
    this.hostName = host;
  }

  public void setRoute(String route) {
    this.route = Slash + route + Slash;
  }

  public String getBaseUri() {
    return Http + hostName + Colon + port + route;
  }

  public String getServiceId() {
    return serviceId;
  }

  public Map<String, IStreamPipesFunctionDeclarer> getFunctions() {
    return functions;
  }

  public Collection<StreamPipesAdapter> getAdapters() {
    return adapters.values();
  }

  public Map<String, StreamPipesAdapter> getAdapterMap() {
    return adapters;
  }

  public void setAdapters(List<StreamPipesAdapter> adapters) {
    adapters.forEach(a -> this.adapters.put(a.declareConfig().getAdapterDescription().getAppId(), a));
  }

  public Optional<StreamPipesAdapter> getAdapter(String id) {
    return getAdapters().stream()
        .filter(adapter -> adapter.declareConfig()
            .getAdapterDescription()
            .getAppId().equals(id))
        .findFirst();
  }

  private void checkAndStartExecutableStreams(IStreamPipesDataStream declarer) {
    if (declarer.isExecutable()) {
      declarer.executeStream();
    }
  }

  public SpServiceDefinition toServiceDefinition(String serviceId) {
    SpServiceDefinition serviceDef = new SpServiceDefinition();
    serviceDef.setServiceId(serviceId);
    serviceDef.setDefaultPort(this.getPort());

    // TODO create complete service definition
    return serviceDef;
  }

  public String getServiceGroup() {
    return serviceGroup;
  }

  public SpServiceDefinition getServiceDefinition() {
    return this.serviceDefinition;
  }

  public List<IStreamPipesRuntimeProvider> getRuntimeProviders() {
    return runtimeProviders;
  }
}
