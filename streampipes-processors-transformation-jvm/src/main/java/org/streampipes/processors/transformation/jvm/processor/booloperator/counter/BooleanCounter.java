/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.processors.transformation.jvm.processor.booloperator.counter;

import org.streampipes.logging.api.Logger;
import org.streampipes.model.runtime.Event;
import org.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

public class BooleanCounter implements EventProcessor<BooleanCounterParameters> {

  // From true to false or from false to true

  private static Logger LOG;

  private String fieldName;
  private int flankUp;

  private boolean fieldValueOfLastEvent;
  private int counter;



  @Override
  public void onInvocation(BooleanCounterParameters booleanCounterParametersParameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) {
    LOG = booleanCounterParametersParameters.getGraph().getLogger(BooleanCounter.class);
    this.fieldName = booleanCounterParametersParameters.getInvertFieldName();
    this.flankUp = booleanCounterParametersParameters.getFlankUp();

    if (flankUp == 1) {
        this.fieldValueOfLastEvent = true;
    } else {
        this.fieldValueOfLastEvent = false;
    }

    this.counter = 0;
  }

  @Override
  public void onEvent(Event inputEvent, SpOutputCollector out) {

      boolean value = inputEvent.getFieldBySelector(fieldName).getAsPrimitive().getAsBoolean();
      boolean updateCounter = false;

      if (this.flankUp == 2) {
        // detect up flanks
        if (this.fieldValueOfLastEvent == false && value == true) {
            updateCounter = true;
        }
      } else if (this.flankUp == 1){
        // detect up flanks
        if (this.fieldValueOfLastEvent == true && value == false) {
            updateCounter = true;
        }
      } else {
          if (this.fieldValueOfLastEvent != value) {
              updateCounter = true;
          }
      }

      if (updateCounter) {
          this.counter++;
          inputEvent.addField(BooleanCounterController.COUNT_FIELD_RUNTIME_NAME, this.counter);
          out.collect(inputEvent);
      }

      this.fieldValueOfLastEvent = value;
  }

  @Override
  public void onDetach() {
  }

}
