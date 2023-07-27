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

package org.apache.streampipes.connect.shared.preprocessing.transform.stream;

import org.apache.streampipes.extensions.api.connect.IAdapterPipelineElement;

import java.util.HashMap;
import java.util.Map;

/**
 * A hash of events is stored to check if event was already sent
 * If the same event is sent multiple times the timer is always reseted to cover polling of rest endpoints
 * User can configure how long events are stored in cache, it should be minimum 2x the polling intervall
 */
public class DuplicateFilterPipelineElement implements IAdapterPipelineElement {

  /**
   * Lifetime of events
   */
  private long filterTimeWindow;

  public DuplicateFilterPipelineElement(String filterTimeWindow) {
    // convert it to seconds
    this.filterTimeWindow = 1000 * Long.parseLong(filterTimeWindow);
  }

  // Trade of between computation and storage, maybe change value in future
  private static final long CLEAN_UP_INTERVAL_MILLI_SEC = 10000; //10 seconds

  private Map<Integer, Long> eventState = new HashMap<>();
  private long lastCleanUpTimestamp = System.currentTimeMillis();

  @Override
  public Map<String, Object> process(Map<String, Object> event) {
    cleanUpEvenState();

    int hash = event.hashCode();

    if (isDuplicatedEvent(hash)) {
      saveEvent(hash);
      return null;
    }
    saveEvent(hash);
    return event;
  }

  private boolean isDuplicatedEvent(int key) {
    return eventState.containsKey(key);
  }

  private void saveEvent(int key) {
    eventState.put(key, System.currentTimeMillis());
  }

  private void cleanUpEvenState() {
    if (System.currentTimeMillis() > lastCleanUpTimestamp + CLEAN_UP_INTERVAL_MILLI_SEC) {
      eventState.entrySet().removeIf(entry -> entry.getValue() + filterTimeWindow < System.currentTimeMillis());
      lastCleanUpTimestamp = System.currentTimeMillis();
    }
  }
}
