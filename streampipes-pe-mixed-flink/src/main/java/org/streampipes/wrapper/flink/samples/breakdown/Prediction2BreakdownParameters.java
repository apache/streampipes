package org.streampipes.wrapper.flink.samples.breakdown;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

/**
 * Created by riemer on 12.02.2017.
 */
public class Prediction2BreakdownParameters extends EventProcessorBindingParams {

  public Prediction2BreakdownParameters(SepaInvocation graph) {
    super(graph);
  }
}
