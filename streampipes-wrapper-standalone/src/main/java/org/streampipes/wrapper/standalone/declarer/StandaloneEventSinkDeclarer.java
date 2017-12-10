package org.streampipes.wrapper.standalone.declarer;

import org.streampipes.wrapper.declarer.EventSinkDeclarer;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;
import org.streampipes.wrapper.runtime.EventSink;
import org.streampipes.wrapper.standalone.param.StandaloneEventSinkRuntimeParams;
import org.streampipes.wrapper.standalone.runtime.StandaloneEventSinkRuntime;

import java.util.function.Supplier;

public abstract class StandaloneEventSinkDeclarer<B extends
        EventSinkBindingParams> extends EventSinkDeclarer<B, StandaloneEventSinkRuntime> {

  @Override
  public StandaloneEventSinkRuntime prepareRuntime(B bindingParameters,
                                                        Supplier<EventSink<B>> supplier) {

    StandaloneEventSinkRuntimeParams<B> runtimeParams = new StandaloneEventSinkRuntimeParams<>
            (supplier, bindingParameters, false);

    return new StandaloneEventSinkRuntime(runtimeParams);
  }

}
