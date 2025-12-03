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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.apache.streampipes.connect.iiot.adapters.simulator.machine.MachineDataSimulatorUtils.HAS_SENSOR_ID;
import static org.apache.streampipes.connect.iiot.adapters.simulator.machine.MachineDataSimulatorUtils.MASS_FLOW;
import static org.apache.streampipes.connect.iiot.adapters.simulator.machine.MachineDataSimulatorUtils.SENSOR_ID;
import static org.apache.streampipes.connect.iiot.adapters.simulator.machine.MachineDataSimulatorUtils.TEMPERATURE;
import static org.apache.streampipes.connect.iiot.adapters.simulator.machine.MachineDataSimulatorUtils.TIMESTAMP;
import static org.apache.streampipes.sdk.helpers.EpProperties.timestampProperty;

public class FlowSimulator implements EventSimulator {

  private final SensorValueSimulator valueSimulator;

  public FlowSimulator(SensorValueSimulator valueSimulator) {
    this.valueSimulator = valueSimulator;
  }

  @Override
  public Map<String, Object> buildEvent(int simulationPhase, int sensorIndex, long timestamp) {
    Map<String, Object> event = new HashMap<>();

    event.put("timestamp", timestamp);
    event.put("sensorId", String.format("flowrate%02d", sensorIndex));
    event.put("mass_flow", valueSimulator.randomDoubleBetween(0, 10));
    event.put("volume_flow", valueSimulator.randomDoubleBetween(0, 10));
    event.put("temperature",
        simulationPhase == 0 ? valueSimulator.randomDoubleBetween(40, 50) : valueSimulator.randomDoubleBetween(80, 100));
    event.put("density", valueSimulator.randomDoubleBetween(40, 50));
    event.put("sensor_fault_flags", simulationPhase != 0);

    return event;
  }

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
            .create(Datatypes.Float, MASS_FLOW)
            .label("Mass Flow")
            .description("Denotes the current mass flow in the sensor")
            .semanticType(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample(MASS_FLOW, 5.76f)
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Float, "volume_flow")
            .label("Volume Flow")
            .description("Denotes the current volume flow")
            .semanticType(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample("volume_flow", 3.34f)
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Float, TEMPERATURE)
            .label("Temperature")
            .description("Denotes the current temperature in degrees celsius")
            .semanticType(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .measurementUnit(URI.create("http://qudt.org/vocab/unit#DegreeCelsius"))
            .valueSpecification(0.0f, 100.0f, 0.1f)
            .build())
        .sample(TEMPERATURE, 33.221f)
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Float, "density")
            .label("Density")
            .description("Denotes the current density of the fluid")
            .semanticType(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample("density", 5.0f)
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Boolean, "sensor_fault_flags")
            .label("Sensor Fault Flags")
            .description("Any fault flags of the sensors")
            .semanticType(SO.BOOLEAN)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample("sensor_fault_flags", true)
        .build();
  }
}
