package org.streampipes.wrapper.params.binding;

import org.streampipes.model.graph.DataSinkInvocation;

import java.io.Serializable;

public class EventSinkBindingParams extends BindingParams<DataSinkInvocation> implements Serializable {
  private static final long serialVersionUID = 1L;

  public EventSinkBindingParams(DataSinkInvocation graph) {
    super(new DataSinkInvocation(graph));
  }

  public EventSinkBindingParams() {
  }
}
