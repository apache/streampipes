package org.streampipes.pe.mixed.flink.samples.axoom;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.BindingParameters;

/**
 * Created by riemer on 12.04.2017.
 */
public class MaintenancePredictionParameters extends BindingParameters{

  public MaintenancePredictionParameters(SepaInvocation graph) {
    super(graph);
  }
}
