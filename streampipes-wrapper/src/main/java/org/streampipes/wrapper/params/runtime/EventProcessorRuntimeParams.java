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

package org.streampipes.wrapper.params.runtime;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.context.EventProcessorRuntimeContext;
import org.streampipes.wrapper.context.SpEventProcessorRuntimeContext;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

import java.util.Arrays;

public class EventProcessorRuntimeParams<B extends EventProcessorBindingParams> extends
        RuntimeParams<B, DataProcessorInvocation, EventProcessorRuntimeContext> { // B - Bind Type

  public EventProcessorRuntimeParams(B bindingParams, Boolean singletonEngine) {
    super(bindingParams, singletonEngine);
  }

  @Override
  protected EventProcessorRuntimeContext makeRuntimeContext() {
    return new SpEventProcessorRuntimeContext(Arrays.asList(getSourceInfo(0), getSourceInfo(1)),
            Arrays.asList(getSchemaInfo(0), getSchemaInfo(1)), bindingParams.getOutputStreamParams()
                    .getSourceInfo(), bindingParams.getOutputStreamParams().getSchemaInfo());
  }

}
