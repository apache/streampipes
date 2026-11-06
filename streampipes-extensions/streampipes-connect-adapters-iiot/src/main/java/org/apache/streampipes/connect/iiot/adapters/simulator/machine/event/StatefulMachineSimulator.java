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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Base for simulators that keep an individual state per simulated machine (sensor index)
 * and advance it by the time elapsed between two ticks, so that signals drift, cycle and
 * correlate instead of being drawn independently for every event.
 *
 * @param <T> the per-machine state type
 */
public abstract class StatefulMachineSimulator<T extends StatefulMachineSimulator.MachineState>
    implements EventSimulator {

  /**
   * Common per-machine bookkeeping: the timestamp of the last tick.
   */
  public static class MachineState {
    long lastTimestamp = -1;
  }

  private static final double MAX_STEP_SECONDS = 5.0;

  private final Map<Integer, T> states = new HashMap<>();
  private final Supplier<T> stateFactory;
  protected final Random random;
  private int lastPhase = 0;
  private int incidents = 0;
  private int maxSensorIndex = 1;

  protected StatefulMachineSimulator(Supplier<T> stateFactory, long seed) {
    this.stateFactory = stateFactory;
    this.random = new Random(seed);
  }

  @Override
  public Map<String, Object> buildEvent(int simulationPhase, int sensorIndex, long timestamp) {
    maxSensorIndex = Math.max(maxSensorIndex, sensorIndex);
    if (simulationPhase == 1 && lastPhase == 0) {
      incidents++;
    }
    lastPhase = simulationPhase;
    T state = states.computeIfAbsent(sensorIndex, index -> stateFactory.get());
    double dt = elapsedSeconds(state, timestamp);
    return buildEvent(state, simulationPhase == 1, sensorIndex, dt, timestamp);
  }

  /**
   * Advances the given machine by dt seconds and returns its event.
   *
   * @param state       the machine's state
   * @param alarmPhase  whether the simulation is in its alarm phase
   * @param sensorIndex the machine index (1-based)
   * @param dt          seconds since the machine's previous tick (0 on the first tick)
   * @param timestamp   the event timestamp
   */
  protected abstract Map<String, Object> buildEvent(T state, boolean alarmPhase, int sensorIndex, double dt,
                                                    long timestamp);

  /**
   * Returns whether an alarm-phase incident targets the given machine. Incidents rotate
   * through the machines, so with several machines only one of them is hit per alarm phase.
   */
  protected boolean incidentTargets(int sensorIndex) {
    return incidents > 0 && ((incidents - 1) % maxSensorIndex) + 1 == sensorIndex;
  }

  private double elapsedSeconds(T state, long timestamp) {
    double dt = state.lastTimestamp < 0 ? 0 : (timestamp - state.lastTimestamp) / 1000.0;
    state.lastTimestamp = timestamp;
    return Math.max(0, Math.min(dt, MAX_STEP_SECONDS));
  }

  protected double gaussian(double mean, double standardDeviation) {
    return mean + random.nextGaussian() * standardDeviation;
  }

  protected double uniform(double min, double max) {
    return min + random.nextDouble() * (max - min);
  }

  protected boolean chance(double probability) {
    return random.nextDouble() < probability;
  }

  /**
   * Moves value towards target with a first-order lag (time constant tau in seconds).
   */
  protected static double approach(double value, double target, double dt, double tau) {
    return value + (target - value) * (1 - Math.exp(-dt / tau));
  }

  protected static double clamp(double value, double min, double max) {
    return Math.max(min, Math.min(max, value));
  }

  protected static double round(double value) {
    return Math.round(value * 100.0) / 100.0;
  }
}
