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

import org.apache.streampipes.connect.iiot.adapters.simulator.machine.event.EventSimulator;
import org.apache.streampipes.extensions.api.connect.IEventCollector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class MachineDataSimulator implements Runnable {

  private final IEventCollector collector;

  private final Integer waitTimeMs;
  private final Integer numberOfSensors;
  private final EventSimulator simulator;

  private Boolean running;

  private static final Logger LOG = LoggerFactory.getLogger(MachineDataSimulator.class);

  public MachineDataSimulator(EventSimulator simulator,
                              IEventCollector collector,
                              Integer waitTimeMs,
                              int numberOfSensors) {
    this.simulator = simulator;
    this.collector = collector;
    this.waitTimeMs = waitTimeMs;
    this.running = true;
    this.numberOfSensors = numberOfSensors;
  }

  @Override
  public void run() {
    this.running = true;
    long startTimeMs = System.currentTimeMillis();

    while (running) {
      long currentTimeMs = System.currentTimeMillis();
      long timeDeltaMs = currentTimeMs - startTimeMs;

      Integer simulationPhase = null;

      if (timeDeltaMs > 0 && timeDeltaMs <= 30000) {
        simulationPhase = 0;
      } else if (timeDeltaMs > 30000 && timeDeltaMs <= 60000) {
        simulationPhase = 1;
      } else {
        startTimeMs = currentTimeMs; // reset
      }

      if (simulationPhase != null) {
        long timestamp = System.currentTimeMillis();

        for (int sensorIndex = 1; sensorIndex <= numberOfSensors; sensorIndex++) {
          var event = simulator.buildEvent(simulationPhase, sensorIndex, timestamp);

          if (!event.isEmpty()) {
            collector.collect(event);
          }
        }
      }

      try {
        TimeUnit.MILLISECONDS.sleep(waitTimeMs);
      } catch (InterruptedException e) {
        LOG.error("Machine simulator thread interrupted", e);
        Thread.currentThread().interrupt();
        return;
      }
    }
  }



  public void setRunning(Boolean running) {
    this.running = running;
  }
}
