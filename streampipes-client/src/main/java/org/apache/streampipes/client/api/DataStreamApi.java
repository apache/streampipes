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

import org.apache.streampipes.client.api.live.EventProcessor;
import org.apache.streampipes.client.api.live.IBrokerConfigOverride;
import org.apache.streampipes.client.api.live.IConfiguredEventProducer;
import org.apache.streampipes.client.api.live.ISubscription;
import org.apache.streampipes.client.live.ProducerManager;
import org.apache.streampipes.client.live.SubscriptionManager;
import org.apache.streampipes.client.model.StreamPipesClientConfig;
import org.apache.streampipes.client.util.StreamPipesApiPath;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.message.Message;

import java.net.URLEncoder;
import java.util.List;
import java.util.Optional;

public class DataStreamApi extends AbstractTypedClientApi<SpDataStream> implements IDataStreamApi {

  public DataStreamApi(StreamPipesClientConfig clientConfig) {
    super(clientConfig, SpDataStream.class);
  }

  @Override
  public Optional<SpDataStream> get(String streamId) {
    return getSingle(StreamPipesApiPath.fromBaseApiPath()
        .addToPath("streams").addToPath(streamId));
  }

  /**
   * Get all available data streams
   *
   * @return {@link org.apache.streampipes.model.SpDataStream} A list of all data streams owned by the user.
   */
  @Override
  public List<SpDataStream> all() {
    return getAll(getBaseResourcePath());
  }

  /**
   * Directly install a new data stream
   *
   * @param stream The data stream to add
   */
  @Override
  public void create(SpDataStream stream) {
    post(StreamPipesApiPath.fromBaseApiPath().addToPath("streams"), stream);
  }

  /**
   * Delete a data stream
   *
   * @param streamId The elementId of the stream
   */
  @Override
  public void delete(String streamId) {
    delete(getBaseResourcePath().addToPath(URLEncoder.encode(streamId)), Message.class);
  }

  @Override
  public void update(SpDataStream element) {

  }

  @Override
  public IConfiguredEventProducer getProducer(SpDataStream stream) {
    return new ProducerManager(stream.getEventGrounding()).makeProducer();
  }

  /**
   * Subscribe to a data stream
   *
   * @param stream   The data stream to subscribe to
   * @param callback The callback where events will be received
   */
  @Override
  public ISubscription subscribe(SpDataStream stream,
                                 EventProcessor callback) {
    return new SubscriptionManager(stream.getEventGrounding(), callback).subscribe();
  }

  /**
   * Subscribe to a data stream
   *
   * @param stream      The data stream to subscribe to
   * @param kafkaConfig Additional kafka settings which will override the default value (see docs)
   * @param callback    The callback where events will be received
   */
  @Override
  public ISubscription subscribe(SpDataStream stream,
                                 IBrokerConfigOverride kafkaConfig,
                                 EventProcessor callback) {
    return new SubscriptionManager(kafkaConfig, stream.getEventGrounding(), callback).subscribe();
  }

  @Override
  protected StreamPipesApiPath getBaseResourcePath() {
    return StreamPipesApiPath.fromBaseApiPath()
        .addToPath("streams");
  }
}
