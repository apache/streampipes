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

package org.apache.streampipes.extensions.management.connect;

import org.apache.streampipes.model.health.AdapterInstanceState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks adapter instances that are currently being started or stopped in an extension service.
 *
 * <p>The health check compares the adapters that should be running, according to core storage, with
 * the adapters reported by each extension service. Starting and stopping are short transitional
 * phases where an adapter can temporarily be absent from the regular running-adapter registry, even
 * though the extension service is already handling the requested lifecycle operation.
 *
 * <p>This registry was introduced to make those transitional states visible to the health check.
 * Without it, a health check that runs at the same time as an adapter stop can interpret the
 * temporary absence as a crashed adapter and start it again. Reporting the adapter as
 * {@link AdapterInstanceState#STARTING} or {@link AdapterInstanceState#STOPPING} prevents this
 * recovery race while keeping the normal running-adapter registry focused on fully running
 * instances.
 */
public class AdapterTransitionRegistry {

  public static final AdapterTransitionRegistry INSTANCE = new AdapterTransitionRegistry();

  private final Map<String, AdapterInstanceState> transitioningAdapterInstanceStates = new ConcurrentHashMap<>();

  public void registerStarting(String adapterInstanceId) {
    register(adapterInstanceId, AdapterInstanceState.STARTING);
  }

  public void deregisterStarting(String adapterInstanceId) {
    deregister(adapterInstanceId, AdapterInstanceState.STARTING);
  }

  public void registerStopping(String adapterInstanceId) {
    register(adapterInstanceId, AdapterInstanceState.STOPPING);
  }

  public void deregisterStopping(String adapterInstanceId) {
    deregister(adapterInstanceId, AdapterInstanceState.STOPPING);
  }

  public Map<String, AdapterInstanceState> getTransitioningAdapterInstanceStates() {
    return Map.copyOf(transitioningAdapterInstanceStates);
  }

  private void register(String adapterInstanceId,
                        AdapterInstanceState adapterInstanceState) {
    if (adapterInstanceId != null) {
      transitioningAdapterInstanceStates.put(adapterInstanceId, adapterInstanceState);
    }
  }

  private void deregister(String adapterInstanceId,
                          AdapterInstanceState adapterInstanceState) {
    if (adapterInstanceId != null) {
      transitioningAdapterInstanceStates.remove(adapterInstanceId, adapterInstanceState);
    }
  }
}
