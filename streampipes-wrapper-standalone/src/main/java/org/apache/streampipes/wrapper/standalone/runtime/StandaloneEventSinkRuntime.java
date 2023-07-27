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
import org.apache.streampipes.extensions.api.extractor.IDataSinkParameterExtractor;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataSink;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;
import org.apache.streampipes.extensions.api.pe.routing.RawDataProcessor;
import org.apache.streampipes.extensions.api.pe.routing.SpInputCollector;
import org.apache.streampipes.extensions.api.pe.runtime.IDataSinkRuntime;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.wrapper.context.generator.DataSinkContextGenerator;
import org.apache.streampipes.wrapper.params.generator.DataSinkParameterGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class StandaloneEventSinkRuntime extends StandalonePipelineElementRuntime<
    IStreamPipesDataSink,
    DataSinkInvocation,
    EventSinkRuntimeContext,
    IDataSinkParameterExtractor,
    IDataSinkParameters> implements IDataSinkRuntime, RawDataProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(StandaloneEventSinkRuntime.class);

  public StandaloneEventSinkRuntime() {
    super(new DataSinkContextGenerator(), new DataSinkParameterGenerator());
  }

  @Override
  public void process(Map<String, Object> rawEvent, String sourceInfo) {
    try {
      monitoringManager.increaseInCounter(instanceId, sourceInfo, System.currentTimeMillis());
      pipelineElement.onEvent(internalRuntimeParameters.makeEvent(runtimeParameters, rawEvent, sourceInfo));
    } catch (RuntimeException e) {
      LOG.error("RuntimeException while processing event in {}", pipelineElement.getClass().getCanonicalName(), e);
      addLogEntry(e);
    }
  }

  public void prepareRuntime() throws SpRuntimeException {
    for (SpInputCollector spInputCollector : getInputCollectors(runtimeParameters.getModel().getInputStreams())) {
      spInputCollector.connect();
    }
  }

  public void postDiscard() throws SpRuntimeException {
    for (SpInputCollector spInputCollector : inputCollectors) {
      spInputCollector.disconnect();
    }
  }

  @Override
  protected void beforeStart() {
    pipelineElement.onPipelineStarted(runtimeParameters, runtimeContext);
    inputCollectors.forEach(is -> is.registerConsumer(instanceId, this));
    prepareRuntime();
  }

  @Override
  protected void afterStop() {
    inputCollectors.forEach(is -> is.unregisterConsumer(instanceId));
    postDiscard();
  }
}
