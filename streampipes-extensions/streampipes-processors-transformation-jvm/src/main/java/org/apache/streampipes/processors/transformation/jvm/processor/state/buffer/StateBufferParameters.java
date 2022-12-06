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

package org.apache.streampipes.processors.transformation.jvm.processor.state.buffer;

import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class StateBufferParameters extends EventProcessorBindingParams {
  private String timeProperty;
  private String stateProperty;
  private String sensorValueProperty;

  public StateBufferParameters(DataProcessorInvocation graph, String timeProperty, String stateProperty,
                               String sensorValueProperty) {
    super(graph);
    this.timeProperty = timeProperty;
    this.stateProperty = stateProperty;
    this.sensorValueProperty = sensorValueProperty;
  }

  public String getTimeProperty() {
    return timeProperty;
  }

  public void setTimeProperty(String timeProperty) {
    this.timeProperty = timeProperty;
  }

  public String getStateProperty() {
    return stateProperty;
  }

  public void setStateProperty(String stateProperty) {
    this.stateProperty = stateProperty;
  }

  public String getSensorValueProperty() {
    return sensorValueProperty;
  }

  public void setSensorValueProperty(String sensorValueProperty) {
    this.sensorValueProperty = sensorValueProperty;
  }
}
