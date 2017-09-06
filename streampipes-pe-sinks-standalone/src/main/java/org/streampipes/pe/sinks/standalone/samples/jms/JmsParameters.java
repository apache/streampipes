package org.streampipes.pe.sinks.standalone.samples.jms;

import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

public class JmsParameters extends EventSinkBindingParams {

  private String jmsHost;
  private Integer jmsPort;
  private String topic;

  public JmsParameters(SecInvocation graph, String jmsHost, Integer jmsPort, String topic) {
    super(graph);
    this.jmsHost = jmsHost;
    this.jmsPort = jmsPort;
    this.topic = topic;
  }

  public String getJmsHost() {
    return jmsHost;
  }

  public Integer getJmsPort() {
    return jmsPort;
  }

  public String getTopic() {
    return topic;
  }
}
