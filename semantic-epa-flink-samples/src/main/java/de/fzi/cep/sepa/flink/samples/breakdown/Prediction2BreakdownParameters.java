package de.fzi.cep.sepa.flink.samples.breakdown;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

/**
 * Created by riemer on 12.02.2017.
 */
public class Prediction2BreakdownParameters extends BindingParameters {

  public Prediction2BreakdownParameters(SepaInvocation graph) {
    super(graph);
  }
}
