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
import org.apache.streampipes.messaging.EventConsumer;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.model.grounding.JmsTransportProtocol;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.util.ByteSequence;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import java.io.Serializable;

public class ActiveMQConsumer extends ActiveMQConnectionProvider implements
    EventConsumer,
    AutoCloseable, Serializable {

  private Session session;
  private MessageConsumer consumer;
  private InternalEventProcessor<byte[]> eventProcessor;

  private Boolean connected = false;

  public ActiveMQConsumer(JmsTransportProtocol protocol) {
    super(protocol);
  }

  private void initListener() {
    try {
      consumer.setMessageListener(message -> {
        if (message instanceof BytesMessage) {
          ByteSequence bs = ((ActiveMQBytesMessage) message).getContent();
          eventProcessor.onEvent(bs.getData());
        }

      });
    } catch (JMSException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void connect(InternalEventProcessor<byte[]>
      eventProcessor) throws SpRuntimeException {
    String url = ActiveMQUtils.makeActiveMqUrl(protocol);

    try {
      this.eventProcessor = eventProcessor;
      session = startJmsConnection(url).createSession(false, Session.AUTO_ACKNOWLEDGE);
      consumer = session.createConsumer(session.createTopic(
          protocol.getTopicDefinition().getActualTopicName())
      );
      initListener();
      this.connected = true;
    } catch (JMSException e) {
      throw new SpRuntimeException("could not connect to activemq broker");
    }
  }

  @Override
  public void disconnect() throws SpRuntimeException {
    try {
      consumer.close();
      session.close();
      this.connected = false;
    } catch (JMSException e) {
      throw new SpRuntimeException("could not disconnect from activemq broker");
    }

  }

  @Override
  public boolean isConnected() {
    return connected;
  }

  @Override
  public void close() throws Exception {
    disconnect();
  }
}
