package org.streampipes.wrapper.params.binding;

import org.streampipes.model.graph.DataSinkInvocation;

public class EventSinkBindingParams extends BindingParams<DataSinkInvocation> {

  public EventSinkBindingParams(DataSinkInvocation graph) {
    super(new DataSinkInvocation(graph));
  }
}
