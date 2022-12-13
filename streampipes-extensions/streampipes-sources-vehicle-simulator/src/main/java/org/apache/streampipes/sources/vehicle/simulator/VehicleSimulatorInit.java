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

package org.apache.streampipes.sources.vehicle.simulator;

import org.apache.streampipes.container.model.SpServiceDefinition;
import org.apache.streampipes.container.model.SpServiceDefinitionBuilder;
import org.apache.streampipes.container.standalone.init.StandaloneModelSubmitter;
import org.apache.streampipes.sources.vehicle.simulator.config.ConfigKeys;
import org.apache.streampipes.sources.vehicle.simulator.vehicle.streams.VehicleStream;

public class VehicleSimulatorInit extends StandaloneModelSubmitter {

  public static void main(String[] args) {
    new VehicleSimulatorInit().init();
  }

  @Override
  public SpServiceDefinition provideServiceDefinition() {
    return SpServiceDefinitionBuilder.create("org-apache-streampipes-sources-simulator-vehicle",
            "Vehicle Simulator",
            "",
            8090)
        .registerPipelineElement(new VehicleStream())
        .addConfig(ConfigKeys.KAFKA_HOST, "kafka", "Host for kafka of the vehicle simulator")
        .addConfig(ConfigKeys.KAFKA_PORT, 9092, "Port for kafka of the vehicle simulator")
        .build();
  }
}
