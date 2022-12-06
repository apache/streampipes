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

import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateBuffer implements EventProcessor<StateBufferParameters> {

  private static Logger log;
  private String timeProperty;
  private String stateProperty;
  private String sensorValueProperty;

  private Map<String, List> stateBuffer;

  @Override
  public void onInvocation(StateBufferParameters stateBufferParameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) {
    log = stateBufferParameters.getGraph().getLogger(StateBuffer.class);

    this.timeProperty = stateBufferParameters.getTimeProperty();
    this.stateProperty = stateBufferParameters.getStateProperty();
    this.sensorValueProperty = stateBufferParameters.getSensorValueProperty();
    this.stateBuffer = new HashMap<>();
  }

  @Override
  public void onEvent(Event inputEvent, SpOutputCollector out) {

    long timestamp = inputEvent.getFieldBySelector(this.timeProperty).getAsPrimitive().getAsLong();
    List<String> states = inputEvent.getFieldBySelector(this.stateProperty).getAsList().parseAsSimpleType(String.class);
    double value = inputEvent.getFieldBySelector(this.sensorValueProperty).getAsPrimitive().getAsDouble();

    // add value to state buffer
    for (String state : states) {
      if (stateBuffer.containsKey(state)) {
        stateBuffer.get(state).add(value);
      } else {
        List tmp = new ArrayList();
        tmp.add(value);
        stateBuffer.put(state, tmp);
      }
    }

    // emit event if state is not in event anymore
    List<String> keysToRemove = new ArrayList<>();
    for (String key : stateBuffer.keySet()) {
      if (!states.contains(key)) {
        Event resultEvent = new Event();
        resultEvent.addField(StateBufferController.VALUES, stateBuffer.get(key));
        resultEvent.addField(StateBufferController.STATE, Arrays.asList(key));
        resultEvent.addField(StateBufferController.TIMESTAMP, timestamp);
        out.collect(resultEvent);
        keysToRemove.add(key);
      }
    }

    for (String s : keysToRemove) {
      stateBuffer.remove(s);
    }
  }

  @Override
  public void onDetach() {
  }
}
