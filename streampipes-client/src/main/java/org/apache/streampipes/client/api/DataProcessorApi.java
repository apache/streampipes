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
import org.apache.streampipes.client.api.constants.InputStreamIndex;
import org.apache.streampipes.client.api.live.EventProcessor;
import org.apache.streampipes.client.api.live.IBrokerConfigOverride;
import org.apache.streampipes.client.api.live.ISubscription;
import org.apache.streampipes.client.live.SubscriptionManager;
import org.apache.streampipes.client.model.StreamPipesClientConfig;
import org.apache.streampipes.client.util.StreamPipesApiPath;
import org.apache.streampipes.model.graph.DataProcessorInvocation;

import java.util.List;
import java.util.Optional;

public class DataProcessorApi extends AbstractTypedClientApi<DataProcessorInvocation>
    implements IDataProcessorApi {

  public DataProcessorApi(StreamPipesClientConfig clientConfig) {
    super(clientConfig, DataProcessorInvocation.class);
  }

  public StreamPipesApiPath getBaseResourcePath() {
    return StreamPipesApiPath.fromBaseApiPath()
        .addToPath("sepas");
  }

  @Override
  public Optional<DataProcessorInvocation> get(String s) {
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
  @Override
  public ISubscription subscribe(DataProcessorInvocation processor,
                                 EventProcessor callback) {
    return new SubscriptionManager(processor.getOutputStream().getEventGrounding(), callback).subscribe();
  }

  /**
   * Subscribe to the output stream of the processor
   *
   * @param processor            The data processor to subscribe to
   * @param brokerConfigOverride Additional broker settings which will override the default value (see docs)
   * @param callback             The callback where events will be received
   */
  @Override
  public ISubscription subscribe(DataProcessorInvocation processor,
                                 IBrokerConfigOverride brokerConfigOverride,
                                 EventProcessor callback) {
    return new SubscriptionManager(brokerConfigOverride, processor.getOutputStream().getEventGrounding(), callback)
        .subscribe();
  }

  /**
   * Subscribe to the input stream of the processor
   *
   * @param processor The data processor to subscribe to
   * @param index     The index of the input stream
   * @param callback  The callback where events will be received
   */
  @Override
  public ISubscription subscribe(DataProcessorInvocation processor,
                                 InputStreamIndex index,
                                 EventProcessor callback) {
    return new SubscriptionManager(
        processor.getInputStreams().get(index.toIndex()).getEventGrounding(), callback)
        .subscribe();
  }

  /**
   * Subscribe to the input stream of the sink
   *
   * @param processor            The data processor to subscribe to
   * @param index                The index of the input stream
   * @param brokerConfigOverride Additional kafka settings which will override the default value (see docs)
   * @param callback             The callback where events will be received
   */
  @Override
  public ISubscription subscribe(DataProcessorInvocation processor,
                                 InputStreamIndex index,
                                 IBrokerConfigOverride brokerConfigOverride,
                                 EventProcessor callback) {
    return new SubscriptionManager(
        brokerConfigOverride,
        processor.getInputStreams().get(index.toIndex()).getEventGrounding(), callback)
        .subscribe();
  }
}
