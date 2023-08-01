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
import org.apache.streampipes.extensions.api.extractor.IDataProcessorParameterExtractor;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataProcessor;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.extensions.api.pe.routing.RawDataProcessor;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.extensions.api.pe.runtime.IDataProcessorRuntime;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.wrapper.context.generator.DataProcessorContextGenerator;
import org.apache.streampipes.wrapper.params.generator.DataProcessorParameterGenerator;
import org.apache.streampipes.wrapper.standalone.manager.ProtocolManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class StandaloneEventProcessorRuntime extends StandalonePipelineElementRuntime<
    IStreamPipesDataProcessor,
    DataProcessorInvocation,
    EventProcessorRuntimeContext,
    IDataProcessorParameterExtractor,
    IDataProcessorParameters> implements IDataProcessorRuntime, RawDataProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(StandaloneEventProcessorRuntime.class);

  protected SpOutputCollector outputCollector;

  public StandaloneEventProcessorRuntime() {
    super(new DataProcessorContextGenerator(), new DataProcessorParameterGenerator());
  }

  public SpOutputCollector getOutputCollector() throws SpRuntimeException {
    return ProtocolManager.findOutputCollector(
        runtimeParameters
            .getModel()
            .getOutputStream()
            .getEventGrounding()
            .getTransportProtocol(),
        runtimeParameters
            .getModel()
            .getOutputStream()
            .getEventGrounding()
            .getTransportFormats()
            .get(0),
        this.instanceId);
  }

  @Override
  public void process(Map<String, Object> rawEvent, String sourceInfo) {
    try {
      monitoringManager.increaseInCounter(instanceId, sourceInfo, System.currentTimeMillis());
      var event = this.internalRuntimeParameters.makeEvent(runtimeParameters, rawEvent, sourceInfo);
      pipelineElement
          .onEvent(event, outputCollector);
    } catch (RuntimeException e) {
      LOG.error("RuntimeException while processing event in {}", pipelineElement.getClass().getCanonicalName(), e);
      addLogEntry(e);
    }
  }

  public void prepareRuntime() throws SpRuntimeException {
    registerInputCollectors();
    outputCollector.connect();
    connectInputCollectors();
  }

  @Override
  protected void beforeStart() {
    this.outputCollector = getOutputCollector();
    pipelineElement.onPipelineStarted(runtimeParameters, outputCollector, runtimeContext);
    prepareRuntime();
  }

  @Override
  protected void afterStop() {
    disconnectInputCollectors();
    outputCollector.disconnect();
  }

}
