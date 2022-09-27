/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.streampipes.connect.iiot.protocol.stream.websocket;

import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.utils.Datatypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebsocketSchemaUtils {

  public static Map<String, Object> prepareTemperatureEvent(Map<String, Object> input) {

    Map<String, Object> temperatureEvent = new HashMap<>();

    temperatureEvent.put("temperature", input.get("temperature"));
    temperatureEvent.put("timestamp", input.get("timestamp"));

    return temperatureEvent;
  }

  public static Map<String, Object> prepareAccelerometerEvent(Map<String, Object> input) {

    Map<String, Object> accelerationEvent = new HashMap<>();

    HashMap<String, Object> accelerometerEvent = (HashMap<String, Object>) input.get("accelerometer");

    accelerationEvent.put("x", accelerometerEvent.get("x"));
    accelerationEvent.put("y", accelerometerEvent.get("y"));
    accelerationEvent.put("z", accelerometerEvent.get("z"));
    accelerationEvent.put("timestamp", input.get("timestamp"));


    return accelerationEvent;
  }

  public static GuessSchema getTemperatureSchema() {

    GuessSchema guessSchema = new GuessSchema();

    EventSchema eventSchema = new EventSchema();
    List<EventProperty> allProperties = new ArrayList<>();

    allProperties.add(EpProperties.timestampProperty("timestamp"));

    allProperties.add(
        PrimitivePropertyBuilder
            .create(Datatypes.Double, "temperature")
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .label("Temperature")
            .description("")
            .build());


    eventSchema.setEventProperties(allProperties);
    guessSchema.setEventSchema(eventSchema);
    return guessSchema;
  }

  public static GuessSchema getAccelerometerSchema() {

    GuessSchema guessSchema = new GuessSchema();

    EventSchema eventSchema = new EventSchema();
    List<EventProperty> allProperties = new ArrayList<>();

    allProperties.add(EpProperties.timestampProperty("timestamp"));

    // TODO implement
    allProperties.add(
        PrimitivePropertyBuilder
            .create(Datatypes.Double, "x")
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .label("accelerationX")
                .domainProperty("http://streampipes.org/hmi/accelerationX")
            .description("")
            .build()
    );
    allProperties.add(
            PrimitivePropertyBuilder
                    .create(Datatypes.Double, "y")
                    .scope(PropertyScope.MEASUREMENT_PROPERTY)
                    .label("accelerationY")
                    .domainProperty("http://streampipes.org/hmi/accelerationY")
                    .description("")
                    .build()
    );
    allProperties.add(
            PrimitivePropertyBuilder
                    .create(Datatypes.Double, "z")
                    .scope(PropertyScope.MEASUREMENT_PROPERTY)
                    .label("accelerationZ")
                    .domainProperty("http://streampipes.org/hmi/accelerationZ")
                    .description("")
                    .build()
    );


    eventSchema.setEventProperties(allProperties);
    guessSchema.setEventSchema(eventSchema);
    return guessSchema;
  }
}
