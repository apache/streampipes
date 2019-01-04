/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.streampipes.connect.adapter.generic.pipeline.elements;

import org.streampipes.connect.adapter.generic.pipeline.AdapterPipelineElement;

import java.util.HashMap;
import java.util.Map;

/**
 *  A hash of events is stored to check if event was already sent
 *  If the same event is sent multiple times the timer is always reseted to cover polling of rest endpoints
 *  User can configure how long events are stored in cache, it should be minimum 2x the polling intervall
 */
public class DuplicateFilter implements AdapterPipelineElement {

    /**
     * Lifetime of events
     */
    private long filterTimeWindow;

    public DuplicateFilter(String filterTimeWindow) {
        // convert it to seconds
        this.filterTimeWindow = 1000 * Long.parseLong(filterTimeWindow);
    }

    // Trade of between computation and storage, maybe change value in future
    private static final long CLEAN_UP_INTERVAL_MILLI_SEC = 10000   ; //10 seconds

    private Map<Integer, Long> eventState = new HashMap<>();
    private long lastCleanUpTimestamp = System.currentTimeMillis();

    @Override
    public Map<String, Object> process(Map<String, Object> event) {
        cleanUpEvenState();

        System.out.println("State size: " + eventState.keySet().size());

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
