package org.streampipes.wrapper.standalone.param;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.streampipes.wrapper.params.runtime.EventProcessorRuntimeParams;
import org.streampipes.wrapper.runtime.EventProcessor;
import org.streampipes.wrapper.standalone.manager.PManager;
import org.streampipes.wrapper.standalone.routing.FlatSpInputCollector;
import org.streampipes.wrapper.standalone.routing.FlatSpOutputCollector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class FlatEventProcessorRuntimeParams<B extends EventProcessorBindingParams> extends EventProcessorRuntimeParams<B> {

  private List<FlatSpInputCollector> inputCollector;
  private FlatSpOutputCollector outputCollector;

  public FlatEventProcessorRuntimeParams(Supplier<EventProcessor<B>> supplier, B bindingParams) {
    super(supplier, bindingParams);
    this.inputCollector = new ArrayList<>();
    buildInputCollector();
    buildOutputCollector();
  }

  private void buildOutputCollector() {
    try {
      this.outputCollector = new FlatSpOutputCollector(PManager.getDataFormat(bindingParams.getGraph()
              .getOutputStream().getEventGrounding().getTransportFormats().get(0)), PManager
              .getProducer(bindingParams
              .getGraph()
              .getOutputStream().getEventGrounding().getTransportProtocol()));
    } catch (SpRuntimeException e) {
      e.printStackTrace();
    }
  }

  private void buildInputCollector() {
    bindingParams.getGraph().getInputStreams().forEach(is -> {
      try {
        inputCollector.add(new FlatSpInputCollector(PManager.getDataFormat(is.getEventGrounding()
                .getTransportFormats().get(0)),
                PManager
                .getConsumer(is.getEventGrounding().getTransportProtocol())));
      } catch (SpRuntimeException e) {
        e.printStackTrace();
      }
    });
  }

  public List<FlatSpInputCollector> getInputCollector() {
    return inputCollector;
  }

  public FlatSpOutputCollector getOutputCollector() {
    return outputCollector;
  }

}
