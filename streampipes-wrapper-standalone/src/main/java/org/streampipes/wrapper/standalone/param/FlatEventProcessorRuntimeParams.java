package org.streampipes.wrapper.standalone.param;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.model.impl.EventStream;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.streampipes.wrapper.params.runtime.EventProcessorRuntimeParams;
import org.streampipes.wrapper.runtime.EventProcessor;
import org.streampipes.wrapper.runtime.SpCollector;
import org.streampipes.wrapper.standalone.manager.PManager;
import org.streampipes.wrapper.standalone.routing.FlatSpInputCollector;
import org.streampipes.wrapper.standalone.routing.FlatSpOutputCollector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class FlatEventProcessorRuntimeParams<B extends EventProcessorBindingParams> extends EventProcessorRuntimeParams<B> {


  public FlatEventProcessorRuntimeParams(Supplier<EventProcessor<B>> supplier, B bindingParams) {
    super(supplier, bindingParams);
  }

  @Override
  protected List<SpCollector<EventProcessor<B>>> getInputCollectors() throws SpRuntimeException {
    List<SpCollector> inputCollectors = new ArrayList<>();
    for (EventStream is : bindingParams.getGraph().getInputStreams()) {
      inputCollectors.add(new FlatSpInputCollector(PManager.getDataFormat(is.getEventGrounding()
              .getTransportFormats().get(0)),
              PManager
                      .getConsumer(is.getEventGrounding().getTransportProtocol())));
    }
    return inputCollectors;
  }

  @Override
  protected SpCollector getOutputCollector() throws SpRuntimeException {
    return new FlatSpOutputCollector(PManager.getDataFormat(bindingParams.getGraph()
            .getOutputStream().getEventGrounding().getTransportFormats().get(0)), PManager
            .getProducer(bindingParams
                    .getGraph()
                    .getOutputStream().getEventGrounding().getTransportProtocol()));
  }

}
