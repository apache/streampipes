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
package org.apache.streampipes.connect.iiot.adapters.simulator.machine;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.vocabulary.SO;

import java.net.URI;

import static org.apache.streampipes.sdk.helpers.EpProperties.timestampProperty;

public class MachineDataSimulatorUtils {

  // Vocabulary
  public static final String NS = "https://streampipes.org/vocabulary/examples/watertank/v1/";
  public static final String HAS_SENSOR_ID = NS + "hasSensorId";

  private static final String TIMESTAMP = "timestamp";
  private static final String SENSOR_ID = "sensorId";
  private static final String MASS_FLOW = "mass_flow";
  private static final String TEMPERATURE = "temperature";

  public static GuessSchema getSchema(String selectedSimulatorOption) throws AdapterException {
    switch (selectedSimulatorOption) {
      case "flowrate":
        return getFlowrateSchema();
      case "pressure":
        return getPressureSchema();
      case "waterlevel":
        return getWaterlevelSchema();
      default:
        throw new AdapterException("resource not found");
    }
  }

  private static GuessSchema getWaterlevelSchema() {
    return GuessSchemaBuilder.create()
        .property(timestampProperty(TIMESTAMP))
        .sample(TIMESTAMP, System.currentTimeMillis())
        .property(PrimitivePropertyBuilder
            .create(Datatypes.String, "sensorId")
            .label("Sensor ID")
            .description("The ID of the sensor")
            .domainProperty(HAS_SENSOR_ID)
            .scope(PropertyScope.DIMENSION_PROPERTY)
            .build())
        .sample("sensorId", "sensor01")
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Float, "level")
            .label("Water Level")
            .description("Denotes the current water level in the container")
            .domainProperty(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample("level", 5.25f)
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Boolean, "overflow")
            .label("Overflow")
            .description("Indicates whether the tank overflows")
            .domainProperty(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample("overflow", true)
        .build();
  }

  private static GuessSchema getPressureSchema() {
    return GuessSchemaBuilder.create()
        .property(timestampProperty(TIMESTAMP))
        .sample(TIMESTAMP, System.currentTimeMillis())
        .property(PrimitivePropertyBuilder
            .create(Datatypes.String, "sensorId")
            .label("Sensor ID")
            .description("The ID of the sensor")
            .domainProperty(HAS_SENSOR_ID)
            .scope(PropertyScope.DIMENSION_PROPERTY)
            .build())
        .sample("sensorId", "sensor01")
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Float, "pressure")
            .label("Pressure")
            .description("Denotes the current pressure in the pressure tank")
            .domainProperty(SO.NUMBER)
            .valueSpecification(0.0f, 100.0f, 0.5f)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample("pressure", 85.22f)
        .build();
  }

  public static GuessSchema getFlowrateSchema() {
    return GuessSchemaBuilder.create()
        .property(timestampProperty(TIMESTAMP))
        .sample(TIMESTAMP, System.currentTimeMillis())
        .property(PrimitivePropertyBuilder
            .create(Datatypes.String, SENSOR_ID)
            .label("Sensor ID")
            .description("The ID of the sensor")
            .domainProperty(HAS_SENSOR_ID)
            .scope(PropertyScope.DIMENSION_PROPERTY)
            .build())
        .sample("sensorId", "sensor01")
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Float, MASS_FLOW)
            .label("Mass Flow")
            .description("Denotes the current mass flow in the sensor")
            .domainProperty(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample(MASS_FLOW, 5.76f)
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Float, "volume_flow")
            .label("Volume Flow")
            .description("Denotes the current volume flow")
            .domainProperty(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample("volume_flow", 3.34f)
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Float, TEMPERATURE)
            .label("Temperature")
            .description("Denotes the current temperature in degrees celsius")
            .domainProperty(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .measurementUnit(URI.create("http://qudt.org/vocab/unit#DegreeCelsius"))
            .valueSpecification(0.0f, 100.0f, 0.1f)
            .build())
        .sample(TEMPERATURE, 33.221f)
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Float, "density")
            .label("Density")
            .description("Denotes the current density of the fluid")
            .domainProperty(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample("density", 5.0f)
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Boolean, "sensor_fault_flags")
            .label("Sensor Fault Flags")
            .description("Any fault flags of the sensors")
            .domainProperty(SO.BOOLEAN)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample("sensor_fault_flags", true)
        .build();
  }
}
