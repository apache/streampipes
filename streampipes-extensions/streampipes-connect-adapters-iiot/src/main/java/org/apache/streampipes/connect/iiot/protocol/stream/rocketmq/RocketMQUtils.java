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
package org.apache.streampipes.connect.iiot.protocol.stream.rocketmq;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.MessageListener;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;

import java.util.Collections;

public class RocketMQUtils {

  public static PushConsumer createConsumer(String endpoint, String topic, String consumerGroup,
                                            MessageListener listener) throws ClientException {
    ClientServiceProvider provider = ClientServiceProvider.loadService();
    ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
        .setEndpoints(endpoint)
        .build();

    FilterExpression filterExpression = new FilterExpression("*", FilterExpressionType.TAG);
    PushConsumer consumer = provider.newPushConsumerBuilder()
        .setClientConfiguration(clientConfiguration)
        .setConsumerGroup(consumerGroup)
        .setSubscriptionExpressions(Collections.singletonMap(topic, filterExpression))
        .setMessageListener(listener)
        .build();
    return consumer;
  }
}
