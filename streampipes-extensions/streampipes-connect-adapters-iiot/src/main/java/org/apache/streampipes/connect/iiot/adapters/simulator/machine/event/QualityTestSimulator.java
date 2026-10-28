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
import org.apache.streampipes.model.connect.guess.SampleData;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.apache.streampipes.sdk.builder.adapter.SampleDataBuilder;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.vocabulary.SO;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.apache.streampipes.connect.iiot.adapters.simulator.machine.MachineDataSimulatorUtils.HAS_SENSOR_ID;
import static org.apache.streampipes.connect.iiot.adapters.simulator.machine.MachineDataSimulatorUtils.SENSOR_ID;
import static org.apache.streampipes.connect.iiot.adapters.simulator.machine.MachineDataSimulatorUtils.TIMESTAMP;
import static org.apache.streampipes.sdk.helpers.EpProperties.timestampProperty;

/**
 * Simulates end-of-line leak test rigs. Every rig loads a part, pressurises it, holds the
 * pressure while measuring the pressure drop and reports the result; the last result stays
 * on the rig's tags until the next part is done. A few percent of parts leak and fail. In
 * the alarm phase a bad batch arrives and the failure rate climbs.
 */
public class QualityTestSimulator extends StatefulMachineSimulator<QualityTestSimulator.State> {

  public static final String TEST_STATE = "test_state";
  public static final String PART_ID = "part_id";
  public static final String TEST_PRESSURE = "test_pressure";
  public static final String LEAK_RATE = "leak_rate";
  public static final String TEST_DURATION = "test_duration";
  public static final String PASSED = "passed";
  public static final String TEST_RESULT = "test_result";
  public static final String PARTS_TESTED = "parts_tested";
  public static final String PARTS_FAILED = "parts_failed";

  public static final String STATE_LOADING = "LOADING";
  public static final String STATE_PRESSURIZING = "PRESSURIZING";
  public static final String STATE_TESTING = "TESTING";
  public static final String RESULT_PASS = "PASS";
  public static final String RESULT_FAIL = "FAIL";

  static final double TARGET_PRESSURE = 3.0;
  static final double LEAK_LIMIT = 2.0;
  private static final double LOADING_SECONDS = 6.0;
  private static final double PRESSURIZING_SECONDS = 5.0;

  public static class State extends MachineState {
    String partId;
    double phaseElapsed = 0;
    double testDuration = 30;
    double pressure = 0;
    double leakRate = 0.8;
    double lastLeakRate = 0.8;
    double lastDuration = 30;
    boolean lastPassed = true;
    int step = 0; // 0 loading, 1 pressurising, 2 testing
    int partsTested = 0;
    int partsFailed = 0;
  }

  public QualityTestSimulator() {
    this(System.nanoTime());
  }

  public QualityTestSimulator(long seed) {
    super(State::new, seed);
  }

  @Override
  protected Map<String, Object> buildEvent(State s, boolean alarmPhase, int sensorIndex, double dt, long timestamp) {
    if (s.partId == null) {
      startPart(s, alarmPhase);
    }
    s.phaseElapsed += dt;
    if (s.step == 0 && s.phaseElapsed >= LOADING_SECONDS) {
      s.step = 1;
      s.phaseElapsed = 0;
    } else if (s.step == 1 && s.phaseElapsed >= PRESSURIZING_SECONDS) {
      s.step = 2;
      s.phaseElapsed = 0;
    } else if (s.step == 2 && s.phaseElapsed >= s.testDuration) {
      finishPart(s);
      startPart(s, alarmPhase);
    }

    // pressure ramps up while pressurising and decays with the leak while testing
    if (s.step == 0) {
      s.pressure = approach(s.pressure, 0, dt, 1.0);
    } else if (s.step == 1) {
      s.pressure = approach(s.pressure, TARGET_PRESSURE + 0.05, dt, 1.5);
    } else {
      s.pressure = Math.max(0, s.pressure - s.leakRate * 0.004 * dt);
    }
    double pressure = s.pressure + gaussian(0, 0.005);

    Map<String, Object> event = new HashMap<>();
    event.put(TIMESTAMP, timestamp);
    event.put(SENSOR_ID, String.format("eol%02d", sensorIndex));
    event.put(TEST_STATE, s.step == 0 ? STATE_LOADING : (s.step == 1 ? STATE_PRESSURIZING : STATE_TESTING));
    event.put(PART_ID, s.partId);
    event.put(TEST_PRESSURE, round(Math.max(0, pressure)));
    event.put(LEAK_RATE, round(s.lastLeakRate));
    event.put(TEST_DURATION, round(s.lastDuration));
    event.put(PASSED, s.lastPassed);
    event.put(TEST_RESULT, s.lastPassed ? RESULT_PASS : RESULT_FAIL);
    event.put(PARTS_TESTED, s.partsTested);
    event.put(PARTS_FAILED, s.partsFailed);
    return event;
  }

