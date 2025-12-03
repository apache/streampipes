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

package org.apache.streampipes.connect.iiot.adapters.simulator.machine.event;

import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.vocabulary.SO;

import java.util.HashMap;
import java.util.Map;

import static org.apache.streampipes.connect.iiot.adapters.simulator.machine.MachineDataSimulatorUtils.HAS_SENSOR_ID;
import static org.apache.streampipes.connect.iiot.adapters.simulator.machine.MachineDataSimulatorUtils.SENSOR_ID;
import static org.apache.streampipes.connect.iiot.adapters.simulator.machine.MachineDataSimulatorUtils.TIMESTAMP;
import static org.apache.streampipes.sdk.helpers.EpProperties.timestampProperty;

public class WaterlevelSimulator implements EventSimulator {

  private final SensorValueSimulator valueSimulator;

  public WaterlevelSimulator(SensorValueSimulator valueSimulator) {
    this.valueSimulator = valueSimulator;
  }

  @Override
  public Map<String, Object> buildEvent(int simulationPhase, int sensorIndex, long timestamp) {
    Map<String, Object> event = new HashMap<>();

    event.put("timestamp", timestamp);
    event.put("sensorId", String.format("level%02d", sensorIndex));
    event.put("level",
        simulationPhase == 0 ? valueSimulator.randomDoubleBetween(20, 30) : valueSimulator.randomDoubleBetween(60, 80));
    event.put("overflow", simulationPhase != 0);

    return event;
  }

  @Override
  public GuessSchema getSchema() {
    return GuessSchemaBuilder.create()
        .property(timestampProperty(TIMESTAMP))
        .sample(TIMESTAMP, System.currentTimeMillis())
        .property(PrimitivePropertyBuilder
            .create(Datatypes.String, SENSOR_ID)
            .label("Sensor ID")
            .description("The ID of the sensor")
            .semanticType(HAS_SENSOR_ID)
            .scope(PropertyScope.DIMENSION_PROPERTY)
            .build())
        .sample(SENSOR_ID, "sensor01")
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Float, "level")
            .label("Water Level")
            .description("Denotes the current water level in the container")
            .semanticType(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample("level", 5.25f)
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Boolean, "overflow")
            .label("Overflow")
            .description("Indicates whether the tank overflows")
            .semanticType(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample("overflow", true)
        .build();
  }
}
