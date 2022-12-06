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

package org.apache.streampipes.processors.transformation.jvm.processor.transformtoboolean;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.field.AbstractField;
import org.apache.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.apache.streampipes.wrapper.routing.SpOutputCollector;
import org.apache.streampipes.wrapper.runtime.EventProcessor;

import java.util.List;

public class TransformToBoolean implements EventProcessor<TransformToBooleanParameters> {

  private static Logger log;

  private List<String> transformFields;

  @Override
  public void onInvocation(TransformToBooleanParameters transformToBooleanParameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) {

    log = transformToBooleanParameters.getGraph().getLogger(TransformToBoolean.class);
    this.transformFields = transformToBooleanParameters.getTransformFields();
  }

  @Override
  public void onEvent(Event inputEvent, SpOutputCollector out) {
    for (String transformField : transformFields) {
      AbstractField field = inputEvent.getFieldBySelector(transformField);
      // Is the field a primitive (and no list/nested field)?
      if (field.isPrimitive()) {
        // Yes. So remove the element and replace it with a boolean (if possible)
        inputEvent.removeFieldBySelector(transformField);
        try {
          inputEvent.addField(transformField, toBoolean(field.getRawValue()));
        } catch (SpRuntimeException e) {
          log.info(e.getMessage());
          return;
        }
      }
    }
    out.collect(inputEvent);
  }

  @Override
  public void onDetach() {
  }

  private Boolean toBoolean(Object value) throws SpRuntimeException {
    String s = value.toString().toLowerCase();
    // If it is a double, maybe add some delta here?
    if (s.equals("true") || s.equals("1") || s.equals("1.0")) {
      return true;
    } else if (s.equals("false") || s.equals("0") || s.equals("0.0")) {
      return false;
    } else {
      throw new SpRuntimeException("Value " + s + " not convertible to boolean");
    }
  }
}
