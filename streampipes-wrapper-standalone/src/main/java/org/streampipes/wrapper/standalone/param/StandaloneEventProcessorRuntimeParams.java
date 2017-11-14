package org.streampipes.wrapper.standalone.param;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.model.SpDataStream;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.streampipes.wrapper.params.runtime.EventProcessorRuntimeParams;
import org.streampipes.wrapper.routing.SpInputCollector;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;
import org.streampipes.wrapper.standalone.manager.ProtocolManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class StandaloneEventProcessorRuntimeParams<B extends EventProcessorBindingParams> extends EventProcessorRuntimeParams<B> {

  private Boolean singletonEngine;

  public StandaloneEventProcessorRuntimeParams(Supplier<EventProcessor<B>> supplier, B bindingParams,
                                               Boolean singletonEngine) {
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

  @Override
  public SpOutputCollector getOutputCollector() throws SpRuntimeException {
    return ProtocolManager.findOutputCollector(bindingParams.getGraph().getOutputStream()
            .getEventGrounding().getTransportProtocol(), bindingParams.getGraph().getOutputStream
            ().getEventGrounding().getTransportFormats().get(0));
  }

}
