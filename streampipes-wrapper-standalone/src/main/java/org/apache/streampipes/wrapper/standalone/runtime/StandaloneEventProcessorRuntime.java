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

package org.apache.streampipes.wrapper.standalone.runtime;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.apache.streampipes.wrapper.params.runtime.EventProcessorRuntimeParams;
import org.apache.streampipes.wrapper.routing.SpInputCollector;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;
import org.apache.streampipes.wrapper.standalone.manager.ProtocolManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Supplier;

public class StandaloneEventProcessorRuntime<T extends EventProcessorBindingParams> extends
    StandalonePipelineElementRuntime<T, DataProcessorInvocation,
        EventProcessorRuntimeParams<T>, EventProcessorRuntimeContext, EventProcessor<T>> {

  private static final Logger LOG = LoggerFactory.getLogger(StandaloneEventProcessorRuntime.class);

  protected SpOutputCollector outputCollector;

  public StandaloneEventProcessorRuntime(Supplier<EventProcessor<T>> supplier,
                                         EventProcessorRuntimeParams<T> params) {
    super(supplier, params);
    this.outputCollector = getOutputCollector();
  }


  public SpOutputCollector getOutputCollector() throws SpRuntimeException {
    return ProtocolManager.findOutputCollector(
        params
            .getBindingParams()
            .getGraph()
            .getOutputStream()
            .getEventGrounding()
            .getTransportProtocol(),
        params.getBindingParams()
            .getGraph()
            .getOutputStream()
            .getEventGrounding()
            .getTransportFormats()
            .get(0),
        this.resourceId);
  }

  @Override
  public void discardRuntime() throws SpRuntimeException {
    getInputCollectors().forEach(is -> is.unregisterConsumer(instanceId));
    discardEngine();
    postDiscard();
  }

  @Override
  public void process(Map<String, Object> rawEvent, String sourceInfo) {
    try {
      monitoringManager.increaseInCounter(resourceId, sourceInfo, System.currentTimeMillis());
      engine.onEvent(params.makeEvent(rawEvent, sourceInfo), outputCollector);
    } catch (RuntimeException e) {
      LOG.error("RuntimeException while processing event in {}", engine.getClass().getCanonicalName(), e);
      addLogEntry(e);
    }
  }

  @Override
  public void bindRuntime() throws SpRuntimeException {
    bindEngine();
    getInputCollectors().forEach(is -> is.registerConsumer(instanceId, this));
    prepareRuntime();
  }

  @Override
  public void prepareRuntime() throws SpRuntimeException {
    for (SpInputCollector spInputCollector : getInputCollectors()) {
      spInputCollector.connect();
    }

    outputCollector.connect();
  }

  @Override
  public void postDiscard() throws SpRuntimeException {
    for (SpInputCollector spInputCollector : getInputCollectors()) {
      spInputCollector.disconnect();
    }

    outputCollector.disconnect();
  }

  @Override
  public void bindEngine() throws SpRuntimeException {
    engine.onInvocation(params.getBindingParams(), getOutputCollector(), params.getRuntimeContext());
  }

}
