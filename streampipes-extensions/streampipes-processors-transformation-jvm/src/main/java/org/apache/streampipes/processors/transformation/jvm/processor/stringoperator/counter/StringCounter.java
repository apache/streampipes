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

package org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.counter;

import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;

import java.util.HashMap;

public class StringCounter implements EventProcessor<StringCounterParameters> {

  private static Logger LOG;

  private String selectedFieldName;
  private String fieldValueOfLastEvent;

  private HashMap<String, Integer> changeCounter;

  @Override
  public void onInvocation(StringCounterParameters stringCounterParametersParameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) {
    LOG = stringCounterParametersParameters.getGraph().getLogger(StringCounter.class);
    this.selectedFieldName = stringCounterParametersParameters.getSelectedFieldName();
    this.fieldValueOfLastEvent = "";
    this.changeCounter = new HashMap<>();
  }

  @Override
  public void onEvent(Event inputEvent, SpOutputCollector out) {

      String value = inputEvent.getFieldBySelector(selectedFieldName).getAsPrimitive().getAsString();
      String key = this.fieldValueOfLastEvent + ">" + value;
      boolean updateCounter = false;

      if (!this.fieldValueOfLastEvent.equals(value) && !this.fieldValueOfLastEvent.isEmpty()) {
          updateCounter = true;

          if (changeCounter.containsKey(key)) {
              changeCounter.put(key, changeCounter.get(key) + 1);
          } else {
              changeCounter.put(key, 1);
          }
      }

      if (updateCounter) {
          inputEvent.addField(StringCounterController.CHANGE_FROM_FIELD_RUNTIME_NAME, this.fieldValueOfLastEvent);
          inputEvent.addField(StringCounterController.CHANGE_TO_FIELD_RUNTIME_NAME, value);
          inputEvent.addField(StringCounterController.COUNT_FIELD_RUNTIME_NAME, changeCounter.get(key));
          out.collect(inputEvent);
      }

      this.fieldValueOfLastEvent = value;
  }

  @Override
  public void onDetach() {
  }

}
