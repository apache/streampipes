package org.streampipes.pe.sinks.standalone.samples.kafka;

import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

public class KafkaParameters extends EventSinkBindingParams {

  private String kafkaHost;
  private Integer kafkaPort;
  private String topic;

  public KafkaParameters(DataSinkInvocation graph, String kafkaHost, Integer kafkaPort, String topic) {
    super(graph);
    this.kafkaHost = kafkaHost;
    this.kafkaPort = kafkaPort;
    this.topic = topic;
  }

  public String getKafkaHost() {
    return kafkaHost;
  }

  public Integer getKafkaPort() {
    return kafkaPort;
  }

  public String getTopic() {
    return topic;
  }
}
