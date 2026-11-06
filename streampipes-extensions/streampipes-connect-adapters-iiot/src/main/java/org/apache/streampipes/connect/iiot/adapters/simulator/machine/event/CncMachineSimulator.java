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
 * Simulates CNC machining centres. Every machine runs a machining cycle (load part, rough
 * cut, finish cut), its tool wears with every cutting second and is replaced when worn,
 * the spindle temperature follows the load with a lag and the vibration follows load and
 * wear. In the alarm phase the tool of one machine breaks: feed hold, vibration spike,
 * then a tool change.
 */
public class CncMachineSimulator extends StatefulMachineSimulator<CncMachineSimulator.State> {

  public static final String MACHINE_STATE = "machine_state";
  public static final String PROGRAM_ID = "program_id";
  public static final String SPINDLE_SPEED = "spindle_speed";
  public static final String SPINDLE_LOAD = "spindle_load";
  public static final String FEED_RATE = "feed_rate";
  public static final String SPINDLE_TEMPERATURE = "spindle_temperature";
  public static final String TOOL_WEAR = "tool_wear";
  public static final String COOLANT_FLOW = "coolant_flow";
  public static final String VIBRATION = "vibration";
  public static final String PARTS_COMPLETED = "parts_completed";

  public static final String STATE_LOADING = "LOADING";
  public static final String STATE_RUNNING = "RUNNING";
  public static final String STATE_TOOL_CHANGE = "TOOL_CHANGE";
  public static final String STATE_ALARM = "ALARM";

  static final double TOOL_CHANGE_THRESHOLD = 90.0;
  private static final double AMBIENT_TEMPERATURE = 24.0;
  private static final String[] PROGRAMS = {"GEARBOX-HOUSING-M3", "FLANGE-D120-R2", "SHAFT-CARRIER-L4"};

  enum Step {
    LOADING(10, 0, 0, 0),
    ROUGHING(40, 8000, 1200, 62),
    FINISHING(25, 12000, 600, 32),
    TOOL_CHANGE(15, 0, 0, 0),
    ALARM(12, 0, 0, 0);

    final double duration;
    final double spindleSpeed;
    final double feedRate;
    final double baseLoad;

    Step(double duration, double spindleSpeed, double feedRate, double baseLoad) {
      this.duration = duration;
      this.spindleSpeed = spindleSpeed;
      this.feedRate = feedRate;
      this.baseLoad = baseLoad;
    }

    boolean cutting() {
      return this == ROUGHING || this == FINISHING;
    }
  }

  public static class State extends MachineState {
    Step step = Step.ROUGHING;
    double stepElapsed = 0;
    double toolWear = 20;
    double temperature = 42;
    double spindleSpeed = Step.ROUGHING.spindleSpeed;
    double vibration = 2;
    int partsCompleted = 0;
    boolean incidentHandled = false;
  }

  public CncMachineSimulator() {
    this(System.nanoTime());
  }

  public CncMachineSimulator(long seed) {
    super(State::new, seed);
  }

  @Override
  protected Map<String, Object> buildEvent(State s, boolean alarmPhase, int sensorIndex, double dt, long timestamp) {
    if (alarmPhase && !s.incidentHandled && incidentTargets(sensorIndex) && s.step.cutting()) {
      // the tool breaks: the control stops the feed and spindle and raises an alarm
      s.step = Step.ALARM;
      s.stepElapsed = 0;
      s.incidentHandled = true;
    } else if (!alarmPhase) {
      s.incidentHandled = false;
    }

    advanceCycle(s, dt);

    boolean cutting = s.step.cutting();
    double load = cutting ? s.step.baseLoad + 0.3 * s.toolWear + gaussian(0, 2.5) : gaussian(3, 0.5);
    if (s.step == Step.ALARM && s.stepElapsed < 2) {
      load = 100; // the broken tool jams before the spindle stops
    }
    load = clamp(load, 0, 100);

    double targetSpeed = s.step == Step.ALARM ? 0 : s.step.spindleSpeed;
    s.spindleSpeed = approach(s.spindleSpeed, targetSpeed, dt, 2.0);
    double feedRate = s.step.feedRate > 0 ? s.step.feedRate * (1 - 0.1 * s.toolWear / 100) + gaussian(0, 10) : 0;

    double targetTemperature = AMBIENT_TEMPERATURE + 0.3 * load + 0.1 * s.toolWear;
    s.temperature = approach(s.temperature, targetTemperature, dt, 90.0) + gaussian(0, 0.05);

    double targetVibration = s.step == Step.ALARM && s.stepElapsed < 3
        ? 14
        : (cutting ? 0.4 + 0.02 * load + 0.035 * s.toolWear : 0.2);
    s.vibration = approach(s.vibration, targetVibration, dt, 1.5) + gaussian(0, 0.08);

    double coolant = cutting ? gaussian(20, 0.3) : 0;

    Map<String, Object> event = new HashMap<>();
    event.put(TIMESTAMP, timestamp);
    event.put(SENSOR_ID, String.format("cnc%02d", sensorIndex));
    event.put(PROGRAM_ID, PROGRAMS[Math.floorMod(sensorIndex - 1, PROGRAMS.length)]);
    event.put(MACHINE_STATE, stateName(s.step));
    event.put(SPINDLE_SPEED, round(Math.max(0, s.spindleSpeed + gaussian(0, s.spindleSpeed * 0.003))));
    event.put(SPINDLE_LOAD, round(load));
    event.put(FEED_RATE, round(Math.max(0, feedRate)));
    event.put(SPINDLE_TEMPERATURE, round(s.temperature));
    event.put(TOOL_WEAR, round(s.toolWear));
    event.put(COOLANT_FLOW, round(Math.max(0, coolant)));
    event.put(VIBRATION, round(Math.max(0, s.vibration)));
    event.put(PARTS_COMPLETED, s.partsCompleted);
    return event;
  }

