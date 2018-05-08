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

package org.streampipes.wrapper.standalone;

import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.streampipes.wrapper.runtime.EventProcessor;

import java.util.function.Supplier;

public class ConfiguredEventProcessor<B extends EventProcessorBindingParams> {

  private B bindingParams;
  private Supplier<EventProcessor<B>> engineSupplier;

  public ConfiguredEventProcessor(B bindingParams, Supplier<EventProcessor<B>> engineSupplier) {
    this.bindingParams = bindingParams;
    this.engineSupplier = engineSupplier;
  }

  public B getBindingParams() {
    return bindingParams;
  }

  public Supplier<EventProcessor<B>> getEngineSupplier() {
    return engineSupplier;
  }
}
