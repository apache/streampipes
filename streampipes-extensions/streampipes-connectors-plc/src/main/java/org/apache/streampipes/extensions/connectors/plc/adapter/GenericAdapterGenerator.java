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

package org.apache.streampipes.extensions.connectors.plc.adapter;

import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.GenericPlc4xAdapter;

import org.apache.plc4x.java.api.PlcConnectionManager;
import org.apache.plc4x.java.api.PlcDriverManager;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GenericAdapterGenerator {

  private static final Logger LOG = LoggerFactory.getLogger(GenericAdapterGenerator.class);

  private final List<String> excludedDrivers = List.of(
      "simulated",
      "c-bus",
      "plc4x"
  );


  public List<StreamPipesAdapter> makeAvailableAdapters(PlcDriverManager driverManager,
                                                        PlcConnectionManager connectionManager) {
    var adapters = new ArrayList<StreamPipesAdapter>();
    var protocolCodes = getDrivers(driverManager);
    protocolCodes
        .stream()
        .filter(pc -> !excludedDrivers.contains(pc))
        .forEach(protocolCode -> {
          try {
            var driver = driverManager.getDriver(protocolCode);
            adapters.add(new GenericPlc4xAdapter(driver, connectionManager));
          } catch (PlcConnectionException e) {
            LOG.error("Could not generate PLC adapter for protocol {}", protocolCode);
          }
        });
    return adapters;
  }

  private Set<String> getDrivers(PlcDriverManager driverManager) {
    return driverManager
        .getProtocolCodes();
  }
}
