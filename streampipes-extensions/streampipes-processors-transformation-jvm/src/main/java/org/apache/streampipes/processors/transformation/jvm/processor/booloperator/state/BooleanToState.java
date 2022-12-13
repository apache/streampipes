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

import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;

import java.util.List;
import java.util.Map;

public class BooleanToState implements EventProcessor<BooleanToStateParameters> {

  private static Logger log;

  private List<String> stateFields;
  private String defaultState;
  private Map<String, String> jsonConfiguration;

  @Override
  public void onInvocation(BooleanToStateParameters booleanInverterParameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) {
    log = booleanInverterParameters.getGraph().getLogger(BooleanToState.class);
    this.stateFields = booleanInverterParameters.getStateFields();
    this.defaultState = booleanInverterParameters.getDefaultState();
    this.jsonConfiguration = booleanInverterParameters.getJsonConfiguration();
  }

  @Override
  public void onEvent(Event inputEvent, SpOutputCollector out) {
    String state = this.defaultState;

    for (String stateField : stateFields) {
      if (inputEvent.getFieldBySelector(stateField).getAsPrimitive().getAsBoolean().equals(true)) {
        state = inputEvent.getFieldBySelector(stateField).getFieldNameIn();
      }
    }

    // replace the state string when user provided a mapping
    if (this.jsonConfiguration.containsKey(state)) {
      state = this.jsonConfiguration.get(state);
    }

    inputEvent.addField(BooleanToStateController.CURRENT_STATE, state);
    out.collect(inputEvent);
  }

  @Override
  public void onDetach() {
  }
}
