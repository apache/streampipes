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

package org.streampipes.processors.filters.jvm.processor.threshold;

import org.streampipes.model.runtime.Event;
import org.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

public class ThresholdDetection implements EventProcessor<ThresholdDetectionParameters> {

  private ThresholdDetectionParameters params;

  @Override
  public void onInvocation(ThresholdDetectionParameters numericalFilterParameters, SpOutputCollector spOutputCollector, EventProcessorRuntimeContext
          runtimeContext) {
    this.params = numericalFilterParameters;
  }

  @Override
  public void onEvent(Event event, SpOutputCollector out) {
    Boolean satisfiesFilter = false;

    Double value = event.getFieldBySelector(params.getFilterProperty()).getAsPrimitive()
            .getAsDouble();

    //Double value = Double.parseDouble(String.valueOf(in.get(params.getFilterProperty())));
    Double threshold = params.getThreshold();

    if (params.getNumericalOperator() == ThresholdDetectionOperator.EQ) {
      satisfiesFilter = (Math.abs(value - threshold) < 0.000001);
    } else if (params.getNumericalOperator() == ThresholdDetectionOperator.GE) {
      satisfiesFilter = (value >= threshold);
    } else if (params.getNumericalOperator() == ThresholdDetectionOperator.GT) {
      satisfiesFilter = value > threshold;
    } else if (params.getNumericalOperator() == ThresholdDetectionOperator.LE) {
      satisfiesFilter = (value <= threshold);
    } else if (params.getNumericalOperator() == ThresholdDetectionOperator.LT) {
      satisfiesFilter = (value < threshold);
    } else if (params.getNumericalOperator() == ThresholdDetectionOperator.IE) {
      satisfiesFilter = (Math.abs(value - threshold) > 0.000001);
    }

    if (satisfiesFilter) {
      event.addField("thresholdDetected", true);
      out.collect(event);
    } else {
      event.addField("thresholdDetected", false);
      out.collect(event);
    }
  }

  @Override
  public void onDetach() {

  }
}
