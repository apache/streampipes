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

package org.apache.streampipes.processors.transformation.jvm.processor.booloperator.state;

import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.wrapper.params.binding.EventProcessorBindingParams;

import java.util.List;
import java.util.Map;

public class BooleanToStateParameters extends EventProcessorBindingParams {
  private List<String> stateFields;
  private String defaultState;
  private Map<String, String> jsonConfiguration;

  public BooleanToStateParameters(DataProcessorInvocation graph, List<String> stateFields, String defaultState,
                                  Map<String, String> jsonConfiguration) {
    super(graph);
    this.stateFields = stateFields;
    this.defaultState = defaultState;
    this.jsonConfiguration = jsonConfiguration;
  }

  public List<String> getStateFields() {
    return stateFields;
  }

  public void setStateFields(List<String> stateFields) {
    this.stateFields = stateFields;
  }

  public String getDefaultState() {
    return defaultState;
  }

  public void setDefaultState(String defaultState) {
    this.defaultState = defaultState;
  }

  public Map<String, String> getJsonConfiguration() {
    return jsonConfiguration;
  }

  public void setJsonConfiguration(Map<String, String> jsonConfiguration) {
    this.jsonConfiguration = jsonConfiguration;
  }
}
