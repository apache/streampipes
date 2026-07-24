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

package org.apache.streampipes.extensions.management.init;

import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.management.connect.adapter.model.EventCollector;
import org.apache.streampipes.extensions.management.monitoring.AdapterHealthCheckManager;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum RunningAdapterInstances {
  INSTANCE;

  private final Map<String, StreamPipesAdapter> runningAdapterInstances = new ConcurrentHashMap<>();
  private final Map<String, AdapterDescription> runningAdapterDescriptionInstances = new ConcurrentHashMap<>();
  private final Map<String, EventCollector> runningAdapterCollectors = new ConcurrentHashMap<>();

  public void addAdapter(String elementId,
                         StreamPipesAdapter adapter,
                         AdapterDescription adapterDescription,
                         EventCollector collector) {
    runningAdapterInstances.put(elementId, adapter);
    runningAdapterDescriptionInstances.put(elementId, adapterDescription);
    runningAdapterCollectors.put(elementId, collector);
    AdapterHealthCheckManager.INSTANCE.registerAdapter(elementId, adapter, adapterDescription);
  }

  public RunningAdapterInstance removeAdapter(String elementId) {
    var result = new RunningAdapterInstance(
        runningAdapterInstances.get(elementId),
        runningAdapterCollectors.get(elementId)
    );
    runningAdapterInstances.remove(elementId);
    runningAdapterDescriptionInstances.remove(elementId);
    AdapterHealthCheckManager.INSTANCE.unregisterAdapter(elementId);
    runningAdapterCollectors.remove(elementId);
    return result.adapter() != null ? result : null;
  }

  public Collection<AdapterDescription> getAllRunningAdapterDescriptions() {
    return this.runningAdapterDescriptionInstances.values();
  }

}
