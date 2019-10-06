/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License at
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
package org.streampipes.processors.transformation.jvm.processor.task;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.model.runtime.Event;
import org.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

public class TaskDuration implements EventProcessor<TaskDurationParameters> {

  private String taskFieldSelector;
  private String timestampFieldSelector;

  private String lastValue;
  private Long lastTimestamp;

  @Override
  public void onInvocation(TaskDurationParameters parameters, SpOutputCollector spOutputCollector, EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    this.taskFieldSelector = parameters.getTaskFieldSelector();
    this.timestampFieldSelector = parameters.getTimestampFieldSelector();
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
    String taskValue = event.getFieldBySelector(taskFieldSelector).getAsPrimitive().getAsString();
    Long timestampValue =
            event.getFieldBySelector(timestampFieldSelector).getAsPrimitive().getAsLong();

    if (lastValue == null) {
      this.lastValue = taskValue;
      this.lastTimestamp = timestampValue;
    } else {
      if (!this.lastValue.equals(taskValue)) {
        Long duration = timestampValue - this.lastTimestamp;

        Event outEvent = new Event();
        outEvent.addField("processId", makeProcessId(taskValue));
        outEvent.addField("duration", duration);

        this.lastValue = taskValue;
        this.lastTimestamp = timestampValue;

        collector.collect(outEvent);
      }
    }
  }

  private String makeProcessId(String taskValue) {
    return this.lastValue + "-" + taskValue;
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    this.lastValue = null;
  }
}
