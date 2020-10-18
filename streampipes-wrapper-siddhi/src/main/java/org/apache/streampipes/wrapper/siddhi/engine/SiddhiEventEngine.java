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

import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;
import org.apache.streampipes.wrapper.siddhi.constants.SiddhiConstants;
import org.apache.streampipes.wrapper.siddhi.engine.callback.SiddhiDebugCallback;
import org.apache.streampipes.wrapper.siddhi.engine.generator.InputStreamNameGenerator;
import org.apache.streampipes.wrapper.siddhi.utils.SiddhiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class SiddhiEventEngine<B extends EventProcessorBindingParams> implements
        EventProcessor<B>, SiddhiStatementGenerator<B> {

  private static final Logger LOG = LoggerFactory.getLogger(SiddhiEventEngine.class);

  private SiddhiEngine siddhiEngine;
  private SiddhiInvocationConfig<B> siddhiConfig;
  private List<String> outputEventKeys = new ArrayList<>();

  public SiddhiEventEngine() {
    this.siddhiEngine = new SiddhiEngine();
  }

  public SiddhiEventEngine(SiddhiDebugCallback debugCallback) {
    this.siddhiEngine = new SiddhiEngine(debugCallback);
  }

  @Override
  public void onInvocation(B parameters, SpOutputCollector spOutputCollector, EventProcessorRuntimeContext runtimeContext) {
    List<String> inputStreamNames = new InputStreamNameGenerator<>(parameters).generateInputStreamNames();
    this.outputEventKeys = new ArrayList<>(parameters.getOutEventType().keySet());
    String fromStatement = fromStatement(inputStreamNames, parameters);
    String selectStatement = selectStatement(outputEventKeys, parameters);
    this.siddhiConfig = new SiddhiInvocationConfig<>(parameters, fromStatement, selectStatement, inputStreamNames);
    this.siddhiEngine.initializeEngine(this.siddhiConfig, spOutputCollector, runtimeContext);
  }

  @Override
  public void onEvent(org.apache.streampipes.model.runtime.Event event, SpOutputCollector collector) {
    this.siddhiEngine.processEvent(event);
  }

  @Override
  public void onDetach() {
    this.siddhiEngine.shutdownEngine();
  }

  public SiddhiInvocationConfig<B> getSiddhiConfig() {
    return this.siddhiConfig;
  }

  public String prepareName(String name) {
    return SiddhiUtils.prepareName(name);
  }

  public String getCustomOutputSelectStatement(DataProcessorInvocation invocation) {
    StringBuilder selectString = new StringBuilder();
    selectString.append(SiddhiConstants.SELECT).append(" ");

    if (outputEventKeys.size() > 0) {
      for (int i=0; i<outputEventKeys.size() - 1; i++) {
        selectString
                .append(SiddhiConstants.FIRST_STREAM_PREFIX)
                .append(outputEventKeys.get(i))
                .append(",");
      }
      selectString
              .append(SiddhiConstants.FIRST_STREAM_PREFIX)
              .append(outputEventKeys.get(outputEventKeys.size() - 1));
    }
    return selectString.toString();
  }

  public String getCustomOutputSelectStatement(DataProcessorInvocation invocation,
                                               String eventName) {
    StringBuilder selectString = new StringBuilder();
    selectString.append(SiddhiConstants.SELECT).append(" ");

    if (outputEventKeys.size() > 0) {
      for (int i = 0; i < outputEventKeys.size() - 1; i++) {
        selectString.append(eventName).append(".s0").append(outputEventKeys.get(i)).append(",");
      }
      selectString.append(eventName).append(".s0").append(outputEventKeys.get(outputEventKeys.size() - 1));

    }

    return selectString.toString();
  }
}
