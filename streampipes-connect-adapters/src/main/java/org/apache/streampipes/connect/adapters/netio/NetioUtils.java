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

package org.apache.streampipes.connect.adapters.netio;

import org.apache.streampipes.connect.adapters.netio.model.NetioGlobalMeasure;
import org.apache.streampipes.connect.adapters.netio.model.NetioPowerOutput;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.utils.Datatypes;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetioUtils {
    public static final String TIMESTAMP_KEY = "timestamp";
    public static final String NAME_KEY = "name";
    public static final String VOLTAGE_KEY = "voltage";
    public static final String FREQUENCY_KEY = "frequency";
    public static final String OVERALL_POWER_FACTOR_KEY = "overall_power_factor";
    public static final String TOTAL_CURRENT_KEY = "total_current";
    public static final String STATE_KEY = "state";
    public static final String ACTION_KEY = "action";
    public static final String DELAY_KEY = "delay";
    public static final String CURRENT_KEY = "current";
    public static final String POWER_FACTOR_KEY = "power_factor";
    public static final String LOAD_KEY = "load";
    public static final String ENERGY_KEY = "energy";

    public static URI VOLT = null;
    public static URI WATT = null;
    public static URI AMPERE = null;
    public static URI WATTHOUR = null;
    public static URI HERTZ = null;

    static {
        try {
            VOLT = new URI("http://qudt.org/vocab/unit#Volt");
            WATT = new URI("http://qudt.org/vocab/unit#Watt");
            AMPERE = new URI("http://qudt.org/vocab/unit#Ampere");
            WATTHOUR = new URI("http://qudt.org/vocab/unit#Watthour");
            HERTZ = new URI("http://qudt.org/vocab/unit#Hertz");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    public static GuessSchema getNetioSchema() {

        GuessSchema guessSchema = new GuessSchema();

        EventSchema eventSchema = new EventSchema();
        List<EventProperty> allProperties = new ArrayList<>();

        allProperties.add(EpProperties.timestampProperty(TIMESTAMP_KEY));
        allProperties.add(
                PrimitivePropertyBuilder
                        .create(Datatypes.String, NAME_KEY)
                        .label("Name")
                        .description("The configured name of plug")
                        .build());
        allProperties.add(
                PrimitivePropertyBuilder
                        .create(Datatypes.Float, VOLTAGE_KEY)
                        .label("Voltage")
                        .description("Instantaneous voltage")
                        .measurementUnit(VOLT)
                        .build());
        allProperties.add(
                PrimitivePropertyBuilder
                        .create(Datatypes.Float, FREQUENCY_KEY)
                        .label("Frequency")
                        .description("Instantaneous frequency")
                        .measurementUnit(HERTZ)
                        .build());
        allProperties.add(
                PrimitivePropertyBuilder
                        .create(Datatypes.Float, OVERALL_POWER_FACTOR_KEY)
                        .label("Overall Power Factor")
                        .description("Instantaneous Power Factor - weighted average from all meters")
                        .build());
        allProperties.add(
                PrimitivePropertyBuilder
                        .create(Datatypes.Float, TOTAL_CURRENT_KEY)
                        .label("Total Current")
                        .description("Instantaneous total current for all power outputs")
                        .build());
        allProperties.add(
                PrimitivePropertyBuilder
                        .create(Datatypes.Integer, STATE_KEY)
                        .label("State")
                        .description("State of the plug. 1 is on. 0 is off")
                        .build());
        allProperties.add(
                PrimitivePropertyBuilder
                        .create(Datatypes.Integer, ACTION_KEY)
                        .label("Action")
                        .description("")
                        .build());
        allProperties.add(
                PrimitivePropertyBuilder
                        .create(Datatypes.Integer, DELAY_KEY)
                        .label("Delay")
                        .description("")
                        .build());
        allProperties.add(
                PrimitivePropertyBuilder
                        .create(Datatypes.Float, CURRENT_KEY)
                        .label("Current")
                        .description("Instantaneous current for the specific power output")
                        .measurementUnit(AMPERE)
                        .build());
        allProperties.add(
                PrimitivePropertyBuilder
                        .create(Datatypes.Float, POWER_FACTOR_KEY)
                        .label("Power Factor")
                        .description("Instantaneous Power Factor for the specific power output")
                        .build());
        allProperties.add(
                PrimitivePropertyBuilder
                        .create(Datatypes.Float, LOAD_KEY)
                        .label("Load")
                        .description("Instantaneous load for the specific power output")
                        .build());
        allProperties.add(
                PrimitivePropertyBuilder
                        .create(Datatypes.Float, ENERGY_KEY)
                        .label("Energy")
                        .description("Instantaneous energy counter for the value for the specific power output")
                        .measurementUnit(WATTHOUR)
                        .build());

        eventSchema.setEventProperties(allProperties);
        guessSchema.setEventSchema(eventSchema);
        guessSchema.setPropertyProbabilityList(new ArrayList<>());
        return guessSchema;
    }

    public static Map<String, Object> getEvent(NetioGlobalMeasure globalMeasure, NetioPowerOutput powerOutput) {
        Map<String, Object> event = new HashMap<>();

        event.put(TIMESTAMP_KEY,System.currentTimeMillis());
        event.put(NAME_KEY, powerOutput.getName());
        event.put(VOLTAGE_KEY, globalMeasure.getVoltage());
        event.put(FREQUENCY_KEY, globalMeasure.getFrequency());
        event.put(OVERALL_POWER_FACTOR_KEY, globalMeasure.getOverallPowerFactor());
        event.put(TOTAL_CURRENT_KEY, globalMeasure.getTotalCurrent());
        event.put(STATE_KEY, powerOutput.getState());
        event.put(ACTION_KEY, powerOutput.getAction());
        event.put(DELAY_KEY, powerOutput.getDelay());
        event.put(CURRENT_KEY, powerOutput.getCurrent());
        event.put(POWER_FACTOR_KEY, powerOutput.getPowerFactor());
        event.put(LOAD_KEY, powerOutput.getLoad());
        event.put(ENERGY_KEY, powerOutput.getEnergy());

        return event;
    }

}
