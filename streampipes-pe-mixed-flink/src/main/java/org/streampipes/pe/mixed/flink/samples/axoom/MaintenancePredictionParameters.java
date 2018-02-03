package org.streampipes.pe.mixed.flink.samples.axoom;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class MaintenancePredictionParameters extends EventProcessorBindingParams {

  public MaintenancePredictionParameters(DataProcessorInvocation graph) {
    super(graph);
  }
}
