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
package org.apache.streampipes.sinks.brokers.jvm.rocketmq;

import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.apache.streampipes.wrapper.standalone.SinkParams;

import static org.apache.streampipes.sinks.brokers.jvm.rocketmq.RocketMQPublisherSink.ENDPOINT_KEY;
import static org.apache.streampipes.sinks.brokers.jvm.rocketmq.RocketMQPublisherSink.TOPIC_KEY;

public class RocketMQParameters {
  private String endpoint;
  private String topic;

  public RocketMQParameters(SinkParams parameters) {
    DataSinkParameterExtractor extractor = parameters.extractor();

    this.endpoint = extractor.singleValueParameter(ENDPOINT_KEY, String.class);
    this.topic = extractor.singleValueParameter(TOPIC_KEY, String.class);
  }

  public String getEndpoint() {
    return endpoint;
  }

  public String getTopic() {
    return topic;
  }
}
