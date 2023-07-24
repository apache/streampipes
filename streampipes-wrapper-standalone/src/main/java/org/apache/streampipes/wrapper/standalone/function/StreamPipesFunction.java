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

package org.apache.streampipes.wrapper.standalone.function;

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.declarer.IFunctionConfig;
import org.apache.streampipes.extensions.api.declarer.IStreamPipesFunctionDeclarer;
import org.apache.streampipes.extensions.api.monitoring.SpMonitoringManager;
import org.apache.streampipes.extensions.api.pe.routing.RawDataProcessor;
import org.apache.streampipes.extensions.api.pe.routing.SpInputCollector;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.extensions.management.util.GroundingDebugUtils;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.constants.PropertySelectorConstants;
import org.apache.streampipes.model.monitoring.SpLogEntry;
import org.apache.streampipes.model.monitoring.SpLogMessage;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.wrapper.standalone.manager.ProtocolManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class StreamPipesFunction implements IStreamPipesFunctionDeclarer, RawDataProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(StreamPipesFunction.class);
  private final Map<String, SourceInfo> sourceInfoMapper;
  private final Map<String, SchemaInfo> schemaInfoMapper;
  private Map<String, SpInputCollector> inputCollectors;

  private Map<String, SpOutputCollector> outputCollectors;

  public StreamPipesFunction() {
    this.sourceInfoMapper = new HashMap<>();
    this.schemaInfoMapper = new HashMap<>();
    this.outputCollectors = new HashMap<>();
  }

  @Override
  public void invokeRuntime(String serviceGroup) {
    var functionId = this.getFunctionConfig().getFunctionId();

    this.initializeProducers();

    var context = new FunctionContextGenerator(
        functionId.getId(),
        serviceGroup,
        this.requiredStreamIds(),
        this.outputCollectors
    ).generate();

    // Creates a source info for each incoming SpDataStream
    // The index is used to create the selector prefix for the SourceInfo
    AtomicInteger index = new AtomicInteger();
    context
        .getStreams()
        .forEach(stream -> {
          var topic = getTopic(stream);
          sourceInfoMapper.put(topic, createSourceInfo(stream, index.get()));
          schemaInfoMapper.put(topic, createSchemaInfo(stream.getEventSchema()));
          index.getAndIncrement();
        });

    this.inputCollectors = getInputCollectors(context.getStreams());

    LOG.info("Invoking function {}:{}", functionId.getId(), functionId.getVersion());

    onServiceStarted(context);
    registerConsumers();
  }

  public void discardRuntime() {
    var functionId = this.getFunctionConfig().getFunctionId();
    LOG.info("Discarding function {}:{}", functionId.getId(), functionId.getVersion());
    onServiceStopped();
    unregisterConsumers();
    this.outputCollectors.forEach((key, value) -> value.disconnect());
  }

  @Override
  public void process(Map<String, Object> rawEvent, String topicName) {
    try {
      var sourceInfo = sourceInfoMapper.get(topicName);

      var event = EventFactory
          .fromMap(rawEvent, sourceInfo, schemaInfoMapper.get(topicName));

      this.onEvent(event, sourceInfo.getSourceId());
      increaseCounter(sourceInfo.getSourceId());
    } catch (RuntimeException e) {
      addError(e);
    }
  }

  private String getTopic(SpDataStream stream) {
    return stream.getEventGrounding().getTransportProtocol().getTopicDefinition().getActualTopicName();
  }

  private void increaseCounter(String sourceInfo) {
    var functionId = this.getFunctionConfig().getFunctionId();
    SpMonitoringManager.INSTANCE.increaseInCounter(
        functionId.getId(),
        sourceInfo,
        System.currentTimeMillis()
    );
  }

  private void addError(RuntimeException e) {
    var functionId = this.getFunctionConfig().getFunctionId();
    SpMonitoringManager.INSTANCE.addErrorMessage(
        functionId.getId(),
        SpLogEntry.from(System.currentTimeMillis(), SpLogMessage.from(e)));
  }

  private void initializeProducers() {
    this.outputCollectors = this.getOutputCollectors();
    this.outputCollectors.forEach((key, value) -> value.connect());
  }

  private Map<String, SpOutputCollector> getOutputCollectors() {
    this.getFunctionConfig().getOutputDataStreams().forEach((key, value) -> {
      this.outputCollectors.put(
          key,
          ProtocolManager.makeOutputCollector(
              value.getEventGrounding().getTransportProtocol(),
              value.getEventGrounding().getTransportFormats().get(0),
              key));
    });

    return this.outputCollectors;
  }

  private Map<String, SpInputCollector> getInputCollectors(Collection<SpDataStream> streams) throws SpRuntimeException {
    Map<String, SpInputCollector> inputCollectors = new HashMap<>();
    var env = getEnvironment();
    for (SpDataStream is : streams) {
      if (env.getSpDebug().getValueOrDefault()) {
        GroundingDebugUtils.modifyGrounding(is.getEventGrounding());
      }
      inputCollectors.put(is.getElementId(), ProtocolManager.findInputCollector(is.getEventGrounding()
          .getTransportProtocol(), is.getEventGrounding().getTransportFormats().get(0), false));
    }
    return inputCollectors;
  }

  private void registerConsumers() {
    this.inputCollectors.forEach((key, is) -> {
      is.registerConsumer(key, this);
      is.connect();
    });
  }

  private void unregisterConsumers() {
    this.inputCollectors.forEach((key, is) -> {
      is.unregisterConsumer(key);
      is.disconnect();
    });
  }

  private SourceInfo createSourceInfo(SpDataStream stream, int streamIndex) {
    return new SourceInfo(
        stream.getElementId(),
        PropertySelectorConstants.STREAM_ID_PREFIX
            + streamIndex);
  }

  private SchemaInfo createSchemaInfo(EventSchema eventSchema) {
    return new SchemaInfo(eventSchema, new ArrayList<>());
  }

  private Environment getEnvironment() {
    return Environments.getEnvironment();
  }

  public abstract IFunctionConfig getFunctionConfig();

  public abstract void onServiceStarted(FunctionContext context);

  public abstract void onEvent(Event event,
                               String streamId);

  public abstract void onServiceStopped();

}
