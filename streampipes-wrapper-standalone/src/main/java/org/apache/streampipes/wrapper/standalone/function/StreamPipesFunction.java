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

import org.apache.streampipes.commons.constants.Envs;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.container.declarer.IStreamPipesFunctionDeclarer;
import org.apache.streampipes.container.monitoring.SpMonitoringManager;
import org.apache.streampipes.container.util.GroundingDebugUtils;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.StreamPipesErrorMessage;
import org.apache.streampipes.model.monitoring.SpLogEntry;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.wrapper.routing.RawDataProcessor;
import org.apache.streampipes.wrapper.routing.SpInputCollector;
import org.apache.streampipes.wrapper.standalone.manager.ProtocolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class StreamPipesFunction implements IStreamPipesFunctionDeclarer, RawDataProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(StreamPipesFunction.class);
  private Map<String, SpInputCollector> inputCollectors;
  private final Map<String, String> sourceInfoMapper;

  public StreamPipesFunction() {
    this.sourceInfoMapper = new HashMap<>();
  }

  public void invokeRuntime() {
    FunctionContext context = new FunctionContextGenerator(
        this.getFunctionId().getId(), this.requiredStreamIds()
    ).generate();
    context.getStreams().forEach(stream -> sourceInfoMapper.put(getTopic(stream), stream.getElementId()));
    this.inputCollectors = getInputCollectors(context.getStreams());
    LOG.info("Invoking function {}:{}", this.getFunctionId().getId(), this.getFunctionId().getVersion());
    onServiceStarted(context);
    registerConsumers();
  }

  public void discardRuntime() {
    LOG.info("Discarding function {}:{}", this.getFunctionId().getId(), this.getFunctionId().getVersion());
    onServiceStopped();
    unregisterConsumers();
  }

  @Override
  public void process(Map<String, Object> rawEvent, String sourceInfo) {
    try {
      this.onEvent(EventFactory.fromMap(rawEvent), sourceInfoMapper.get(sourceInfo));
      increaseCounter(sourceInfo);
    } catch (RuntimeException e) {
      addError(e);
    }
  }

  private String getTopic(SpDataStream stream) {
    return stream.getEventGrounding().getTransportProtocol().getTopicDefinition().getActualTopicName();
  }

  private void increaseCounter(String sourceInfo) {
    SpMonitoringManager.INSTANCE.increaseInCounter(
        this.getFunctionId().getId(),
        sourceInfo,
        System.currentTimeMillis()
    );
  }

  private void addError(RuntimeException e) {
    SpMonitoringManager.INSTANCE.addErrorMessage(
        this.getFunctionId().getId(),
        SpLogEntry.from(System.currentTimeMillis(), StreamPipesErrorMessage.from(e)));
  }

  private Map<String, SpInputCollector> getInputCollectors(Collection<SpDataStream> streams) throws SpRuntimeException {
    Map<String, SpInputCollector> inputCollectors = new HashMap<>();
    for (SpDataStream is : streams) {
      if (Envs.SP_DEBUG.exists() && Envs.SP_DEBUG.getValueAsBoolean()) {
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

  public abstract void onServiceStarted(FunctionContext context);

  public abstract void onEvent(Event event,
                               String streamId);

  public abstract void onServiceStopped();

}
