/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.rest.impl.datalake.model;

import java.util.List;
import java.util.Map;

public class GroupedDataResult {

    private int total;
    private Map<String, List<Map<String, Object>>> groupedEvents;

    public GroupedDataResult() {
    }

    public GroupedDataResult(int total, Map<String, List<Map<String, Object>>> groupedEvents) {
        this.total = total;
        this.groupedEvents = groupedEvents;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Map<String, List<Map<String, Object>>> getEvents() {
        return groupedEvents;
    }

    public void setEvents(Map<String, List<Map<String, Object>>>groupedEvents) {
        this.groupedEvents = groupedEvents;
    }
}
