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
package org.apache.streampipes.wrapper.siddhi.engine;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataProcessor;
import org.apache.streampipes.extensions.api.pe.config.IDataProcessorConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sdk.builder.processor.DataProcessorConfiguration;
import org.apache.streampipes.wrapper.siddhi.engine.callback.SiddhiDebugCallback;
import org.apache.streampipes.wrapper.siddhi.engine.generator.SiddhiInvocationConfigGenerator;

public abstract class StreamPipesSiddhiProcessor
    implements IStreamPipesDataProcessor, SiddhiStatementGenerator {

  private final SiddhiEngine siddhiEngine;

  public StreamPipesSiddhiProcessor() {
    this.siddhiEngine = new SiddhiEngine();
  }

  public StreamPipesSiddhiProcessor(SiddhiDebugCallback debugCallback) {
    this.siddhiEngine = new SiddhiEngine(debugCallback);
  }

  @Override
  public void onPipelineStarted(IDataProcessorParameters params,
                                SpOutputCollector collector,
                                EventProcessorRuntimeContext runtimeContext) {
    SiddhiInvocationConfigGenerator siddhiConfigGenerator =
        new SiddhiInvocationConfigGenerator(params,
            this::makeStatements);
    this.siddhiEngine.initializeEngine(siddhiConfigGenerator, collector, params);
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
    this.siddhiEngine.processEvent(event);
  }

  @Override
  public void onPipelineStopped() {
    this.siddhiEngine.shutdownEngine();
  }

  @Override
  public IDataProcessorConfiguration declareConfig() {
    return DataProcessorConfiguration.create(
        () -> this,
        declareModel()
    );
  }

  public abstract DataProcessorDescription declareModel();
}
