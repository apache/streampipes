package org.streampipes.wrapper.flink.samples.breakdown;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.BindingParameters;

/**
 * Created by riemer on 12.02.2017.
 */
public class Prediction2BreakdownParameters extends BindingParameters {

  public Prediction2BreakdownParameters(SepaInvocation graph) {
    super(graph);
  }
}
