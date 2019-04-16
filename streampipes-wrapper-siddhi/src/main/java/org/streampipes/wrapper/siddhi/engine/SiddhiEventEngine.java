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
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.runtime.EventFactory;
import org.streampipes.model.runtime.SchemaInfo;
import org.streampipes.model.runtime.SourceInfo;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.wrapper.context.EventProcessorRuntimeContext;
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

public abstract class SiddhiEventEngine<B extends EventProcessorBindingParams> implements
        EventProcessor<B> {

  private StringBuilder siddhiAppString;

  private SiddhiAppRuntime siddhiAppRuntime;
  private Map<String, InputHandler> siddhiInputHandlers;
  private List<String> inputStreamNames;

  private List<String> sortedEventKeys;

  private static final Logger LOG = LoggerFactory.getLogger(SiddhiEventEngine.class);

  public SiddhiEventEngine() {
    this.siddhiAppString = new StringBuilder();
    this.siddhiInputHandlers = new HashMap<>();
    this.inputStreamNames = new ArrayList<>();
    sortedEventKeys = new ArrayList<>();
  }

  @Override
  public void onInvocation(B parameters, SpOutputCollector spOutputCollector, EventProcessorRuntimeContext runtimeContext) {
    if (parameters.getInEventTypes().size() != parameters.getGraph().getInputStreams().size()) {
      throw new IllegalArgumentException("Input parameters do not match!");
    }

    SiddhiManager siddhiManager = SpSiddhiManager.INSTANCE.getSiddhiManager();


    LOG.info("Configuring event types for graph " + parameters.getGraph().getName());
    parameters.getInEventTypes().forEach((key, value) -> {
      // TODO why is the prefix not in the parameters.getInEventType
      registerEventTypeIfNotExists( key, value);
      this.inputStreamNames.add(prepareName(key));
    });

    String fromStatement = fromStatement(inputStreamNames, parameters);
    String selectStatement = selectStatement(parameters);
    registerStatements(fromStatement, selectStatement, getOutputTopicName(parameters));

    siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(siddhiAppString.toString());
    parameters
            .getInEventTypes()
            .forEach((key, value) -> siddhiInputHandlers.put(key, siddhiAppRuntime.getInputHandler(prepareName(key))));

    siddhiAppRuntime.addCallback(prepareName(getOutputTopicName(parameters)), new StreamCallback() {
      @Override
      public void receive(Event[] events) {
        for(Event event : events) {
          // TODO provide collector in RuntimeContext
          spOutputCollector.collect(toSpEvent(event, parameters, runtimeContext.getOutputSchemaInfo
                  (), runtimeContext.getOutputSourceInfo()));
        }
      }
    });

  }

  private String getOutputTopicName(B parameters) {
    return parameters
            .getGraph()
            .getOutputStream()
            .getEventGrounding()
            .getTransportProtocol()
            .getTopicDefinition()
            .getActualTopicName();
  }

  private org.streampipes.model.runtime.Event toSpEvent(Event event, B parameters, SchemaInfo
          schemaInfo,
                                                        SourceInfo sourceInfo) {
    Map<String, Object> outMap = new HashMap<>();
    for (int i = 0; i < sortedEventKeys.size(); i++) {
      outMap.put(sortedEventKeys.get(i), event.getData(i));
    }
    return EventFactory.fromMap(outMap, sourceInfo, schemaInfo);
  }


  private void registerEventTypeIfNotExists(String eventTypeName, Map<String, Object> typeMap) {
    String defineStreamPrefix = "define stream " + prepareName(eventTypeName);
    StringJoiner joiner = new StringJoiner(",");

    for (String key : typeMap.keySet()) {
      sortedEventKeys.add(key);
      Collections.sort(sortedEventKeys);
    };

    for (String key : sortedEventKeys) {
      joiner.add("s0" + key + " " + toType((Class<?>) typeMap.get(key)));
    }

    this.siddhiAppString.append(defineStreamPrefix);
    this.siddhiAppString.append("(");
    this.siddhiAppString.append(joiner.toString());
    this.siddhiAppString.append(");\n");
  }

  private String toType(Class<?> o) {
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
            .append(prepareName(outputStream))
            .append(";");

    LOG.info("Registering statement: \n" +this.siddhiAppString.toString());

  }

  @Override
  public void onEvent(org.streampipes.model.runtime.Event event, SpOutputCollector collector) {
    try {
      siddhiInputHandlers.get(event.getSourceInfo().getSourceId()).send(toObjArr(event.getRaw()));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private Object[] toObjArr(Map<String, Object> event) {
    Object[] result = new Object[sortedEventKeys.size()];
    for (int i = 0; i < sortedEventKeys.size(); i++) {
      result[i] = event.get(sortedEventKeys.get(i));
    }

    return result;
  }

  @Override
  public void onDetach() {
    this.siddhiAppRuntime.shutdown();
  }

  protected abstract String fromStatement(List<String> inputStreamNames, final B bindingParameters);
  protected abstract String selectStatement(final B bindingParameters);

  protected String prepareName(String eventName) {
    return eventName
            .replaceAll("\\.", "")
//            .replaceAll("VVVVV", "\\.")
            .replaceAll("::", "");
  }


  protected String getCustomOutputSelectStatement(DataProcessorInvocation invocation) {
    StringBuilder selectString = new StringBuilder();
    selectString.append("select ");

    List<EventProperty> eventProperties = invocation.getOutputStream().getEventSchema().getEventProperties();

    if (eventProperties.size() > 0) {
      for (int i = 0; i < eventProperties.size() - 1; i++) {
        selectString.append("e1.s0" + eventProperties.get(i).getRuntimeName() + ",");
      }

      selectString.append("e1.s0" + eventProperties.get(eventProperties.size() - 1).getRuntimeName());
    }

    return selectString.toString();
  }

  public void setSortedEventKeys(List<String> sortedEventKeys) {
    this.sortedEventKeys = sortedEventKeys;
  }
}