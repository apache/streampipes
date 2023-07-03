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

import org.apache.streampipes.model.grounding.JmsTransportProtocol;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.JMSException;

public abstract class ActiveMQConnectionProvider {

  protected JmsTransportProtocol protocol;

  public ActiveMQConnectionProvider(JmsTransportProtocol protocol) {
    this.protocol = protocol;
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
