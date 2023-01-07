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

import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;
import org.apache.streampipes.wrapper.siddhi.engine.callback.SiddhiDebugCallback;
import org.apache.streampipes.wrapper.siddhi.engine.generator.SiddhiInvocationConfigGenerator;
import org.apache.streampipes.wrapper.siddhi.utils.SiddhiUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SiddhiEventEngine<T extends EventProcessorBindingParams> implements
    EventProcessor<T>, SiddhiStatementGenerator<T> {

  private static final Logger LOG = LoggerFactory.getLogger(SiddhiEventEngine.class);

  private final SiddhiEngine siddhiEngine;

  public SiddhiEventEngine() {
    this.siddhiEngine = new SiddhiEngine();
  }

  public SiddhiEventEngine(SiddhiDebugCallback debugCallback) {
    this.siddhiEngine = new SiddhiEngine(debugCallback);
  }

  @Override
  public void onInvocation(T parameters, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) {
    SiddhiInvocationConfigGenerator<T> siddhiConfigGenerator = new SiddhiInvocationConfigGenerator<>(parameters,
        this::makeStatements);
    this.siddhiEngine.initializeEngine(siddhiConfigGenerator, spOutputCollector, runtimeContext);
  }

  @Override
  public void onEvent(org.apache.streampipes.model.runtime.Event event, SpOutputCollector collector) {
    this.siddhiEngine.processEvent(event);
  }

  @Override
  public void onDetach() {
    this.siddhiEngine.shutdownEngine();
  }

  public String prepareName(String name) {
    return SiddhiUtils.prepareName(name);
  }

}
