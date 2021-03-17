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
package org.apache.streampipes.node.controller.container.management.relay;

import org.apache.streampipes.node.controller.container.management.RunningInstances;

import java.util.*;
import java.util.stream.Collectors;

public enum RunningRelayInstances implements RunningInstances<Map<String,EventRelay>> {
    INSTANCE;

    private final Map<String, Map<String, EventRelay>> runningInstances = new HashMap<>();

    // TODO: persist active relays to support failure handling
    @Override
    public void add(String id, Map<String,EventRelay> eventRelayMap) {
        runningInstances.put(id, eventRelayMap);
    }

    @Override
    public boolean isRunning(String id) {
        return runningInstances.get(id) != null;
    }

    @Override
    public Map<String,EventRelay> get(String id) {
        return isRunning(id) ? runningInstances.get(id) : null;
    }

    @Override
    public List<Map<String, EventRelay>> getAll() {
        return new ArrayList<>(runningInstances.values());
    }

    @Override
    public void remove(String id) {
        runningInstances.remove(id);
    }

    public List<EventRelay> getRunningInstances() {
        return runningInstances.values().stream()
                .map(Map::values)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
