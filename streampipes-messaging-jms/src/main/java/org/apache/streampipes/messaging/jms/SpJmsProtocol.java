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

import org.apache.streampipes.messaging.EventConsumer;
import org.apache.streampipes.messaging.EventProducer;
import org.apache.streampipes.messaging.SpProtocolDefinition;
import org.apache.streampipes.model.grounding.JmsTransportProtocol;

public class SpJmsProtocol implements SpProtocolDefinition<JmsTransportProtocol> {

  private final EventConsumer<JmsTransportProtocol> jmsConsumer;
  private final EventProducer<JmsTransportProtocol> jmsProducer;

  public SpJmsProtocol() {
    this.jmsConsumer = new ActiveMQConsumer();
    this.jmsProducer = new ActiveMQPublisher();
  }

  @Override
  public EventConsumer<JmsTransportProtocol> getConsumer() {
    return jmsConsumer;
  }

  @Override
  public EventProducer<JmsTransportProtocol> getProducer() {
    return jmsProducer;
  }
}
