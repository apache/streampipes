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
import org.apache.streampipes.client.api.live.IBrokerConfigOverride;
import org.apache.streampipes.client.api.live.ISubscription;
import org.apache.streampipes.model.graph.DataSinkInvocation;

import java.util.List;
import java.util.Optional;

public interface IDataSinkApi extends CRUDApi<String, DataSinkInvocation> {
  @Override
  Optional<DataSinkInvocation> get(String s);

  @Override
  List<DataSinkInvocation> all();

  @Override
  @NotYetImplemented
  void create(DataSinkInvocation element);

  @Override
  @NotYetImplemented
  void delete(String s);

  @Override
  void update(DataSinkInvocation element);

  ISubscription subscribe(DataSinkInvocation sink,
                          EventProcessor callback);

  ISubscription subscribe(DataSinkInvocation sink,
                          IBrokerConfigOverride brokerConfigOverride,
                          EventProcessor callback);

  DataSinkInvocation getDataSinkForPipelineElement(String templateId, DataSinkInvocation pipelineElement);
}
