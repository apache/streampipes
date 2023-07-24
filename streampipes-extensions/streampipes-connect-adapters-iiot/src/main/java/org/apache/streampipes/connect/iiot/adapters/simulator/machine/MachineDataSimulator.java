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
import org.apache.streampipes.extensions.api.connect.IEventCollector;

import java.util.HashMap;
import java.util.Map;

public class MachineDataSimulator implements Runnable {

  private IEventCollector collector;

  private final Integer waitTimeMs;
  private final String selectedSimulatorOption;

  private Boolean running;

  public MachineDataSimulator(IEventCollector collector,
                              Integer waitTimeMs,
                              String selectedSimulatorOption) {
    this.collector = collector;
    this.waitTimeMs = waitTimeMs;
    this.selectedSimulatorOption = selectedSimulatorOption;
    this.running = true;
  }

  @Override
  public void run() {
    this.running = true;
    Map<String, Object> event = new HashMap<>();
    long startTimeMs = System.currentTimeMillis();

    while (running) {
      long currentTimeMs = System.currentTimeMillis();
      long timeDeltaMs = currentTimeMs - startTimeMs;

      switch (this.selectedSimulatorOption) {
        case "flowrate":
          // 0 - 30s
          if (timeDeltaMs > 0 && timeDeltaMs <= 30000) {
            event = buildFlowrateEvent(0);
          } else if (timeDeltaMs > 30000 && timeDeltaMs <= 60000) {
            // 30s - 60s
            event = buildFlowrateEvent(1);
          } else {
            // > 60s
            // reset start time to start over again
            startTimeMs = currentTimeMs;
          }
          break;
        case "pressure":
          // 0 - 30s
          if (timeDeltaMs > 0 && timeDeltaMs <= 30000) {
            event = buildPressureEvent(0);
          } else if (timeDeltaMs > 30000 && timeDeltaMs <= 60000) {
            // 30s - 60s
            event = buildPressureEvent(1);
          } else {
            // > 60s
            // reset start time to start over again
            startTimeMs = currentTimeMs;
          }
          break;
        case "waterlevel":
          if (timeDeltaMs > 0 && timeDeltaMs <= 30000) {
            // 0 - 30s
            event = buildWaterlevelEvent(0);
          } else if (timeDeltaMs > 30000 && timeDeltaMs <= 60000) {
            // 30s - 60s
            event = buildWaterlevelEvent(1);
          } else {
            // > 60s
            // reset start time to start over again
            startTimeMs = currentTimeMs;
          }
          break;
        default:
          try {
            throw new AdapterException("resource not found");
          } catch (AdapterException e) {
            e.printStackTrace();
          }
      }

      if (event.keySet().size() > 0) {
        collector.collect(event);
      }

      try {
        Thread.sleep(waitTimeMs);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private Map<String, Object> buildFlowrateEvent(int simulationPhase) {
    Map<String, Object> event = new HashMap<>();

    event.put("timestamp", System.currentTimeMillis());
    event.put("sensorId", "flowrate02");
    event.put("mass_flow", randomDoubleBetween(0, 10));
    event.put("volume_flow", randomDoubleBetween(0, 10));
    event.put("temperature", simulationPhase == 0 ? randomDoubleBetween(40, 50) : randomDoubleBetween(80, 100));
    event.put("density", randomDoubleBetween(40, 50));
    event.put("sensor_fault_flags", simulationPhase != 0);

    return event;
  }

  private Map<String, Object> buildPressureEvent(int simulationPhase) {
    Map<String, Object> event = new HashMap<>();

    event.put("timestamp", System.currentTimeMillis());
    event.put("sensorId", "pressure01");
    event.put("pressure", simulationPhase == 0 ? randomDoubleBetween(10, 40) : randomDoubleBetween(40, 70));

    return event;
  }

  private Map<String, Object> buildWaterlevelEvent(int simulationPhase) {
    Map<String, Object> event = new HashMap<>();

    event.put("timestamp", System.currentTimeMillis());
    event.put("sensorId", "level01");
    event.put("level", simulationPhase == 0 ? randomDoubleBetween(20, 30) : randomDoubleBetween(60, 80));
    event.put("overflow", simulationPhase != 0);

    return event;
  }

  private double randomDoubleBetween(int min, int max) {
    return Math.random() * (max - min + 1) + min;
  }

  public void setRunning(Boolean running) {
    this.running = running;
  }
}
