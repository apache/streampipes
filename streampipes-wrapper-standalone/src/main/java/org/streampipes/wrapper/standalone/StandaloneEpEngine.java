package org.streampipes.wrapper.standalone;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.params.BindingParameters;
import org.streampipes.wrapper.EPEngine;
import org.streampipes.wrapper.params.EngineParameters;
import org.streampipes.wrapper.OutputCollector;

import java.util.Map;
import java.util.Optional;

/**
 * Created by riemer on 26.07.2017.
 */
public abstract class StandaloneEpEngine<B extends BindingParameters> implements EPEngine<B> {

  private Optional<OutputCollector> collectorOpt;

  @Override
  public void bind(EngineParameters<B> parameters, OutputCollector collector, SepaInvocation graph) {
    this.collectorOpt = Optional.of(collector);
    onInvocation(parameters, graph);
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

  public abstract void onInvocation(EngineParameters<B> params, SepaInvocation graph);

  public abstract void onEvent(Map<String, Object> event, String sourceInfo, OutputCollector
          collector);

  public abstract void onDetach();
}
