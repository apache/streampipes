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
import org.apache.streampipes.connect.iiot.adapters.simulator.machine.event.DiagnosticSimulator;
import org.apache.streampipes.connect.iiot.adapters.simulator.machine.event.EventSimulator;
import org.apache.streampipes.connect.iiot.adapters.simulator.machine.event.FlowSimulator;
import org.apache.streampipes.connect.iiot.adapters.simulator.machine.event.PressureSimulator;
import org.apache.streampipes.connect.iiot.adapters.simulator.machine.event.SensorValueSimulator;
import org.apache.streampipes.connect.iiot.adapters.simulator.machine.event.WaterlevelSimulator;

public class MachineDataSimulatorUtils {

  // Vocabulary
  public static final String NS = "https://streampipes.org/vocabulary/examples/watertank/v1/";
  public static final String HAS_SENSOR_ID = NS + "hasSensorId";

  public static final String TIMESTAMP = "timestamp";
  public static final String SENSOR_ID = "sensorId";
  public static final String MASS_FLOW = "mass_flow";
  public static final String TEMPERATURE = "temperature";

  public static EventSimulator getSimulator(String selectedSimulatorOption) throws AdapterException {
    return switch (selectedSimulatorOption) {
      case "flowrate" -> new FlowSimulator(new SensorValueSimulator());
      case "pressure" -> new PressureSimulator(new SensorValueSimulator());
      case "waterlevel" -> new WaterlevelSimulator(new SensorValueSimulator());
      case "diagnostics" -> new DiagnosticSimulator(new SensorValueSimulator());
      default -> throw new AdapterException("resource not found");
    };
  }
}
