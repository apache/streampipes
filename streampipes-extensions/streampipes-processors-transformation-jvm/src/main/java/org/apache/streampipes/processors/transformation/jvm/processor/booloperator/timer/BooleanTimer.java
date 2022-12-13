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

package org.apache.streampipes.processors.transformation.jvm.processor.booloperator.timer;

import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;

public class BooleanTimer implements EventProcessor<BooleanTimerParameters> {

  private static Logger log;

  private String fieldName;
  private boolean measureTrue;

  private Long timestamp;

  private double outputDivisor;


  @Override
  public void onInvocation(BooleanTimerParameters booleanInverterParameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) {
    log = booleanInverterParameters.getGraph().getLogger(BooleanTimer.class);
    this.fieldName = booleanInverterParameters.getFieldName();
    this.measureTrue = booleanInverterParameters.isMeasureTrue();
    this.timestamp = Long.MIN_VALUE;
    this.outputDivisor = booleanInverterParameters.getOutputDivisor();
  }

  @Override
  public void onEvent(Event inputEvent, SpOutputCollector out) {

    boolean field = inputEvent.getFieldBySelector(this.fieldName).getAsPrimitive().getAsBoolean();

    if (this.measureTrue == field) {
      if (timestamp == Long.MIN_VALUE) {
        timestamp = System.currentTimeMillis();
      }
    } else {
      if (timestamp != Long.MIN_VALUE) {
        Long difference = System.currentTimeMillis() - timestamp;

        double result = difference / this.outputDivisor;

        inputEvent.addField("measured_time", result);
        timestamp = Long.MIN_VALUE;
        out.collect(inputEvent);
      }
    }

  }

  @Override
  public void onDetach() {
  }

  public static void main(String... args) {

    double result = (60000L / 631.1);

    System.out.println(result);


  }
}
