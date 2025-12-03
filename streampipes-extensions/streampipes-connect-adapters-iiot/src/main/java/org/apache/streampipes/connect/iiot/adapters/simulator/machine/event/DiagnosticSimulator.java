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
import org.apache.streampipes.model.schema.EventPropertyNested;
import org.apache.streampipes.sdk.builder.NestedPropertyBuilder;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.utils.Datatypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.streampipes.connect.iiot.adapters.simulator.machine.MachineDataSimulatorUtils.TIMESTAMP;
import static org.apache.streampipes.sdk.helpers.EpProperties.timestampProperty;

public class DiagnosticSimulator implements EventSimulator {

  private static final String SENSOR_ID = "sensorId";
  private static final String SENSOR_TYPE = "sensorType";
  private static final String PHASE = "phase";
  private static final String ACTIVE = "active";
  private static final String TAGS = "tags";
  private static final String METRICS = "metrics";
  private static final String RECENT_SAMPLES = "recentSamples";
  private static final String VOLUME_FLOW = "volume_flow";
  private static final String TEMPERATURE = "temperature";
  private static final String ACTIVE_ALARMS = "activeAlarms";
  private static final String OVERALL_STATE = "overallState";
  private static final String STATUS = "status";
  private static final String OFFSET_MS = "offsetMs";

  private final SensorValueSimulator valueSimulator;

  public DiagnosticSimulator(SensorValueSimulator valueSimulator) {
    this.valueSimulator = valueSimulator;
  }

  @Override
  public Map<String, Object> buildEvent(int simulationPhase, int sensorIndex, long timestamp) {
    Map<String, Object> event = new HashMap<>();

    event.put(TIMESTAMP, timestamp);
    event.put(SENSOR_ID, String.format("sensor%02d", sensorIndex));
    event.put(SENSOR_TYPE, "diagnostic");
    event.put(PHASE, simulationPhase);
    event.put(ACTIVE, Boolean.TRUE);

    List<String> tags = new ArrayList<>();
    tags.add("diagnostic");
    tags.add(simulationPhase == 0 ? "normal" : "alarm");
    event.put(TAGS, tags);

    Map<String, Object> metrics = new HashMap<>();

    double volumeFlowValue = valueSimulator.randomDoubleBetween(0, 10);
    double temperatureValue = (simulationPhase == 0)
        ? valueSimulator.randomDoubleBetween(40, 50)
        : valueSimulator.randomDoubleBetween(80, 100);

    metrics.put(VOLUME_FLOW,
        buildMetricBlock("l/s", volumeFlowValue, 0.0, 10.0, 1.0, 9.0));
    metrics.put(TEMPERATURE,
        buildMetricBlock("°C", temperatureValue,
            (simulationPhase == 0 ? 30.0 : 70.0),
            (simulationPhase == 0 ? 60.0 : 120.0),
            (simulationPhase == 0 ? 35.0 : 75.0),
            (simulationPhase == 0 ? 55.0 : 110.0)));


    event.put(METRICS, metrics);

    List<Map<String, Object>> samples = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      Map<String, Object> sample = new HashMap<>();
      sample.put(OFFSET_MS, -i * 1000);
      sample.put(VOLUME_FLOW, volumeFlowValue + (i - 1) * 0.1);
      sample.put(TEMPERATURE, temperatureValue + (i - 1) * 0.2);
      samples.add(sample);
    }
    event.put(RECENT_SAMPLES, samples);

    Map<String, Object> status = new HashMap<>();
    status.put(OVERALL_STATE, simulationPhase == 0 ? "OK" : "ALARM");

    List<String> activeAlarms = new ArrayList<>();
    if (simulationPhase != 0) {
      if (temperatureValue > 80) {
        activeAlarms.add("HIGH_TEMPERATURE");
      }
    }
    status.put(ACTIVE_ALARMS, activeAlarms);

