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

package org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.timer;

import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;

public class StringTimer implements EventProcessor<StringTimerParameters> {

  private static Logger LOG;

  private String selectedFieldName;
  private Long timestamp;
  private double outputDivisor;
  private String fieldValueOfLastEvent;
  private boolean useInputFrequencyForOutputFrequency;

  @Override
  public void onInvocation(StringTimerParameters stringTimerParameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) {
    LOG = stringTimerParameters.getGraph().getLogger(StringTimer.class);
    this.selectedFieldName = stringTimerParameters.getSelectedFieldName();
    this.outputDivisor = stringTimerParameters.getOutputDivisor();
    this.useInputFrequencyForOutputFrequency = stringTimerParameters.isUseInputFrequencyForOutputFrequency();

  }

  @Override
  public void onEvent(Event inputEvent, SpOutputCollector out) {

      String value = inputEvent.getFieldBySelector(selectedFieldName).getAsPrimitive().getAsString();
      Long currentTime = System.currentTimeMillis();

      if (this.fieldValueOfLastEvent == null) {
          this.timestamp = currentTime;
      } else {
          // Define if result event should be emitted or not
          if (this.useInputFrequencyForOutputFrequency || !this.fieldValueOfLastEvent.equals(value)) {
              Long difference = currentTime - this.timestamp;
              double result = difference / this.outputDivisor;

              inputEvent.addField(StringTimerController.MEASURED_TIME_FIELD_RUNTIME_NAME, result);
              inputEvent.addField(StringTimerController.FIELD_VALUE_RUNTIME_NAME, this.fieldValueOfLastEvent);
              out.collect(inputEvent);
          }

          // if state changes reset timestamp
          if (!this.fieldValueOfLastEvent.equals(value)) {
              timestamp = currentTime;
          }

      }
      this.fieldValueOfLastEvent = value;
  }

  @Override
  public void onDetach() {
  }
}
