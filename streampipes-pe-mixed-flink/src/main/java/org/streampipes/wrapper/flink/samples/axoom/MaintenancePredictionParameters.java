package org.streampipes.wrapper.flink.samples.axoom;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

/**
 * Created by riemer on 12.04.2017.
 */
public class MaintenancePredictionParameters extends EventProcessorBindingParams {

  public MaintenancePredictionParameters(SepaInvocation graph) {
    super(graph);
  }
}