  private void startPart(State s, boolean alarmPhase) {
    s.partId = String.format("GB-%06d", 100000 + random.nextInt(900000));
    s.step = 0;
    s.phaseElapsed = 0;
    s.testDuration = gaussian(30, 1);
    boolean leaking = chance(alarmPhase ? 0.4 : 0.03);
    s.leakRate = leaking ? uniform(4, 15) : Math.abs(gaussian(0.8, 0.4));
  }

  private void finishPart(State s) {
    s.lastLeakRate = s.leakRate;
    s.lastDuration = s.testDuration;
    s.lastPassed = s.leakRate <= LEAK_LIMIT;
    s.partsTested++;
    if (!s.lastPassed) {
      s.partsFailed++;
    }
  }

  @Override
  public SampleData getSampleData() {
    return SampleDataBuilder.create()
        .sample(new QualityTestSimulator(42).buildEvent(0, 1, System.currentTimeMillis()))
        .build();
  }

  @Override
  public GuessSchema getSchema() {
    return GuessSchemaBuilder.create()
        .property(timestampProperty(TIMESTAMP))
        .sample(TIMESTAMP, System.currentTimeMillis())
        .property(PrimitivePropertyBuilder
            .create(Datatypes.String, SENSOR_ID)
            .label("Sensor ID")
            .description("The ID of the simulated machine")
            .semanticType(HAS_SENSOR_ID)
            .scope(PropertyScope.DIMENSION_PROPERTY)
            .build())
        .sample(SENSOR_ID, "eol01")
        .property(PrimitivePropertyBuilder
            .create(Datatypes.String, TEST_STATE)
            .label("Test state")
            .description("LOADING, PRESSURIZING or TESTING")
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample(TEST_STATE, "TESTING")
        .property(PrimitivePropertyBuilder
            .create(Datatypes.String, PART_ID)
            .label("Part ID")
            .description("Serial number of the part on the rig")
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample(PART_ID, "GB-123456")
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Float, TEST_PRESSURE)
            .label("Test pressure")
            .description("Current pressure in the part in bar")
            .semanticType(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .measurementUnit(URI.create("http://qudt.org/vocab/unit#Bar"))
            .build())
        .sample(TEST_PRESSURE, 3.0f)
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Float, LEAK_RATE)
            .label("Leak rate")
            .description("Leak rate of the last tested part in millilitres per minute")
            .semanticType(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample(LEAK_RATE, 0.8f)
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Float, TEST_DURATION)
            .label("Test duration")
            .description("Duration of the last test in seconds")
            .semanticType(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .measurementUnit(URI.create("http://qudt.org/vocab/unit#SecondTime"))
            .build())
        .sample(TEST_DURATION, 30.0f)
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Boolean, PASSED)
            .label("Passed")
            .description("Whether the last tested part passed")
            .semanticType(SO.BOOLEAN)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample(PASSED, true)
        .property(PrimitivePropertyBuilder
            .create(Datatypes.String, TEST_RESULT)
            .label("Test result")
            .description("PASS or FAIL of the last tested part")
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample(TEST_RESULT, "PASS")
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Integer, PARTS_TESTED)
            .label("Parts tested")
            .description("Number of parts tested since the simulation started")
            .semanticType(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample(PARTS_TESTED, 0)
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Integer, PARTS_FAILED)
            .label("Parts failed")
            .description("Number of parts that failed the test")
            .semanticType(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample(PARTS_FAILED, 0)
        .build();
  }
}
