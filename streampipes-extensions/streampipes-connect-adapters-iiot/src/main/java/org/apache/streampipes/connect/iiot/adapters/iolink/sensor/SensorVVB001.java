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

package org.apache.streampipes.connect.iiot.adapters.iolink.sensor;

import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.apache.streampipes.sdk.utils.Datatypes;

import java.util.HashMap;
import java.util.Map;

import static org.apache.streampipes.sdk.helpers.EpProperties.timestampProperty;

public class SensorVVB001 implements IoLinkSensor {

  public static final String V_RMS_NAME = "vRms";
  public static final String A_PEAK_NAME = "aPeak";
  public static final String A_RMS_NAME = "aRms";
  public static final String TEMPERATURE_NAME = "temperature";
  public static final String CREST_NAME = "crest";
  public static final String STATUS_NAME = "status";
  public static final String OUT_1_NAME = "out1";
  public static final String OUT_2_NAME = "out2";
  public static final String PORT_NAME = "port";

  public static final String IO_LINK_MASTER_SN = "ioLinkMasterSN";


  @Override
  public GuessSchema getEventSchema() {

    return GuessSchemaBuilder.create()
        .property(timestampProperty("timestamp"))
        .sample("timestamp", 1685525380729L)
        .property(
            PrimitivePropertyBuilder
                .create(Datatypes.Float, V_RMS_NAME)
                .scope(PropertyScope.MEASUREMENT_PROPERTY)
                .description("Speed RMS value")
                .build()
        )
        .sample(V_RMS_NAME, 0.0023)
        .property(
            PrimitivePropertyBuilder
                .create(Datatypes.Float, A_PEAK_NAME)
                .scope(PropertyScope.MEASUREMENT_PROPERTY)
                .description("Acceleration peak value")
                .build()
        )
        .sample(A_PEAK_NAME, 6.6)
        .property(
            PrimitivePropertyBuilder
                .create(Datatypes.Float, A_RMS_NAME)
                .scope(PropertyScope.MEASUREMENT_PROPERTY)
                .description("Acceleration RMS value")
                .build()
        )
        .sample(A_RMS_NAME, 1.8)
        .property(
            PrimitivePropertyBuilder
                .create(Datatypes.Float, TEMPERATURE_NAME)
                .scope(PropertyScope.MEASUREMENT_PROPERTY)
                .description("Current temperature")
                .build()
        )
        .sample(TEMPERATURE_NAME, 22.0)
        .property(
            PrimitivePropertyBuilder
                .create(Datatypes.Float, CREST_NAME)
                .scope(PropertyScope.MEASUREMENT_PROPERTY)
                .description("Acceleration crest factor")
                .build()
        )
        .sample(CREST_NAME, 3.7)
        .property(
            PrimitivePropertyBuilder
                .create(Datatypes.Integer, STATUS_NAME)
                .scope(PropertyScope.MEASUREMENT_PROPERTY)
                .description("Device status (0: OK, 1: Maintenance required, "
                             + "2: Out of specification, 3: Function Test, 4: Error)")
                .build()
        )
        .sample(STATUS_NAME, 0)
        .property(
            PrimitivePropertyBuilder
                .create(Datatypes.Boolean, OUT_1_NAME)
                .scope(PropertyScope.MEASUREMENT_PROPERTY)
                .description("Current state of the digital signal")
                .build()
        )
        .sample(OUT_1_NAME, true)
        .property(
            PrimitivePropertyBuilder
                .create(Datatypes.Boolean, OUT_2_NAME)
                .scope(PropertyScope.MEASUREMENT_PROPERTY)
                .description("Current state of the digital signal")
                .build()
        )
        .sample(OUT_2_NAME, true)
        .property(
            PrimitivePropertyBuilder
                .create(Datatypes.String, PORT_NAME)
                .scope(PropertyScope.DIMENSION_PROPERTY)
                .description("Port the sensor is connected to at IOLink master")
                .build()
        )
        .sample(PORT_NAME, "port1")
        .property(
            PrimitivePropertyBuilder
                .create(Datatypes.String, IO_LINK_MASTER_SN)
                .scope(PropertyScope.DIMENSION_PROPERTY)
                .description("This is the serial number of the IO-Link Master")
                .build()
        )
        .sample(PORT_NAME, "XXXXXXXXXXXX")
        .build();
  }

  @Override
  public Map<String, Object> parseEvent(String encodedEvent) {
    var event = new HashMap<String, Object>();
    event.put(V_RMS_NAME, getValue(0, 4, 0.0001, encodedEvent));
    event.put(A_PEAK_NAME, getValue(8, 12, 0.1, encodedEvent));
    event.put(A_RMS_NAME, getValue(16, 20, 0.1, encodedEvent));
    event.put(TEMPERATURE_NAME, getValue(24, 28, 0.1, encodedEvent));
    event.put(CREST_NAME, getValue(32, 36, 0.1, encodedEvent));
    event.put(STATUS_NAME, (int) getValue(38, 39, 1.0, encodedEvent));

    // The last two bits represent the status
    var outValues = (int) getValue(39, 40, 1.0, encodedEvent);
    event.put(OUT_1_NAME, (outValues & 1) != 0);
    event.put(OUT_2_NAME, (outValues & 2) != 0);

    return event;
  }

  private double getValue(int start, int end, double scaleFactor, String encodedEvent) {
    var word = encodedEvent.substring(start, end);

    var rawValue = (double) (Integer.parseInt(word, 16));

    return rawValue * scaleFactor;
  }
}
