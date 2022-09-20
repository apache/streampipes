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

public class StandaloneEventProcessorRuntime<B extends EventProcessorBindingParams> extends
    StandalonePipelineElementRuntime<B, DataProcessorInvocation,
        EventProcessorRuntimeParams<B>, EventProcessorRuntimeContext, EventProcessor<B>> {

  private static final Logger LOG = LoggerFactory.getLogger(StandaloneEventProcessorRuntime.class);

  public StandaloneEventProcessorRuntime(Supplier<EventProcessor<B>> supplier,
                                         EventProcessorRuntimeParams<B> params) {
    super(supplier, params);
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
            .get(0));
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
      engine.onEvent(params.makeEvent(rawEvent, sourceInfo), getOutputCollector());
    } catch (RuntimeException e) {
      LOG.error("RuntimeException while processing event in {}", engine.getClass().getCanonicalName(), e);
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

    getOutputCollector().connect();
  }

  @Override
  public void postDiscard() throws SpRuntimeException {
    for (SpInputCollector spInputCollector : getInputCollectors()) {
      spInputCollector.disconnect();
    }

    getOutputCollector().disconnect();
  }

  @Override
  public void bindEngine() throws SpRuntimeException {
    engine.onInvocation(params.getBindingParams(), getOutputCollector(), params.getRuntimeContext());
  }

}
