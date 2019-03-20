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

import org.streampipes.model.schema.EventSchema;

public class InfoResult {

    private String index;
    private EventSchema eventSchema;

    public InfoResult() {
    }

    public InfoResult(String index, EventSchema eventSchema) {
        this.index = index;
        this.eventSchema = eventSchema;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public EventSchema getEventSchema() {
        return eventSchema;
    }

    public void setEventSchema(EventSchema eventSchema) {
        this.eventSchema = eventSchema;
    }
}
