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

package org.apache.streampipes.wrapper.params.binding;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.grounding.EventGrounding;
import org.apache.streampipes.model.output.PropertyRenameRule;
import org.apache.streampipes.model.util.SchemaUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Deprecated(since = "0.70.0", forRemoval = true)
public abstract class EventProcessorBindingParams extends
        BindingParams<DataProcessorInvocation> implements
        Serializable {

  private static final long serialVersionUID = 7716492945641719007L;

  private SpDataStream outputStream;
  private String outName;

  private final Map<String, Object> outEventType;
  private OutputStreamParams outputStreamParams;


  private final static String topicPrefix = "topic://";

  public EventProcessorBindingParams(DataProcessorInvocation graph) {
    super(new DataProcessorInvocation(graph));
    this.outEventType = SchemaUtils.toRuntimeMap(graph.getOutputStream().getEventSchema().getEventProperties());
    this.outputStreamParams = new OutputStreamParams(graph.getOutputStream(), getRenameRules());
    outputStream = graph.getOutputStream();
    EventGrounding outputGrounding = outputStream.getEventGrounding();
    outName = outputGrounding.getTransportProtocol().getTopicDefinition().getActualTopicName();

  }

  public String getOutName() {
    return outName;
  }

  public Map<String, Object> getOutEventType() {
    return outEventType;
  }

  public List<String> getOutputProperties() {
    return SchemaUtils.toPropertyList(outputStream.getEventSchema().getEventProperties());
  }

  @Override
  public List<PropertyRenameRule> getRenameRules() {
    return graph.getOutputStrategies().stream().flatMap(o -> o.getRenameRules().stream()).collect
            (Collectors.toList());
  }

  public OutputStreamParams getOutputStreamParams() {
    return outputStreamParams;
  }

}
