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

package org.apache.streampipes.processors.geo.jvm.processor.distancecalculator;

import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.processors.geo.jvm.processor.util.DistanceUtil;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;

public class DistanceCalculator implements EventProcessor<DistanceCalculatorParameters> {

  private DistanceCalculatorParameters params;

  @Override
  public void onInvocation(DistanceCalculatorParameters numericalFilterParameters, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext
                               runtimeContext) {
    this.params = numericalFilterParameters;
  }

  @Override
  public void onEvent(Event event, SpOutputCollector out) {

    float lat1 = event.getFieldBySelector(this.params.getLat1PropertyName()).getAsPrimitive().getAsFloat();
    float long1 = event.getFieldBySelector(this.params.getLong1PropertyName()).getAsPrimitive().getAsFloat();
    float lat2 = event.getFieldBySelector(this.params.getLat2PropertyName()).getAsPrimitive().getAsFloat();
    float long2 = event.getFieldBySelector(this.params.getLong2PropertyName()).getAsPrimitive().getAsFloat();

    double resultDist = DistanceUtil.dist(lat1, long1, lat2, long2);

    event.addField("distance", resultDist);

    out.collect(event);
  }

  @Override
  public void onDetach() {

  }

}
