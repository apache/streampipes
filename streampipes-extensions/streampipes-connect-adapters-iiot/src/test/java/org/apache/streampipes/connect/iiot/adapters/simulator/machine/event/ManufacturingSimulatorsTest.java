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

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.connect.iiot.adapters.simulator.machine.MachineDataSimulatorUtils;
import org.apache.streampipes.model.schema.EventProperty;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManufacturingSimulatorsTest {

  private static final List<String> NEW_OPTIONS = List.of("cnc", "assembly", "quality");
  private static final long SEED = 4711L;

  /**
   * Runs the simulator with one-second ticks and returns the events of one machine.
   */
  private static List<Map<String, Object>> run(EventSimulator simulator, int phase, int sensors, int seconds,
                                               int sensorIndex, long startMs) {
    List<Map<String, Object>> events = new ArrayList<>();
    for (int second = 0; second < seconds; second++) {
      for (int sensor = 1; sensor <= sensors; sensor++) {
        var event = simulator.buildEvent(phase, sensor, startMs + second * 1000L);
        if (sensor == sensorIndex) {
          events.add(event);
        }
      }
    }
    return events;
  }

  private static double number(Map<String, Object> event, String field) {
    return ((Number) event.get(field)).doubleValue();
  }

  private static boolean hasState(Map<String, Object> event, String field, String state) {
    return state.equals(event.get(field));
  }

  private static boolean anyWithState(List<Map<String, Object>> events, String field, String state) {
    return events.stream().anyMatch(event -> hasState(event, field, state));
  }

  @Test
  void newScenariosAreResolvable() throws AdapterException {
    for (String option : NEW_OPTIONS) {
      assertNotNull(MachineDataSimulatorUtils.getSimulator(option));
    }
  }

  @Test
  void unknownScenarioIsRejected() {
    assertThrows(AdapterException.class, () -> MachineDataSimulatorUtils.getSimulator("unknown"));
  }

  @Test
  void cncMachineRunsCyclesAndWearsItsTool() {
    var events = run(new CncMachineSimulator(SEED), 0, 1, 600, 1, 0);

    Set<Object> states = new HashSet<>();
    int toolChanges = 0;
    double previousWear = -1;
    double previousTemperature = number(events.get(0), CncMachineSimulator.SPINDLE_TEMPERATURE);
    for (var event : events) {
      states.add(event.get(CncMachineSimulator.MACHINE_STATE));
      double wear = number(event, CncMachineSimulator.TOOL_WEAR);
      if (wear < previousWear - 1) {
        toolChanges++;
        assertTrue(previousWear >= CncMachineSimulator.TOOL_CHANGE_THRESHOLD, "tool changed before it was worn");
      }
      previousWear = wear;
      double temperature = number(event, CncMachineSimulator.SPINDLE_TEMPERATURE);
      assertTrue(Math.abs(temperature - previousTemperature) < 1.5, "temperature jumped by more than 1.5 degrees");
      previousTemperature = temperature;
      assertEquals("cnc01", event.get("sensorId"));
      assertEquals("GEARBOX-HOUSING-M3", event.get(CncMachineSimulator.PROGRAM_ID));
      if (CncMachineSimulator.STATE_RUNNING.equals(event.get(CncMachineSimulator.MACHINE_STATE))) {
        assertTrue(number(event, CncMachineSimulator.SPINDLE_LOAD) > 20, "cutting without load");
        assertTrue(number(event, CncMachineSimulator.COOLANT_FLOW) > 15, "cutting without coolant");
      } else {
        assertEquals(0.0, number(event, CncMachineSimulator.FEED_RATE), "feed while not cutting");
      }
    }
    assertTrue(states.containsAll(List.of(CncMachineSimulator.STATE_LOADING, CncMachineSimulator.STATE_RUNNING,
        CncMachineSimulator.STATE_TOOL_CHANGE)), "states seen: " + states);
    assertTrue(toolChanges >= 1, "no tool change in ten minutes");
    assertTrue(number(events.get(events.size() - 1), CncMachineSimulator.PARTS_COMPLETED) >= 5);
    assertFalse(states.contains(CncMachineSimulator.STATE_ALARM));
  }

  @Test
  void cncToolBreakHitsOneMachinePerAlarmPhase() {
    var simulator = new CncMachineSimulator(SEED);
    run(simulator, 0, 2, 100, 1, 0);
    var firstAlarm = run(simulator, 1, 2, 30, 1, 100_000);
    var firstAlarmOther = run(simulator, 1, 2, 1, 2, 130_000);

    assertTrue(anyWithState(firstAlarm, CncMachineSimulator.MACHINE_STATE, CncMachineSimulator.STATE_ALARM),
        "machine 1 did not raise an alarm");
    assertTrue(firstAlarm.stream().anyMatch(e -> number(e, CncMachineSimulator.VIBRATION) > 8), "no vibration spike");
    assertTrue(firstAlarm.stream()
        .filter(e -> hasState(e, CncMachineSimulator.MACHINE_STATE, CncMachineSimulator.STATE_ALARM))
        .allMatch(e -> number(e, CncMachineSimulator.FEED_RATE) == 0), "feed not held during alarm");
    assertNotEquals(CncMachineSimulator.STATE_ALARM, firstAlarmOther.get(0).get(CncMachineSimulator.MACHINE_STATE));

    run(simulator, 0, 2, 100, 1, 131_000);
    var secondAlarm = run(simulator, 1, 2, 30, 2, 231_000);
    assertTrue(anyWithState(secondAlarm, CncMachineSimulator.MACHINE_STATE, CncMachineSimulator.STATE_ALARM),
        "second incident did not rotate to machine 2");
  }

  @Test
  void assemblyStationWorksOnOnePartPerCycle() {
    var events = run(new AssemblyStationSimulator(SEED), 0, 1, 600, 1, 0);

    Set<Object> partIds = new HashSet<>();
    int partChanges = 0;
    Object previousPart = null;
    for (var event : events) {
      Object part = event.get(AssemblyStationSimulator.PART_ID);
      assertTrue(part.toString().matches("GB-\\d{6}"));
      if (previousPart != null && !part.equals(previousPart)) {
        partChanges++;
      }
      previousPart = part;
      partIds.add(part);
      double torque = number(event, AssemblyStationSimulator.TORQUE);
      assertTrue(torque >= 10 && torque <= 30, "torque " + torque);
      boolean withinSpec = torque >= AssemblyStationSimulator.TORQUE_MIN
          && torque <= AssemblyStationSimulator.TORQUE_MAX;
      assertEquals(withinSpec, event.get(AssemblyStationSimulator.SCREW_OK));
      assertNotEquals(AssemblyStationSimulator.STATE_BLOCKED, event.get(AssemblyStationSimulator.STATION_STATE));
    }
    var last = events.get(events.size() - 1);
    int completed = (int) number(last, AssemblyStationSimulator.PARTS_OK)
        + (int) number(last, AssemblyStationSimulator.PARTS_NOK);
    assertTrue(partChanges >= 8 && partChanges <= 16, "parts changed " + partChanges + " times in ten minutes");
    assertTrue(completed >= partChanges, "counters do not cover the completed cycles");
    double cycleTime = number(last, AssemblyStationSimulator.CYCLE_TIME);
    assertTrue(cycleTime > 25 && cycleTime < 60, "cycle time " + cycleTime);
  }

  @Test
  void badFastenerBatchBlocksTheAssemblyStation() {
    var simulator = new AssemblyStationSimulator(SEED);
    run(simulator, 0, 1, 60, 1, 0);
    var alarm = run(simulator, 1, 1, 400, 1, 60_000);
    var recovered = run(simulator, 0, 1, 2, 1, 460_000);

    assertTrue(anyWithState(alarm, AssemblyStationSimulator.STATION_STATE, AssemblyStationSimulator.STATE_BLOCKED),
        "station never blocked during the bad batch");
    assertTrue(number(alarm.get(alarm.size() - 1), AssemblyStationSimulator.PARTS_NOK) >= 3);
    assertFalse(hasState(recovered.get(1), AssemblyStationSimulator.STATION_STATE,
        AssemblyStationSimulator.STATE_BLOCKED));
  }

  @Test
  void leakTestRigPressurisesAndMostlyPasses() {
    var events = run(new QualityTestSimulator(SEED), 0, 1, 900, 1, 0);

    Set<Object> states = new HashSet<>();
    double maxPressure = 0;
    for (var event : events) {
      states.add(event.get(QualityTestSimulator.TEST_STATE));
      maxPressure = Math.max(maxPressure, number(event, QualityTestSimulator.TEST_PRESSURE));
      assertEquals(QualityTestSimulator.RESULT_PASS.equals(event.get(QualityTestSimulator.TEST_RESULT)),
          event.get(QualityTestSimulator.PASSED));
    }
    var last = events.get(events.size() - 1);
    double tested = number(last, QualityTestSimulator.PARTS_TESTED);
    double failed = number(last, QualityTestSimulator.PARTS_FAILED);
    assertTrue(states.containsAll(List.of(QualityTestSimulator.STATE_LOADING, QualityTestSimulator.STATE_PRESSURIZING,
        QualityTestSimulator.STATE_TESTING)), "states seen: " + states);
    assertTrue(maxPressure > 2.8 && maxPressure < 3.3, "max pressure " + maxPressure);
    assertTrue(tested >= 15, "only " + tested + " parts tested in 15 minutes");
    assertTrue(failed / tested < 0.2, "failure rate too high: " + failed / tested);
  }

  @Test
  void badBatchRaisesTheFailureRate() {
    var simulator = new QualityTestSimulator(SEED);
    var events = run(simulator, 1, 1, 2000, 1, 0);
    var last = events.get(events.size() - 1);
    double tested = number(last, QualityTestSimulator.PARTS_TESTED);
    double failed = number(last, QualityTestSimulator.PARTS_FAILED);
    assertTrue(tested >= 30);
    assertTrue(failed / tested > 0.2, "failure rate during bad batch: " + failed / tested);
  }

  @Test
  void schemaAndSampleDataDescribeTheSameFields() throws AdapterException {
    for (String option : NEW_OPTIONS) {
      var simulator = MachineDataSimulatorUtils.getSimulator(option);

      var schemaFields = simulator.getSchema().getEventSchema().getEventProperties().stream()
          .map(EventProperty::getRuntimeName)
          .sorted()
          .toList();
      var sampleFields = simulator.getSampleData().getSamples().get(0).keySet().stream().sorted().toList();

      assertEquals(sampleFields, schemaFields, option);
      assertFalse(schemaFields.isEmpty(), option);
    }
  }
}
