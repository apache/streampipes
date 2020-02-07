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

package org.apache.streampipes.processors.filters.jvm.processor.numericaltextfilter;

import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;

public class NumericalTextFilter implements EventProcessor<NumericalTextFilterParameters> {

  private NumericalTextFilterParameters params;

  @Override
  public void onInvocation(NumericalTextFilterParameters numericalTextFilterParameters, SpOutputCollector spOutputCollector, EventProcessorRuntimeContext
          runtimeContext) {
    this.params = numericalTextFilterParameters;
  }

  @Override
  public void onEvent(Event event, SpOutputCollector out) {
    boolean satisfiesNumberFilter = false;
    boolean satisfiesTextFilter = false;

    Double numbervalue = event.getFieldBySelector(params.getNumberProperty())
            .getAsPrimitive()
            .getAsDouble();

    String value = event.getFieldBySelector(params.getTextProperty())
            .getAsPrimitive()
            .getAsString();

    //Double value = Double.parseDouble(String.valueOf(in.get(params.getFilterProperty())));
    Double threshold = params.getNumberThreshold();

    if (params.getNumericalOperator() == NumericalOperator.EQ) {
      satisfiesNumberFilter = (Math.abs(numbervalue - threshold) < 0.000001);
    } else if (params.getNumericalOperator() == NumericalOperator.GE) {
      satisfiesNumberFilter = (numbervalue >= threshold);
    } else if (params.getNumericalOperator() == NumericalOperator.GT) {
      satisfiesNumberFilter = numbervalue > threshold;
    } else if (params.getNumericalOperator() == NumericalOperator.LE) {
      satisfiesNumberFilter = (numbervalue <= threshold);
    } else if (params.getNumericalOperator() == NumericalOperator.LT) {
      satisfiesNumberFilter = (numbervalue < threshold);
    } else if (params.getNumericalOperator() == NumericalOperator.IE) {
      satisfiesNumberFilter = (Math.abs(numbervalue - threshold) > 0.000001);
    }

    if (params.getTextOperator() == StringOperator.MATCHES) {
      satisfiesTextFilter = (value.equals(params.getTextKeyword()));
    } else if (params.getTextOperator() == StringOperator.CONTAINS) {
      satisfiesTextFilter = (value.contains(params.getTextKeyword()));
    }

    if (satisfiesNumberFilter && satisfiesTextFilter) {
      out.collect(event);
    }
  }

  @Override
  public void onDetach() {

  }
}
