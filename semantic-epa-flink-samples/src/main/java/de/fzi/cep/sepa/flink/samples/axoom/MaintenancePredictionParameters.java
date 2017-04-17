package de.fzi.cep.sepa.flink.samples.axoom;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

/**
 * Created by riemer on 12.04.2017.
 */
public class MaintenancePredictionParameters extends BindingParameters{

  public MaintenancePredictionParameters(SepaInvocation graph) {
    super(graph);
  }
}
