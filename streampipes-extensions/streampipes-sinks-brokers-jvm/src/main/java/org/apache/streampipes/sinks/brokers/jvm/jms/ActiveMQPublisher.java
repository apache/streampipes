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

package org.apache.streampipes.sinks.brokers.jvm.jms;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.messaging.EventProducer;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.jms.BytesMessage;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.DeliveryMode;
import jakarta.jms.JMSException;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;


public class ActiveMQPublisher implements EventProducer {

  private static final Logger LOG = LoggerFactory.getLogger(ActiveMQPublisher.class);

  private Connection connection;
  private Session session;
  private MessageProducer producer;

  private final String host;
  private final int port;
  private final String topic;

  private boolean connected = false;

  public ActiveMQPublisher(String host,
                           int port,
                           String topic) {
    this.host = host;
    this.port = port;
    this.topic = topic;
  }

  @Override
  public void connect() throws SpRuntimeException {

    String url = ActiveMQUtils.makeActiveMqUrl(host, port);
    ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);

    boolean co = false;
    do {
      try {
        this.connection = connectionFactory.createConnection();
        co = true;
      } catch (JMSException e) {
        LOG.error("Trying to connect...", e);
      }
    } while (!co);

    try {
      this.session = connection
          .createSession(false, Session.AUTO_ACKNOWLEDGE);
      this.producer = session.createProducer(session.createTopic(topic));
      this.producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
      this.connection.start();
      this.connected = true;
    } catch (JMSException e) {
      throw new SpRuntimeException("could not connect to activemq broker. Broker: '"
          + host + "' Port: " + port);
    }
  }

  @Override
  public void publish(byte[] event) {
    BytesMessage message;
    try {
      message = session.createBytesMessage();
      message.writeBytes(event);
      producer.send(message);
    } catch (JMSException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void disconnect() throws SpRuntimeException {
    try {
      producer.close();
      session.close();
      connection.close();
      this.connected = false;
    } catch (JMSException e) {
      throw new SpRuntimeException("could not disconnect from activemq broker");
    }
  }

  @Override
  public boolean isConnected() {
    return connected;
  }

  protected Connection startJmsConnection(String url) {
    try {
      ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
      connectionFactory.setAlwaysSyncSend(false);
      Connection connect = connectionFactory.createConnection();

      connect.start();
      return connect;
    } catch (JMSException e) {
      throw new AssertionError("Failed to establish the JMS-Connection!", e);
    }
  }

}
