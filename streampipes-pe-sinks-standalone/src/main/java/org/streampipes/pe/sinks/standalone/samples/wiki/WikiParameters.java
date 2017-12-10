package org.streampipes.pe.sinks.standalone.samples.wiki;

import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

public class WikiParameters extends EventSinkBindingParams {

  public WikiParameters(DataSinkInvocation graph) {
    super(graph);
  }
}
