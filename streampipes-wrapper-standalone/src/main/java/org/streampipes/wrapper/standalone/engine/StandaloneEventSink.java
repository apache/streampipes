/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.wrapper.standalone.engine;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.model.runtime.Event;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;
import org.streampipes.wrapper.runtime.EventSink;

import java.util.Map;

public abstract class StandaloneEventSink <B extends EventSinkBindingParams> extends EventSink<B> {

  private Boolean active;

  public StandaloneEventSink(B params) {
    super(params);
  }

  @Override
  public void bind(B parameters) throws SpRuntimeException {
    this.active = true;
    onInvocation(parameters);
  }

  @Override
  public void discard() throws SpRuntimeException {
    this.active = false;
    onDetach();
  }

  @Override
  public void onEvent(Map<String, Object> event, String sourceInfo) {
    if (active) {
      onEvent(makeEvent(event, sourceInfo));
    } else {
      throw new IllegalArgumentException("");
    }
  }

  public abstract void onInvocation(B params) throws SpRuntimeException;

  public abstract void onDetach() throws SpRuntimeException;

  public void onEvent(Event event) {

  }
}
