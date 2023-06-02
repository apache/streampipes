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
package org.apache.streampipes.wrapper.siddhi.model;

import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.sdk.helpers.Tuple2;
import org.apache.streampipes.wrapper.siddhi.constants.SiddhiConstants;
import org.apache.streampipes.wrapper.siddhi.constants.SiddhiStreamSelector;
import org.apache.streampipes.wrapper.siddhi.utils.SiddhiUtils;

import java.util.List;
import java.util.Map;

public class SiddhiProcessorParams {

  private final IDataProcessorParameters params;
  private final List<String> inputStreamNames;
  private final Map<String, List<EventPropertyDef>> eventTypeInfo;
  private final List<String> outputEventKeys;
  private final List<EventPropertyDef> outTypeInfo;

  public SiddhiProcessorParams(IDataProcessorParameters params,
                               List<String> inputStreamNames,
                               Map<String, List<EventPropertyDef>> eventTypeInfo,
                               List<String> outputEventKeys,
                               List<EventPropertyDef> outTypeInfo) {
    this.params = params;
    this.inputStreamNames = inputStreamNames;
    this.eventTypeInfo = eventTypeInfo;
    this.outputEventKeys = outputEventKeys;
    this.outTypeInfo = outTypeInfo;
  }

  public IDataProcessorParameters getParams() {
    return params;
  }

  public List<String> getInputStreamNames() {
    return inputStreamNames;
  }

  public Map<String, List<EventPropertyDef>> getEventTypeInfo() {
    return eventTypeInfo;
  }

  public List<String> getOutputEventKeys() {
    return outputEventKeys;
  }

  public List<Tuple2<SiddhiStreamSelector, String>> getAllInputFieldNames() {
    //return this.params.getInEventTypes()
    return null;
  }

  public String getCustomOutputSelectStatement(DataProcessorInvocation invocation) {
    StringBuilder selectString = new StringBuilder();
    selectString.append(SiddhiConstants.SELECT).append(" ");

    if (outputEventKeys.size() > 0) {
      for (int i = 0; i < outputEventKeys.size() - 1; i++) {
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

  public String getOutputStreamName() {
    return SiddhiUtils.getPreparedOutputTopicName(params);
  }

  public List<EventPropertyDef> getOutTypeInfo() {
    return outTypeInfo;
  }
}
