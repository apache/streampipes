/*
 * Copyright 2018 FZI Forschungszentrum Informatik
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

package org.streampipes.processors.transformation.jvm.processor.value.duration;

import org.streampipes.logging.api.Logger;
import org.streampipes.model.runtime.Event;
import org.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

public class CalculateDuration implements EventProcessor<CalculateDurationParameters> {

  private static Logger LOG;

  private String startTs;
  private String endTs;
  private String unit;
  private String durationName;

  @Override
  public void onInvocation(CalculateDurationParameters calculateDurationParameters,
                            SpOutputCollector spOutputCollector,
                            EventProcessorRuntimeContext runtimeContext) {
    LOG = calculateDurationParameters.getGraph().getLogger(
        CalculateDuration.class);

    this.startTs = calculateDurationParameters.getStartTs();
    this.endTs= calculateDurationParameters.getEndTs();
    this.unit = calculateDurationParameters.getUnit();
    this.durationName = calculateDurationParameters.getDurationName();
  }

  @Override
  public void onEvent(Event inputEvent, SpOutputCollector out) {
    Long start = inputEvent.getFieldBySelector(startTs).getAsPrimitive().getAsLong();
    Long end = inputEvent.getFieldBySelector(endTs).getAsPrimitive().getAsLong();
    Long duration = end - start;

    if (unit.equals(CalculateDurationController.MS)) {
      inputEvent.addField(durationName, duration);
    } else if (unit.equals(CalculateDurationController.SECONDS)) {
      inputEvent.addField(durationName, (duration + 500) / 1000);
    } else if (unit.equals(CalculateDurationController.MINUTES)) {
      inputEvent.addField(durationName, (duration + 30000) / 60000);
    } else {
      // Hours
      inputEvent.addField(durationName, (duration + 1800000) / 3600000);
    }
    out.collect(inputEvent);
  }

  @Override
  public void onDetach() {
  }
}
