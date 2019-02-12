/*
 * Copyright 2017 FZI Forschungszentrum Informatik
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
 */

package org.streampipes.processors.filters.jvm.processor.textfilter;

import org.streampipes.model.runtime.Event;
import org.streampipes.wrapper.context.RuntimeContext;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

public class TextFilter implements EventProcessor<TextFilterParameters> {

  private TextFilterParameters params;

  @Override
  public void onInvocation(TextFilterParameters textFilterParameters, RuntimeContext runtimeContext) {
    this.params = textFilterParameters;
  }

  @Override
  public void onEvent(Event event, SpOutputCollector out) {
    Boolean satisfiesFilter = false;
    String value = event.getFieldBySelector(params.getFilterProperty())
            .getAsPrimitive()
            .getAsString();

    if (params.getStringOperator() == StringOperator.MATCHES) {
      satisfiesFilter = (value.equals(params.getKeyword()));
    } else if (params.getStringOperator() == StringOperator.CONTAINS) {
      satisfiesFilter = (value.contains(params.getKeyword()));
    }

    if (satisfiesFilter) {
      out.collect(event);
    }
  }

  @Override
  public void onDetach() {

  }
}
