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
package org.apache.streampipes.wrapper.siddhi.engine.generator;

import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.wrapper.siddhi.SiddhiAppConfig;
import org.apache.streampipes.wrapper.siddhi.model.EventPropertyDef;
import org.apache.streampipes.wrapper.siddhi.model.SiddhiProcessorParams;
import org.apache.streampipes.wrapper.siddhi.utils.SiddhiUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class SiddhiInvocationConfigGenerator {

  private final String siddhiAppString;
  private final SiddhiAppConfig siddhiAppConfig;

  private final SiddhiProcessorParams siddhiProcessorParams;

  public SiddhiInvocationConfigGenerator(IDataProcessorParameters params,
                                         BiFunction<SiddhiProcessorParams,
                                             String, SiddhiAppConfig> statementFunction) {
    List<String> inputStreamNames = new InputStreamNameGenerator(params).generateInputStreamNames();
    Map<String, List<EventPropertyDef>> eventTypeInfo = new EventTypeGenerator(params).generateInEventTypes();
    List<EventPropertyDef> outTypeInfo = new EventTypeGenerator(params).generateOutEventTypes();
    List<String> outputEventKeys = new ArrayList<>(params.getOutEventType().keySet());
    this.siddhiProcessorParams =
        new SiddhiProcessorParams(params, inputStreamNames, eventTypeInfo, outputEventKeys, outTypeInfo);
    this.siddhiAppConfig = statementFunction.apply(siddhiProcessorParams, getOutputStreamName());
    this.siddhiAppString = new SiddhiAppGenerator(siddhiProcessorParams, siddhiAppConfig)
        .generateSiddhiApp();
  }

  private String getOutputStreamName() {
    return SiddhiUtils.prepareName(SiddhiUtils.getOutputTopicName(this.siddhiProcessorParams.getParams()));
  }

  public String getSiddhiAppString() {
    return siddhiAppString;
  }

  public SiddhiProcessorParams getSiddhiProcessorParams() {
    return siddhiProcessorParams;
  }

  public SiddhiAppConfig getSiddhiAppConfig() {
    return siddhiAppConfig;
  }
}
