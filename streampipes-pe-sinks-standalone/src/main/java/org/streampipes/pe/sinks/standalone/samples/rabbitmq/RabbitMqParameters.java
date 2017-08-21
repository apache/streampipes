package org.streampipes.pe.sinks.standalone.samples.rabbitmq;

import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

public class RabbitMqParameters extends EventSinkBindingParams {

  public RabbitMqParameters(SecInvocation graph) {
    super(graph);
  }
}