    event.put(STATUS, status);
    return event;
  }

  @Override
  public GuessSchema getSchema() {
    return GuessSchemaBuilder.create()
        .property(timestampProperty(TIMESTAMP))
        .sample(TIMESTAMP, System.currentTimeMillis())
        .property(
            PrimitivePropertyBuilder
                .create(Datatypes.String, SENSOR_ID)
                .label("Sensor ID")
                .description("The ID of the sensor")
                .build()
        )
        .sample(SENSOR_ID, "sensor01")
        .property(
            PrimitivePropertyBuilder
                .create(Datatypes.String, SENSOR_TYPE)
                .label("Sensor Type")
                .description("The type of the sensor")
                .build()
        )
        .sample(SENSOR_TYPE, "diagnostic")
        .property(
            PrimitivePropertyBuilder.create(Datatypes.Integer, PHASE)
                .label("Phase")
                .description("The current simulation phase")
                .build()
        )
        .sample(PHASE, 0)
        .property(
            PrimitivePropertyBuilder.create(Datatypes.Boolean, ACTIVE)
                .label("Active")
                .description("Indicates whether the sensor is active")
                .build()
        )
        .sample(ACTIVE, true)
        .property(
            EpProperties.listStringEp(
                Labels.from(TAGS, "Tags", "Tags associated with the sensor"),
                TAGS,
                null)
        )
        .sample(TAGS, List.of("diagnostic", "normal"))
        .property(
            NestedPropertyBuilder.create(METRICS)
                .withEventProperty(
                    makeMetricsSchema(VOLUME_FLOW)
                )
                .withEventProperty(
                    makeMetricsSchema(TEMPERATURE)
                )
                .build()
        )
        .sample(METRICS, Map.of(
            VOLUME_FLOW, Map.of("unit", "l/s", "value", 5.0, "thresholds",
                Map.of("min", 0.0, "max", 10.0, "warningMin", 1.0, "warningMax", 9.0),
                "recentValues", List.of(4.75, 5.0, 5.25),
                "samples", List.of(
                    Map.of("index", 0, "sampleValue", 4.9, "quality", "GOOD"),
                    Map.of("index", 1, "sampleValue", 5.0, "quality", "FAIR"),
                    Map.of("index", 2, "sampleValue", 5.1, "quality", "POOR")
                )
            ),
            TEMPERATURE, Map.of("unit", "°C", "value", 45.0, "thresholds",
                Map.of("min", 30.0, "max", 60.0, "warningMin", 35.0, "warningMax", 55.0),
                "recentValues", List.of(42.75, 45.0, 47.25),
                "samples", List.of(
                    Map.of("index", 0, "sampleValue", 44.8, "quality", "GOOD"),
                    Map.of("index", 1, "sampleValue", 45.0, "quality", "FAIR"),
                    Map.of("index", 2, "sampleValue", 45.2, "quality", "POOR")
                )
            )
        ))
        .property(
            EpProperties.listNestedEp(
                Labels.from(RECENT_SAMPLES, "Recent Samples", "Recent sensor samples"),
                RECENT_SAMPLES,
                List.of(
                    PrimitivePropertyBuilder
                        .create(Datatypes.Integer, OFFSET_MS)
                        .label("Offset (ms)")
                        .description("Offset in milliseconds from the event timestamp")
                        .build(),
                    PrimitivePropertyBuilder
                        .create(Datatypes.Double, VOLUME_FLOW)
                        .label("Volume Flow")
                        .description("Volume flow value")
                        .build(),
                    PrimitivePropertyBuilder
                        .create(Datatypes.Double, TEMPERATURE)
                        .label("Temperature")
                        .description("Temperature value")
                        .build()
                )
            )
        )
        .sample(RECENT_SAMPLES, List.of(
            Map.of(
                OFFSET_MS, -0,
                VOLUME_FLOW, 5.0,
                TEMPERATURE, 45.0
            ),
            Map.of(
                OFFSET_MS, -1000,
                VOLUME_FLOW, 4.9,
                TEMPERATURE, 44.8
            ),
            Map.of(
                OFFSET_MS, -2000,
                VOLUME_FLOW, 5.1,
                TEMPERATURE, 45.2
        )))
        .property(
            NestedPropertyBuilder.create("status")
                .withEventProperty(
                    PrimitivePropertyBuilder
                        .create(Datatypes.String, OVERALL_STATE)
                        .label("Overall State")
                        .description("Overall state of the sensor")
                        .build()
                )
                .withEventProperty(
                    EpProperties.listStringEp(
                        Labels.from(ACTIVE_ALARMS, "Active Alarms", "List of active alarms"),
                        ACTIVE_ALARMS,
                        null)
                )
                .build()
        )
        .sample("status", Map.of(
            OVERALL_STATE, "OK",
            ACTIVE_ALARMS, List.of()
        ))
        .build();
  }

  private EventPropertyNested makeMetricsSchema(String metricsName) {
    return NestedPropertyBuilder
        .create(metricsName)
        .withEventProperty(
            PrimitivePropertyBuilder
                .create(Datatypes.String, "unit")
                .label("Unit")
                .description("Unit of the volume flow")
                .build()
        )
        .withEventProperty(
            PrimitivePropertyBuilder
                .create(Datatypes.Double, "value")
                .label("Value")
                .description("Current value of the volume flow")
                .build()
        )
        .withEventProperty(
            NestedPropertyBuilder
                .create("thresholds")
                .withEventProperty(
                    PrimitivePropertyBuilder
                        .create(Datatypes.Double, "min")
                        .label("Min")
                        .description("Minimum threshold")
                        .build()
                )
                .withEventProperty(
                    PrimitivePropertyBuilder
                        .create(Datatypes.Double, "max")
                        .label("Max")
                        .description("Maximum threshold")
                        .build()
                )
                .withEventProperty(
                    PrimitivePropertyBuilder
                        .create(Datatypes.Double, "warningMin")
                        .label("Warning Min")
                        .description("Warning minimum threshold")
                        .build()
                )
                .withEventProperty(
                    PrimitivePropertyBuilder
                        .create(Datatypes.Double, "warningMax")
                        .label("Warning Max")
                        .description("Warning maximum threshold")
                        .build()
                )
                .build())
        .withEventProperty(
            EpProperties.listDoubleEp(
                Labels.from("recentValues", "Recent Values", "Recent volume flow values"),
                "recentValues",
                null
            )
        )
        .withEventProperty(
            EpProperties.listNestedEp(
                Labels.from("samples", "Samples", "Recent volume flow samples"),
                "samples",
                List.of(
                    PrimitivePropertyBuilder
                        .create(Datatypes.Integer, "index")
                        .label("Index")
                        .description("Sample index")
                        .build(),
                    PrimitivePropertyBuilder
                        .create(Datatypes.Double, "sampleValue")
                        .label("Sample Value")
                        .description("Value of the sample")
                        .build(),
                    PrimitivePropertyBuilder
                        .create(Datatypes.String, "quality")
                        .label("Quality")
                        .description("Quality of the sample")
                        .build()
                )
            )
        )
        .build();
  }

  private Map<String, Object> buildMetricBlock(String unit,
                                               double value,
                                               double min,
                                               double max,
                                               double warningMin,
                                               double warningMax) {
    Map<String, Object> metric = new HashMap<>();
    metric.put("unit", unit);
    metric.put("value", value);

    Map<String, Object> thresholds = new HashMap<>();
    thresholds.put("min", min);
    thresholds.put("max", max);
    thresholds.put("warningMin", warningMin);
    thresholds.put("warningMax", warningMax);
    metric.put("thresholds", thresholds);

    List<Double> recentValues = new ArrayList<>();
    recentValues.add(value * 0.95);
    recentValues.add(value);
    recentValues.add(value * 1.05);
    metric.put("recentValues", recentValues);

    List<Map<String, Object>> samples = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      Map<String, Object> sample = new HashMap<>();
      sample.put("index", i);
      sample.put("sampleValue", value + (i - 1) * 0.1);
      sample.put("quality", i == 0 ? "GOOD" : (i == 1 ? "FAIR" : "POOR"));
      samples.add(sample);
    }
    metric.put("samples", samples);

    return metric;
  }
}
