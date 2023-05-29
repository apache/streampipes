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

import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.siddhi.engine.callback.SiddhiDebugCallback;
import org.apache.streampipes.wrapper.siddhi.engine.callback.SiddhiOutputStreamCallback;
import org.apache.streampipes.wrapper.siddhi.engine.callback.SiddhiOutputStreamDebugCallback;
import org.apache.streampipes.wrapper.siddhi.engine.generator.SiddhiInvocationConfigGenerator;
import org.apache.streampipes.wrapper.siddhi.manager.SpSiddhiManager;
import org.apache.streampipes.wrapper.siddhi.model.EventPropertyDef;
import org.apache.streampipes.wrapper.siddhi.utils.SiddhiUtils;

import io.siddhi.core.SiddhiAppRuntime;
import io.siddhi.core.SiddhiManager;
import io.siddhi.core.stream.input.InputHandler;
import io.siddhi.core.stream.output.StreamCallback;
import io.siddhi.query.api.definition.Attribute;
import io.siddhi.query.api.definition.StreamDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SiddhiEngine {

  private static final Logger LOG = LoggerFactory.getLogger(SiddhiEngine.class);

  private SiddhiAppRuntime siddhiAppRuntime;
  private final Map<String, InputHandler> siddhiInputHandlers;
  private Map<String, List<EventPropertyDef>> typeInfo;

  private Boolean debugMode;
  private SiddhiDebugCallback debugCallback;

  public SiddhiEngine() {
    this.siddhiInputHandlers = new HashMap<>();
    this.debugMode = false;
  }

  public SiddhiEngine(SiddhiDebugCallback debugCallback) {
    this();
    this.debugCallback = debugCallback;
    this.debugMode = true;
  }

  public void initializeEngine(SiddhiInvocationConfigGenerator settings,
                               SpOutputCollector spOutputCollector,
                               IDataProcessorParameters runtimeParameters) {

    IDataProcessorParameters params = settings.getSiddhiProcessorParams().getParams();
    this.typeInfo = settings.getSiddhiProcessorParams().getEventTypeInfo();
    SiddhiManager siddhiManager = SpSiddhiManager.INSTANCE.getSiddhiManager();

    siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(settings.getSiddhiAppString());
    settings.getSiddhiProcessorParams().getParams()
        .getInEventTypes()
        .forEach((key, value) -> {
          String preparedKey = SiddhiUtils.prepareName(key);
          siddhiInputHandlers.put(key, siddhiAppRuntime.getInputHandler(preparedKey));
        });

    StreamCallback callback;
    Map<String, StreamDefinition> streamDef = siddhiAppRuntime.getStreamDefinitionMap();
    String outputKey = SiddhiUtils.getPreparedOutputTopicName(params);
    List<Attribute> streamAttributes = streamDef.get(outputKey).getAttributeList();
    if (!debugMode) {
      callback = new SiddhiOutputStreamCallback(spOutputCollector,
          runtimeParameters,
          streamAttributes,
          settings.getSiddhiAppConfig().getOutputConfig());
    } else {
      callback = new SiddhiOutputStreamDebugCallback(debugCallback, settings.getSiddhiAppConfig().getOutputConfig());
    }
    LOG.info(SiddhiUtils.getPreparedOutputTopicName(params));
    siddhiAppRuntime.addCallback(SiddhiUtils.getPreparedOutputTopicName(params), callback);
    siddhiAppRuntime.start();
  }

  public void processEvent(org.apache.streampipes.model.runtime.Event event) {
    try {
      String sourceId = event.getSourceInfo().getSourceId();
      InputHandler inputHandler = siddhiInputHandlers.get(sourceId);
      List<String> eventKeys = this.typeInfo
          .get(sourceId)
          .stream()
          .map(EventPropertyDef::getFieldName)
          .collect(Collectors.toList());

      inputHandler.send(SiddhiUtils.toObjArr(eventKeys, event.getRaw()));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void shutdownEngine() {
    this.siddhiAppRuntime.shutdown();
  }

}
