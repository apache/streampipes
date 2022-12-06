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

package org.apache.streampipes.processors.transformation.jvm.processor.booloperator.edge;

import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class SignalEdgeFilterParameters extends EventProcessorBindingParams {
  private String booleanSignalField;
  private String flank;
  private Integer delay;
  private String eventSelection;

  public SignalEdgeFilterParameters(DataProcessorInvocation graph, String booleanSignalField, String flank,
                                    Integer delay, String eventSelection) {
    super(graph);
    this.booleanSignalField = booleanSignalField;
    this.flank = flank;
    this.delay = delay;
    this.eventSelection = eventSelection;
  }

  public String getBooleanSignalField() {
    return booleanSignalField;
  }

  public void setBooleanSignalField(String booleanSignalField) {
    this.booleanSignalField = booleanSignalField;
  }

  public String getFlank() {
    return flank;
  }

  public void setFlank(String flank) {
    this.flank = flank;
  }

  public Integer getDelay() {
    return delay;
  }

  public void setDelay(Integer delay) {
    this.delay = delay;
  }

  public String getEventSelection() {
    return eventSelection;
  }

  public void setEventSelection(String eventSelection) {
    this.eventSelection = eventSelection;
  }
}
