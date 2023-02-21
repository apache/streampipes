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
package org.apache.streampipes.client.api;

import org.apache.streampipes.client.annotation.NotYetImplemented;
import org.apache.streampipes.client.live.EventProcessor;
import org.apache.streampipes.client.live.KafkaConfig;
import org.apache.streampipes.client.live.SubscriptionManager;
import org.apache.streampipes.client.model.InputStreamIndex;
import org.apache.streampipes.client.model.StreamPipesClientConfig;
import org.apache.streampipes.client.util.StreamPipesApiPath;
import org.apache.streampipes.messaging.kafka.SpKafkaConsumer;
import org.apache.streampipes.model.graph.DataProcessorInvocation;

import java.util.List;

public class DataProcessorApi extends AbstractTypedClientApi<DataProcessorInvocation>
    implements CRUDApi<String, DataProcessorInvocation> {

  public DataProcessorApi(StreamPipesClientConfig clientConfig) {
    super(clientConfig, DataProcessorInvocation.class);
  }

  @Override
  protected StreamPipesApiPath getBaseResourcePath() {
    return StreamPipesApiPath.fromBaseApiPath()
        .addToPath("sepas");
  }

  @Override
  public DataProcessorInvocation get(String s) {
    return getSingle(getBaseResourcePath().addToPath(s));
  }

  @Override
  public List<DataProcessorInvocation> all() {
    return getAll(getBaseResourcePath());
  }

  @Override
  @NotYetImplemented
  public void create(DataProcessorInvocation element) {

  }

  @Override
  @NotYetImplemented
  public void delete(String s) {

  }

  @Override
  @NotYetImplemented
  public void update(DataProcessorInvocation element) {

  }

  /**
   * Subscribe to the output stream of the processor
   *
   * @param processor The data processor to subscribe to
   * @param callback  The callback where events will be received
   */
  public SpKafkaConsumer subscribe(DataProcessorInvocation processor,
                                   EventProcessor callback) {
    return new SubscriptionManager(clientConfig, processor.getOutputStream().getEventGrounding(), callback).subscribe();
  }

  /**
   * Subscribe to the output stream of the processor
   *
   * @param processor   The data processor to subscribe to
   * @param kafkaConfig Additional kafka settings which will override the default value (see docs)
   * @param callback    The callback where events will be received
   */
  public SpKafkaConsumer subscribe(DataProcessorInvocation processor,
                                   KafkaConfig kafkaConfig,
                                   EventProcessor callback) {
    return new SubscriptionManager(clientConfig, kafkaConfig, processor.getOutputStream().getEventGrounding(), callback)
        .subscribe();
  }

  /**
   * Subscribe to the input stream of the processor
   *
   * @param processor The data processor to subscribe to
   * @param index     The index of the input stream
   * @param callback  The callback where events will be received
   */
  public SpKafkaConsumer subscribe(DataProcessorInvocation processor,
                                   InputStreamIndex index,
                                   EventProcessor callback) {
    return new SubscriptionManager(clientConfig,
        processor.getInputStreams().get(index.toIndex()).getEventGrounding(), callback).subscribe();
  }

  /**
   * Subscribe to the input stream of the sink
   *
   * @param processor   The data processor to subscribe to
   * @param index       The index of the input stream
   * @param kafkaConfig Additional kafka settings which will override the default value (see docs)
   * @param callback    The callback where events will be received
   */
  public SpKafkaConsumer subscribe(DataProcessorInvocation processor,
                                   InputStreamIndex index,
                                   KafkaConfig kafkaConfig,
                                   EventProcessor callback) {
    return new SubscriptionManager(clientConfig, kafkaConfig,
        processor.getInputStreams().get(index.toIndex()).getEventGrounding(), callback)
        .subscribe();
  }
}
