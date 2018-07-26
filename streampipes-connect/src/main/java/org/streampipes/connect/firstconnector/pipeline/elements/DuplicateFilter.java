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

package org.streampipes.connect.firstconnector.pipeline.elements;

import org.streampipes.connect.firstconnector.pipeline.AdapterPipelineElement;

import java.util.HashMap;
import java.util.Map;

public class DuplicateFilter implements AdapterPipelineElement {

    private static final long CLEAN_UP_INTERVAL_MILLI_SEC = 1000 * 60 * 60; //1 Hour
    private static final long LIFE_TIME_EVENT_DUPLICATE = 1000 * 60 * 60 * 24; //24 Hour

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

           eventState.entrySet().removeIf(entry -> entry.getValue() + LIFE_TIME_EVENT_DUPLICATE < System.currentTimeMillis());

        }
    }
}
