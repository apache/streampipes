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

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.standalone.engine.StandaloneEventProcessorEngine;

import java.util.Map;

public class TextFilter extends StandaloneEventProcessorEngine<TextFilterParameters> {

  private TextFilterParameters params;

  public TextFilter(TextFilterParameters params) {
    super(params);
  }

  @Override
  public void onInvocation(TextFilterParameters textFilterParameters, DataProcessorInvocation dataProcessorInvocation) {
    this.params = textFilterParameters;
  }

  @Override
  public void onEvent(Map<String, Object> in, String s, SpOutputCollector out) {
    Boolean satisfiesFilter = false;
    String value = String.valueOf(in.get(params.getFilterProperty()));

    if (params.getStringOperator() == StringOperator.MATCHES) {
      satisfiesFilter = (value.equals(params.getKeyword()));
    } else if (params.getStringOperator() == StringOperator.CONTAINS) {
      satisfiesFilter = (value.contains(params.getKeyword()));
    }

    if (satisfiesFilter) {
      out.onEvent(in);
    }
  }

  @Override
  public void onDetach() {

  }
}
