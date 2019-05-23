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

package org.streampipes.processors.transformation.jvm.processor.value.change;

import org.streampipes.logging.api.Logger;
import org.streampipes.model.runtime.Event;
import org.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

public class ChangedValueDetection implements EventProcessor<ChangedValueDetectionParameters> {

  private static Logger LOG;

  private String compareParameter;
  private String changeFieldName;
  private Object lastObject = null;

  @Override
  public void onInvocation(ChangedValueDetectionParameters changedValueDetectionParameters,
                            SpOutputCollector spOutputCollector,
                            EventProcessorRuntimeContext runtimeContext) {
    LOG = changedValueDetectionParameters.getGraph().getLogger(ChangedValueDetection.class);
    this.compareParameter = changedValueDetectionParameters.getCompareField();
    this.changeFieldName = changedValueDetectionParameters.getChangeFieldName();
  }

  @Override
  public void onEvent(Event inputEvent, SpOutputCollector out) {
    Object newObject = inputEvent.getFieldBySelector(compareParameter).getRawValue();

    if (newObject != null) {
      if (!newObject.equals(lastObject)) {
        lastObject = newObject;
        //TODO: Better handling of multiple timestamps (if the field "change_detected" is already in the input)?
        inputEvent.addField(changeFieldName, System.currentTimeMillis());
        out.collect(inputEvent);
      }
    }
  }

  @Override
  public void onDetach() {
  }
}
