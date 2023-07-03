/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.sinks.brokers.jvm.rabbitmq;

public class RabbitMqParameters {

  private String rabbitMqHost;
  private Integer rabbitMqPort;
  private String rabbitMqTopic;
  private String rabbitMqUser;
  private String rabbitMqPassword;
  private String exchangeName;

  public RabbitMqParameters(String rabbitMqHost, Integer rabbitMqPort, String rabbitMqTopic,
                            String rabbitMqUser, String rabbitMqPassword, String exchangeName) {
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
