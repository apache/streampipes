package org.streampipes.wrapper.standalone.engine;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

import java.util.Map;
import java.util.Optional;

/**
 * Created by riemer on 26.07.2017.
 */
public abstract class StandaloneEventProcessorEngine<B extends EventProcessorBindingParams> implements EventProcessor<B> {

  private Optional<SpOutputCollector> collectorOpt;

  @Override
  public void bind(B parameters, SpOutputCollector collector) {
    collectorOpt = Optional.of(collector);
    onInvocation(parameters, parameters.getGraph());
  }

  @Override
  public void onEvent(Map<String, Object> event, String sourceInfo) {
      if (collectorOpt.isPresent()) {
        onEvent(event, sourceInfo, collectorOpt.get());
      } else {
        throw new IllegalArgumentException("");
      }
  }

  @Override
  public void discard() {
    this.collectorOpt = Optional.empty();
    onDetach();
  }

  public abstract void onInvocation(B params, DataProcessorInvocation graph);

  public abstract void onEvent(Map<String, Object> event, String sourceInfo, SpOutputCollector
          collector);

  public abstract void onDetach();
}
