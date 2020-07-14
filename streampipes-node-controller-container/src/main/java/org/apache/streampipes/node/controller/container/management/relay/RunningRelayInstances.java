package org.apache.streampipes.node.controller.container.management.relay;/*
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

import java.util.HashMap;
import java.util.Map;

public enum RunningRelayInstances {
    INSTANCE;

    private final Map<String, EventRelayManager> runningInstances = new HashMap<>();

    // TODO: persist active relays to support failure handling
    public void addRelay(String id, EventRelayManager eventRelayManager) {
        runningInstances.put(id, eventRelayManager);
    }

    public boolean isRunning(String id) {
        return runningInstances.get(id) != null;
    }

    public EventRelayManager get(String id) {
        return isRunning(id) ? runningInstances.get(id) : null;
    }

    public EventRelayManager removeRelay(String id) {
        EventRelayManager eventRelayManager = runningInstances.get(id);
        runningInstances.remove(id);
        return eventRelayManager;
    }
}
