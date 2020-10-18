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

import org.apache.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.apache.streampipes.wrapper.siddhi.engine.generator.EventTypeGenerator;
import org.apache.streampipes.wrapper.siddhi.engine.generator.SiddhiAppGenerator;
import org.apache.streampipes.wrapper.siddhi.model.EventType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SiddhiInvocationConfig<B extends EventProcessorBindingParams> {

  private final B params;
  private final String siddhiAppString;
  private final List<String> inputStreamNames;
  private final Map<String, List<EventType>> eventTypeInfo;
  private final List<String> outputEventKeys;

  public SiddhiInvocationConfig(B params,
                                String fromStatement,
                                String selectStatement,
                                List<String> inputStreamNames) {
    this.params = params;
    this.inputStreamNames = inputStreamNames;
    this.eventTypeInfo = new EventTypeGenerator<>(params).generateEventTypes();
    this.siddhiAppString = new SiddhiAppGenerator<>(params, inputStreamNames, eventTypeInfo, fromStatement, selectStatement)
            .generateSiddhiApp();
    this.outputEventKeys = new ArrayList<>(this.params.getOutEventType().keySet());
  }

  public B getParams() {
    return params;
  }

  public String getSiddhiAppString() {
    return siddhiAppString;
  }

  public List<String> getInputStreamNames() {
    return inputStreamNames;
  }

  public Map<String, List<EventType>> getEventTypeInfo() {
    return eventTypeInfo;
  }

  public List<String> getOutputEventKeys() {
    return this.outputEventKeys;
  }

  public String getSiddhiApp() {
    return this.siddhiAppString;
  }


}
