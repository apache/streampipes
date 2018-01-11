package org.streampipes.pe.mixed.flink.samples.breakdown;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class Prediction2BreakdownParameters extends EventProcessorBindingParams {

  public Prediction2BreakdownParameters(DataProcessorInvocation graph) {
    super(graph);
  }
}
