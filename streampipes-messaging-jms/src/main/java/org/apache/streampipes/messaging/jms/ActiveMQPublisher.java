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

package org.apache.streampipes.messaging.jms;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.messaging.EventProducer;
import org.apache.streampipes.model.grounding.JmsTransportProtocol;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;


public class ActiveMQPublisher extends ActiveMQConnectionProvider implements EventProducer {

  private static final Logger LOG = LoggerFactory.getLogger(ActiveMQPublisher.class);

  private Connection connection;
  private Session session;
  private MessageProducer producer;

  private boolean connected = false;

  public ActiveMQPublisher(JmsTransportProtocol protocol) {
    super(protocol);
  }

  @Override
  public void connect() throws SpRuntimeException {

    String url = ActiveMQUtils.makeActiveMqUrl(protocol);
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
      this.producer = session.createProducer(session.createTopic(protocol
          .getTopicDefinition()
          .getActualTopicName()));
      this.producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
      this.connection.start();
      this.connected = true;
    } catch (JMSException e) {
      throw new SpRuntimeException("could not connect to activemq broker. Broker: '"
          + protocol.getBrokerHostname() + "' Port: " + protocol.getPort());
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

}
