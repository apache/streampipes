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

package org.apache.streampipes.connect.adapters.flic;

import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.utils.Datatypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlicUtils {
  public static final String TIMESTAMP_KEY = "timestamp";
  public static final String BUTTON_ID_KEY = "button_id";
  public static final String CLICK_TYPE_KEY = "click_type";

  public static GuessSchema getFlicSchema() {

    GuessSchema guessSchema = new GuessSchema();

    EventSchema eventSchema = new EventSchema();
    List<EventProperty> allProperties = new ArrayList<>();

    allProperties.add(EpProperties.timestampProperty(TIMESTAMP_KEY));
    allProperties.add(
        PrimitivePropertyBuilder
            .create(Datatypes.String, BUTTON_ID_KEY)
            .label("Button ID")
            .description("The ID of the button")
            .build());
    allProperties.add(
        PrimitivePropertyBuilder
            .create(Datatypes.String, CLICK_TYPE_KEY)
            .label("Click Type")
            .description("Type of the click")
            .build());

    eventSchema.setEventProperties(allProperties);
    guessSchema.setEventSchema(eventSchema);
    return guessSchema;
  }

  public static Map<String, Object> getEvent(FlicOutput output) {
    Map<String, Object> event = new HashMap<>();

    event.put(TIMESTAMP_KEY, output.getTimestamp());
    event.put(BUTTON_ID_KEY, output.getButtonID());
    event.put(CLICK_TYPE_KEY, output.getClickType());
    return event;
  }

}
