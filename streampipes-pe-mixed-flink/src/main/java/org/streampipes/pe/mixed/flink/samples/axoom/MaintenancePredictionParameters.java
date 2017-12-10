package org.streampipes.pe.mixed.flink.samples.axoom;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

/**
 * Created by riemer on 12.04.2017.
 */
public class MaintenancePredictionParameters extends EventProcessorBindingParams {

  public MaintenancePredictionParameters(DataProcessorInvocation graph) {
    super(graph);
  }
}
