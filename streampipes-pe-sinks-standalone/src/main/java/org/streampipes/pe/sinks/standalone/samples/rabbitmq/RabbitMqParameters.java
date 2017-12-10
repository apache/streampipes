package org.streampipes.pe.sinks.standalone.samples.rabbitmq;

import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

public class RabbitMqParameters extends EventSinkBindingParams {

  private String rabbitMqHost;
  private Integer rabbitMqPort;
  private String rabbitMqTopic;
  private String rabbitMqUser;
  private String rabbitMqPassword;
  private String exchangeName;

  public RabbitMqParameters(DataSinkInvocation graph, String rabbitMqHost, Integer rabbitMqPort, String rabbitMqTopic,
                            String rabbitMqUser, String rabbitMqPassword, String exchangeName) {
    super(graph);
    this.rabbitMqHost = rabbitMqHost;
    this.rabbitMqPort = rabbitMqPort;
    this.rabbitMqTopic = rabbitMqTopic;
    this.rabbitMqUser = rabbitMqUser;
    this.rabbitMqPassword = rabbitMqPassword;
    this.exchangeName = exchangeName;

  }

  public String getRabbitMqHost() {
    return rabbitMqHost;
  }

  public Integer getRabbitMqPort() {
    return rabbitMqPort;
  }

  public String getRabbitMqTopic() {
    return rabbitMqTopic;
  }

  public String getRabbitMqUser() {
    return rabbitMqUser;
  }

  public String getRabbitMqPassword() {
    return rabbitMqPassword;
  }

  public String getExchangeName() {
    return exchangeName;
  }
}
