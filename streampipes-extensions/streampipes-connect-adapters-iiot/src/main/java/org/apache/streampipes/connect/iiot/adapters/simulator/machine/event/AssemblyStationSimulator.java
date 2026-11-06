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
 * Simulates screwing stations on an assembly line. Every station works on one part per
 * cycle: the part is loaded, the joint is tightened and the result (torque, angle, OK/NOK)
 * stays on the station's tags until the next part is finished, as a PLC would report it.
 * A small share of joints is NOK and reworked. In the alarm phase a bad batch of fasteners
 * pulls the torque down, NOK joints pile up and the station blocks the line.
 */
public class AssemblyStationSimulator extends StatefulMachineSimulator<AssemblyStationSimulator.State> {

  public static final String STATION_STATE = "station_state";
  public static final String PART_ID = "part_id";
  public static final String CYCLE_PROGRESS = "cycle_progress";
  public static final String TORQUE = "torque";
  public static final String TORQUE_ANGLE = "torque_angle";
  public static final String CYCLE_TIME = "cycle_time";
  public static final String SCREW_OK = "screw_ok";
  public static final String PARTS_OK = "parts_ok";
  public static final String PARTS_NOK = "parts_nok";

  public static final String STATE_LOADING = "LOADING";
  public static final String STATE_RUNNING = "RUNNING";
  public static final String STATE_BLOCKED = "BLOCKED";

  static final double TORQUE_TARGET = 24.0;
  static final double TORQUE_MIN = 21.0;
  static final double TORQUE_MAX = 27.0;
  private static final double LOADING_SECONDS = 5.0;
  private static final int BLOCK_AFTER_CONSECUTIVE_NOK = 3;

  public static class State extends MachineState {
    String partId;
    double cycleDuration = 45;
    double cycleElapsed = 0;
    double lastCycleTime = 45;
    double torque = TORQUE_TARGET;
    double angle = 31;
    boolean screwOk = true;
    int partsOk = 0;
    int partsNok = 0;
    int consecutiveNok = 0;
    boolean blocked = false;
  }

  public AssemblyStationSimulator() {
    this(System.nanoTime());
  }

  public AssemblyStationSimulator(long seed) {
    super(State::new, seed);
  }

  @Override
  protected Map<String, Object> buildEvent(State s, boolean alarmPhase, int sensorIndex, double dt, long timestamp) {
    if (s.partId == null) {
      s.partId = newPartId();
      s.cycleDuration = gaussian(45, 2.5);
    }
    if (s.blocked && !alarmPhase) {
      // the operator has cleared the fault and restarted the station
      s.blocked = false;
      s.consecutiveNok = 0;
    }
    if (!s.blocked) {
      s.cycleElapsed += dt;
      if (s.cycleElapsed >= s.cycleDuration) {
        finishCycle(s, alarmPhase);
      }
    }

    String state = s.blocked ? STATE_BLOCKED : (s.cycleElapsed < LOADING_SECONDS ? STATE_LOADING : STATE_RUNNING);
    double progress = s.blocked ? 100 : clamp(100 * s.cycleElapsed / s.cycleDuration, 0, 100);

    Map<String, Object> event = new HashMap<>();
    event.put(TIMESTAMP, timestamp);
    event.put(SENSOR_ID, String.format("station%02d", sensorIndex));
    event.put(STATION_STATE, state);
    event.put(PART_ID, s.partId);
    event.put(CYCLE_PROGRESS, round(progress));
    event.put(TORQUE, round(s.torque));
    event.put(TORQUE_ANGLE, round(s.angle));
    event.put(CYCLE_TIME, round(s.lastCycleTime));
    event.put(SCREW_OK, s.screwOk);
    event.put(PARTS_OK, s.partsOk);
    event.put(PARTS_NOK, s.partsNok);
    return event;
  }

  private void finishCycle(State s, boolean alarmPhase) {
    boolean faultyFastener = chance(alarmPhase ? 0.6 : 0.03);
    if (faultyFastener) {
      s.torque = uniform(14, 20);
      s.angle = uniform(42, 60);
    } else {
      s.torque = gaussian(alarmPhase ? 22.5 : TORQUE_TARGET, 0.7);
      s.angle = gaussian(31, 1.8);
    }
    s.screwOk = s.torque >= TORQUE_MIN && s.torque <= TORQUE_MAX;
    s.lastCycleTime = s.cycleElapsed;
    s.cycleElapsed = 0;
    if (s.screwOk) {
      s.partsOk++;
      s.consecutiveNok = 0;
      s.partId = newPartId();
      s.cycleDuration = gaussian(45, 2.5);
    } else {
      s.partsNok++;
      s.consecutiveNok++;
      // the same part is reworked in a shorter cycle
      s.cycleDuration = gaussian(30, 2);
      if (s.consecutiveNok >= BLOCK_AFTER_CONSECUTIVE_NOK) {
        s.blocked = true;
      }
    }
  }

  private String newPartId() {
    return String.format("GB-%06d", 100000 + random.nextInt(900000));
  }

  @Override
  public SampleData getSampleData() {
    return SampleDataBuilder.create()
        .sample(new AssemblyStationSimulator(42).buildEvent(0, 1, System.currentTimeMillis()))
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
        .sample(SENSOR_ID, "station01")
        .property(PrimitivePropertyBuilder
            .create(Datatypes.String, STATION_STATE)
            .label("Station state")
            .description("LOADING, RUNNING or BLOCKED")
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample(STATION_STATE, "RUNNING")
        .property(PrimitivePropertyBuilder
            .create(Datatypes.String, PART_ID)
            .label("Part ID")
            .description("Serial number of the part on the station")
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample(PART_ID, "GB-123456")
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Float, CYCLE_PROGRESS)
            .label("Cycle progress")
            .description("Progress of the current cycle in percent")
            .semanticType(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .measurementUnit(URI.create("http://qudt.org/vocab/unit#Percent"))
            .build())
        .sample(CYCLE_PROGRESS, 40.0f)
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Float, TORQUE)
            .label("Torque")
            .description("Final tightening torque of the last joint in newton metres")
            .semanticType(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .measurementUnit(URI.create("http://qudt.org/vocab/unit#NewtonMeter"))
            .build())
        .sample(TORQUE, 24.0f)
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Float, TORQUE_ANGLE)
            .label("Torque angle")
            .description("Rotation angle after the snug torque of the last joint in degrees")
            .semanticType(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .measurementUnit(URI.create("http://qudt.org/vocab/unit#DegreeAngle"))
            .build())
        .sample(TORQUE_ANGLE, 31.0f)
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Float, CYCLE_TIME)
            .label("Cycle time")
            .description("Duration of the last completed cycle in seconds")
            .semanticType(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .measurementUnit(URI.create("http://qudt.org/vocab/unit#SecondTime"))
            .build())
        .sample(CYCLE_TIME, 45.0f)
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Boolean, SCREW_OK)
            .label("Screw OK")
            .description("Whether the last joint was tightened within specification")
            .semanticType(SO.BOOLEAN)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample(SCREW_OK, true)
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Integer, PARTS_OK)
            .label("Parts OK")
            .description("Number of parts assembled within specification")
            .semanticType(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample(PARTS_OK, 0)
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Integer, PARTS_NOK)
            .label("Parts NOK")
            .description("Number of joints outside the specification")
            .semanticType(SO.NUMBER)
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .sample(PARTS_NOK, 0)
        .build();
  }
}
