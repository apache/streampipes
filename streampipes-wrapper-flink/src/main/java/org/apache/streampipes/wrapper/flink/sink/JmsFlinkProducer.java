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

package org.apache.streampipes.wrapper.flink.sink;

import org.apache.streampipes.messaging.jms.ActiveMQPublisher;
import org.apache.streampipes.model.grounding.JmsTransportProtocol;
import org.apache.streampipes.wrapper.flink.serializer.ByteArraySerializer;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.util.Map;


public class JmsFlinkProducer extends RichSinkFunction<Map<String, Object>> {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private JmsTransportProtocol protocol;

  private ByteArraySerializer serializationSchema;

  private ActiveMQPublisher publisher;

  public JmsFlinkProducer(JmsTransportProtocol protocol, ByteArraySerializer
      serializationSchema) {
    this.protocol = protocol;
    this.serializationSchema = serializationSchema;
  }

  @Override
  public void open(Configuration configuration) throws Exception {
    try {
      publisher = new ActiveMQPublisher(protocol);
      publisher.connect();
    } catch (Exception e) {
      throw new Exception("Failed to open Jms connection: " + e.getMessage(), e);
    }
  }

  @Override
  public void invoke(Map<String, Object> value) throws Exception {
    byte[] msg = serializationSchema.serialize(value);
    publisher.publish(msg);
  }
}


