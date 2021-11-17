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

package org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.state;

import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;

import java.util.ArrayList;
import java.util.List;

public class StringToState implements EventProcessor<StringToStateParameters> {

  private static Logger LOG;

  private List<String> stateFields;

  @Override
  public void onInvocation(StringToStateParameters booleanInverterParameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) {
    LOG = booleanInverterParameters.getGraph().getLogger(StringToState.class);
    this.stateFields = booleanInverterParameters.getStateFields();
  }

  @Override
  public void onEvent(Event inputEvent, SpOutputCollector out) {
    List<String> states = new ArrayList<>();

    for (String stateField : stateFields) {
      states.add(inputEvent.getFieldBySelector(stateField).getAsPrimitive().getAsString());
    }

    inputEvent.addField(StringToStateController.CURRENT_STATE, states.toArray());
    out.collect(inputEvent);
  }

  @Override
  public void onDetach() {
  }
}
