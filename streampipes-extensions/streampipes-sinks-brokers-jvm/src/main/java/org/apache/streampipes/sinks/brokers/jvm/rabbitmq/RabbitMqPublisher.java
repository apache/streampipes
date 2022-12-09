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

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class RabbitMqPublisher {

  private Map<String, Channel> queueMap;
  private boolean errorMode;

  private ConnectionFactory factory;
  private Connection connection;

  private RabbitMqParameters params;

  private String exchangeName;
  private static final Logger LOG = LoggerFactory.getLogger(RabbitMqPublisher.class);

  public RabbitMqPublisher(RabbitMqParameters params) {
    try {
      this.queueMap = new HashMap<>();
      this.params = params;
      this.exchangeName = params.getExchangeName();
      setupConnection();

      this.errorMode = false;
    } catch (IOException e) {
      LOG.error("Error (IOException) while connecting to RabbitMQ..entering error mode");
      this.errorMode = true;
    } catch (TimeoutException e) {
      LOG.error("Error (Timeout) while connecting to RabbitMQ..entering error mode");
      this.errorMode = true;
    }
  }

  private void setupConnection() throws IOException, TimeoutException {
    this.factory = new ConnectionFactory();
    this.factory.setPort(params.getRabbitMqPort());
    this.factory.setHost(params.getRabbitMqHost());
    this.factory.setUsername(params.getRabbitMqUser());
    this.factory.setPassword(params.getRabbitMqPassword());
    this.connection = factory.newConnection();

  }

  public boolean isConnected()  {
    return this.connection.isOpen();
  }

  public void fire(byte[] event, String topic) {
    if (!channelActive(topic)) {
      setupChannel(topic);
    }
    try {
      queueMap.get(topic).basicPublish(exchangeName, topic, null, event);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void setupChannel(String topic) {
    try {
      Channel channel = connection.createChannel();
      channel.exchangeDeclare(exchangeName, "topic", true, false, null);

      queueMap.put(topic, channel);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private boolean channelActive(String topic) {
    return queueMap.containsKey(topic);
  }

  public void cleanup() {
    queueMap
            .keySet()
            .stream()
            .map(key -> queueMap.get(key))
            .forEach(channel -> {
              try {
                channel.close();
              } catch (IOException e) {
                e.printStackTrace();
              } catch (TimeoutException e) {
                e.printStackTrace();
              }
            });
    try {
      connection.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
