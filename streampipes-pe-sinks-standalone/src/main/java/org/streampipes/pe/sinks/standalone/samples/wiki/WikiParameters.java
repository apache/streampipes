package org.streampipes.pe.sinks.standalone.samples.wiki;

import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

public class WikiParameters extends EventSinkBindingParams {

  public WikiParameters(SecInvocation graph) {
    super(graph);
  }
}
