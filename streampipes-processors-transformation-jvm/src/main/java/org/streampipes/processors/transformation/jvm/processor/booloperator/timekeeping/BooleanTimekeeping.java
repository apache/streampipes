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

package org.streampipes.processors.transformation.jvm.processor.booloperator.timekeeping;

import org.streampipes.logging.api.Logger;
import org.streampipes.model.runtime.Event;
import org.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

import java.util.LinkedList;
import java.util.List;

public class BooleanTimekeeping implements EventProcessor<BooleanTimekeepingParameters> {

  private static Logger LOG;

  private String leftFieldName;
  private String rightFieldName;

  private boolean leftFieldLast;
  private boolean rightFieldLast;

  private LinkedList<Long> allPending;
  private Long counter;

  private double outputDivisor;

  @Override
  public void onInvocation(BooleanTimekeepingParameters booleanInverterParameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) {
    LOG = booleanInverterParameters.getGraph().getLogger(BooleanTimekeeping.class);
    this.leftFieldName = booleanInverterParameters.getLeftFieldName();
    this.rightFieldName = booleanInverterParameters.getRightFieldName();
    this.outputDivisor = booleanInverterParameters.getOutputDivisor();
    this.leftFieldLast = false;
    this.rightFieldLast = false;
    this.allPending = new LinkedList<>();
    this.counter = 0L;
  }

  @Override
  public void onEvent(Event inputEvent, SpOutputCollector out) {
    boolean leftField = inputEvent.getFieldBySelector(leftFieldName).getAsPrimitive().getAsBoolean();
    boolean rightField = inputEvent.getFieldBySelector(rightFieldName).getAsPrimitive().getAsBoolean();

    if (leftFieldLast == false && leftField == true) {
      this.allPending.push(System.currentTimeMillis());
    }

    if (rightFieldLast == false && rightField == true) {
      Long startTime = this.allPending.removeLast();
      if (startTime != null) {

        Long timeDifference  = System.currentTimeMillis() - startTime;

        double result = timeDifference  / this.outputDivisor;

        this.counter++;

        if (this.counter == Long.MAX_VALUE) {
          this.counter = 0L;
        }

        inputEvent.addField("measured_time", result);
        inputEvent.addField("counter", this.counter);

        out.collect(inputEvent);
      }
    }
  }

  @Override
  public void onDetach() {
  }

}
