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

import org.apache.streampipes.client.api.annotation.NotYetImplemented;
import org.apache.streampipes.client.api.live.EventProcessor;
import org.apache.streampipes.client.api.live.IKafkaConfig;
import org.apache.streampipes.client.live.SubscriptionManager;
import org.apache.streampipes.client.model.StreamPipesClientConfig;
import org.apache.streampipes.client.util.StreamPipesApiPath;
import org.apache.streampipes.messaging.EventConsumer;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;

import java.util.List;
import java.util.Optional;

public class DataSinkApi extends AbstractTypedClientApi<DataSinkInvocation>
    implements IDataSinkApi {

  public DataSinkApi(StreamPipesClientConfig clientConfig) {
    super(clientConfig, DataSinkInvocation.class);
  }

  @Override
  public Optional<DataSinkInvocation> get(String s) {
    return getSingle(getBaseResourcePath().addToPath(s));
  }

  @Override
  public List<DataSinkInvocation> all() {
    return getAll(getBaseResourcePath());
  }

  @Override
  @NotYetImplemented
  public void create(DataSinkInvocation element) {

  }

  @Override
  @NotYetImplemented
  public void delete(String s) {

  }

  @Override
  public void update(DataSinkInvocation element) {

  }

  /**
   * Subscribe to the input stream of the sink
   *
   * @param sink     The data sink to subscribe to
   * @param callback The callback where events will be received
   */
  @Override
  public EventConsumer<KafkaTransportProtocol> subscribe(DataSinkInvocation sink,
                                                         EventProcessor callback) {
    return new SubscriptionManager(clientConfig,
        sink.getInputStreams().get(0).getEventGrounding(), callback).subscribe();
  }

  /**
   * Subscribe to the input stream of the sink
   *
   * @param sink        The data sink to subscribe to
   * @param kafkaConfig Additional kafka settings which will override the default value (see docs)
   * @param callback    The callback where events will be received
   */
  @Override
  public EventConsumer<KafkaTransportProtocol> subscribe(DataSinkInvocation sink,
                                   IKafkaConfig kafkaConfig,
                                   EventProcessor callback) {
    return new SubscriptionManager(clientConfig, kafkaConfig,
        sink.getInputStreams().get(0).getEventGrounding(), callback).subscribe();
  }

  @Override
  public DataSinkInvocation getDataSinkForPipelineElement(String templateId, DataSinkInvocation pipelineElement) {
    StreamPipesApiPath path = StreamPipesApiPath.fromBaseApiPath()
        .addToPath("pipeline-element-templates")
        .addToPath(templateId)
        .addToPath("sink");

    return post(path, pipelineElement, DataSinkInvocation.class);
  }

  @Override
  protected StreamPipesApiPath getBaseResourcePath() {
    return StreamPipesApiPath.fromBaseApiPath()
        .addToPath("actions");
  }
}
