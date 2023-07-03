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
import org.apache.streampipes.model.graph.DataProcessorInvocation;

import java.util.List;
import java.util.Optional;

public interface IDataProcessorApi extends CRUDApi<String, DataProcessorInvocation> {

  @Override
  Optional<DataProcessorInvocation> get(String s);

  @Override
  List<DataProcessorInvocation> all();

  @Override
  @NotYetImplemented
  void create(DataProcessorInvocation element);

  @Override
  @NotYetImplemented
  void delete(String s);

  @Override
  @NotYetImplemented
  void update(DataProcessorInvocation element);

  ISubscription subscribe(DataProcessorInvocation processor,
                          EventProcessor callback);

  ISubscription subscribe(DataProcessorInvocation processor,
                          IBrokerConfigOverride brokerConfigOverride,
                          EventProcessor callback);

  ISubscription subscribe(DataProcessorInvocation processor,
                          InputStreamIndex index,
                          EventProcessor callback);

  ISubscription subscribe(DataProcessorInvocation processor,
                          InputStreamIndex index,
                          IBrokerConfigOverride brokerConfigOverride,
                          EventProcessor callback);
}