  private void advanceCycle(State s, double dt) {
    if (s.step == Step.ROUGHING) {
      s.toolWear += 0.4 * dt;
    } else if (s.step == Step.FINISHING) {
      s.toolWear += 0.12 * dt;
    }
    s.toolWear = clamp(s.toolWear, 0, 100);
    s.stepElapsed += dt;
    if (s.stepElapsed < s.step.duration) {
      return;
    }
    s.stepElapsed = 0;
    switch (s.step) {
      case LOADING -> s.step = Step.ROUGHING;
      case ROUGHING -> s.step = Step.FINISHING;
      case FINISHING -> {
        s.partsCompleted++;
        s.step = s.toolWear >= TOOL_CHANGE_THRESHOLD ? Step.TOOL_CHANGE : Step.LOADING;
      }
      case TOOL_CHANGE -> {
        s.toolWear = uniform(2, 6);
        s.step = Step.LOADING;
      }
      case ALARM -> s.step = Step.TOOL_CHANGE;
      default -> s.step = Step.LOADING;
    }
  }

  private static String stateName(Step step) {
    return switch (step) {
      case LOADING -> STATE_LOADING;
      case TOOL_CHANGE -> STATE_TOOL_CHANGE;
      case ALARM -> STATE_ALARM;
      default -> STATE_RUNNING;
    };
  }

  @Override
  public SampleData getSampleData() {
    return SampleDataBuilder.create()
        .sample(new CncMachineSimulator(42).buildEvent(0, 1, System.currentTimeMillis()))
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
        .sample(SENSOR_ID, "cnc01")
        .property(PrimitivePropertyBuilder
            .create(Datatypes.String, PROGRAM_ID)
            .label("Program")
            .description("The NC program currently running")
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample(PROGRAM_ID, "GEARBOX-HOUSING-M3")
        .property(PrimitivePropertyBuilder
            .create(Datatypes.String, MACHINE_STATE)
            .label("Machine state")
            .description("LOADING, RUNNING, TOOL_CHANGE or ALARM")
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample(MACHINE_STATE, "RUNNING")
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Float, SPINDLE_SPEED)
            .label("Spindle speed")
            .description("Spindle speed in revolutions per minute")
            .semanticType(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .measurementUnit(URI.create("http://qudt.org/vocab/unit#RevolutionPerMinute"))
            .build())
        .sample(SPINDLE_SPEED, 8000.0f)
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Float, SPINDLE_LOAD)
            .label("Spindle load")
            .description("Spindle load in percent of the rated load")
            .semanticType(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .measurementUnit(URI.create("http://qudt.org/vocab/unit#Percent"))
            .build())
        .sample(SPINDLE_LOAD, 68.0f)
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Float, FEED_RATE)
            .label("Feed rate")
            .description("Feed rate in millimetres per minute")
            .semanticType(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample(FEED_RATE, 1180.0f)
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Float, SPINDLE_TEMPERATURE)
            .label("Spindle temperature")
            .description("Spindle bearing temperature in degrees celsius")
            .semanticType(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .measurementUnit(URI.create("http://qudt.org/vocab/unit#DegreeCelsius"))
            .build())
        .sample(SPINDLE_TEMPERATURE, 42.0f)
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Float, TOOL_WEAR)
            .label("Tool wear")
            .description("Estimated wear of the active tool in percent")
            .semanticType(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .measurementUnit(URI.create("http://qudt.org/vocab/unit#Percent"))
            .build())
        .sample(TOOL_WEAR, 20.0f)
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Float, COOLANT_FLOW)
            .label("Coolant flow")
            .description("Coolant flow in litres per minute")
            .semanticType(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample(COOLANT_FLOW, 20.0f)
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Float, VIBRATION)
            .label("Vibration")
            .description("Vibration velocity in millimetres per second")
            .semanticType(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample(VIBRATION, 2.0f)
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Integer, PARTS_COMPLETED)
            .label("Parts completed")
            .description("Number of parts finished since the simulation started")
            .semanticType(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample(PARTS_COMPLETED, 0)
        .build();
  }
}
