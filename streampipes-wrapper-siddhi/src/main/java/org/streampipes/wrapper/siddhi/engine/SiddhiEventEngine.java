/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.streampipes.wrapper.siddhi.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;
import org.streampipes.wrapper.siddhi.manager.SpSiddhiManager;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.stream.output.StreamCallback;

import java.util.*;

public abstract class SiddhiEventEngine<T extends EventProcessorBindingParams> extends EventProcessor<T> {

  private StringBuilder siddhiAppString;

  private SiddhiAppRuntime siddhiAppRuntime;
  private Map<String, InputHandler> siddhiInputHandlers;
  private List<String> inputStreamNames;

  private static final Logger LOG = LoggerFactory.getLogger(SiddhiEventEngine.class);

  public SiddhiEventEngine(T params) {
    super(params);
    this.siddhiAppString = new StringBuilder();
    this.siddhiInputHandlers = new HashMap<>();
    this.inputStreamNames = new ArrayList<>();
  }

  @Override
  public void bind(T parameters, SpOutputCollector collector) {
    if (parameters.getInEventTypes().size() != parameters.getGraph().getInputStreams().size()) {
      throw new IllegalArgumentException("Input parameters do not match!");
    }

    SiddhiManager siddhiManager = SpSiddhiManager.INSTANCE.getSiddhiManager();

    LOG.info("Configuring event types for graph " + parameters.getGraph().getName());
    parameters.getInEventTypes().forEach((key, value) -> {
      Map inTypeMap = value;
      registerEventTypeIfNotExists(key, inTypeMap);
      this.inputStreamNames.add(fixEventName(key));
    });

    String fromStatement = fromStatement(inputStreamNames, parameters);
    String selectStatement = selectStatement(parameters);
    registerStatements(fromStatement, selectStatement, getOutputTopicName(parameters));

    siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(siddhiAppString.toString());
    parameters
            .getInEventTypes()
            .forEach((key, value) -> siddhiInputHandlers.put(key, siddhiAppRuntime.getInputHandler(fixEventName(key))));

    siddhiAppRuntime.addCallback(fixEventName(getOutputTopicName(parameters)), new StreamCallback() {
      @Override
      public void receive(Event[] events) {
        for(Event event : events) {
          collector.onEvent(toMap(event, parameters));
        }
      }
    });

  }

  private String getOutputTopicName(T parameters) {
    return parameters
            .getGraph()
            .getOutputStream()
            .getEventGrounding()
            .getTransportProtocol()
            .getTopicDefinition()
            .getActualTopicName();
  }

  private Map<String, Object> toMap(Event event, T parameters) {
    Map<String, Object> outMap = new HashMap<>();
    int i = 0;
    // TODO make sure that ordering of event attributes is correct
    for (String key : parameters.getOutEventType().keySet()) {
      outMap.put(key, event.getData(i));
      i++;
    }
    return outMap;
  }


  private void registerEventTypeIfNotExists(String eventTypeName, Map<String, Object> typeMap) {
    String defineStreamPrefix = "define stream " + fixEventName(eventTypeName);
    StringJoiner joiner = new StringJoiner(",");

    for (String key : typeMap.keySet()) {
      joiner.add(key + " " + toType((Class<?>) typeMap.get(key)));
    }

    this.siddhiAppString.append(defineStreamPrefix);
    this.siddhiAppString.append("(");
    this.siddhiAppString.append(joiner.toString());
    this.siddhiAppString.append(");\n");
  }

  private String toType(Class<?> o) {
    System.out.println(o.getCanonicalName());
    if (o.equals(Long.class)) {
      return "LONG";
    } else if (o.equals(Integer.class)) {
      return "INT";
    } else if (o.equals(Double.class)) {
      return "DOUBLE";
    } else if (o.equals(Float.class)) {
      return "FLOAT";
    } else if (o.equals(Boolean.class)) {
      return "BOOL";
    } else {
      return "STRING";
    }
  }

  private void registerStatements(String fromStatement, String selectStatement, String outputStream) {
      this.siddhiAppString.append(fromStatement)
              .append("\n")
              .append(selectStatement)
              .append("\n")
              .append("insert into ")
              .append(fixEventName(outputStream))
              .append(";");

      LOG.info("Registering statement: \n" +this.siddhiAppString.toString());

  }

  @Override
  public void onEvent(Map<String, Object> event, String sourceInfo) {
    try {
      siddhiInputHandlers.get(sourceInfo).send(toObjArr(event));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private Object[] toObjArr(Map<String, Object> event) {
    return event.values().toArray();
  }

  @Override
  public void discard() {
    this.siddhiAppRuntime.shutdown();
  }

  protected abstract String fromStatement(List<String> inputStreamNames, final T bindingParameters);
  protected abstract String selectStatement(final T bindingParameters);

  private String fixEventName(String eventName) {
    return eventName.replaceAll("\\.", "");
  }
}