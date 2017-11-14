package org.streampipes.wrapper.standalone.param;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.model.SpDataStream;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;
import org.streampipes.wrapper.params.runtime.EventSinkRuntimeParams;
import org.streampipes.wrapper.routing.SpInputCollector;
import org.streampipes.wrapper.runtime.EventSink;
import org.streampipes.wrapper.standalone.manager.ProtocolManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class StandaloneEventSinkRuntimeParams<B extends EventSinkBindingParams> extends
        EventSinkRuntimeParams<B> {

  Boolean singletonEngine;

  public StandaloneEventSinkRuntimeParams(Supplier<EventSink<B>> supplier, B bindingParams, Boolean
          singletonEngine) {
    super(supplier, bindingParams);
    this.singletonEngine = singletonEngine;
  }

  @Override
  public List<SpInputCollector> getInputCollectors() throws SpRuntimeException {
    List<SpInputCollector> inputCollectors = new ArrayList<>();
    for (SpDataStream is : bindingParams.getGraph().getInputStreams()) {
      inputCollectors.add(ProtocolManager.findInputCollector(is.getEventGrounding()
                      .getTransportProtocol(), is.getEventGrounding().getTransportFormats().get(0),
              singletonEngine));
    }
    return inputCollectors;
  }

  public Boolean isSingletonEngine() {
    return singletonEngine;
  }
}
