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

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;
import org.streampipes.wrapper.runtime.EventSink;

import java.util.function.Supplier;

public abstract class EventSinkRuntimeParams<B extends EventSinkBindingParams> extends
        RuntimeParams<B, EventSink<B>> {

  public EventSinkRuntimeParams(Supplier<EventSink<B>> supplier, B bindingParams) {
    super(supplier, bindingParams);
  }

  @Override
  public void bindEngine() throws SpRuntimeException {
    engine.bind(bindingParams);
  }

  public void discardEngine() throws SpRuntimeException {
    engine.discard();
  }




}
