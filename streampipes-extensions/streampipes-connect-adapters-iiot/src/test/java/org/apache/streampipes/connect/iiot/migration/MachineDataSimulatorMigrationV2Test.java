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

package org.apache.streampipes.connect.iiot.migration;

import org.apache.streampipes.connect.iiot.adapters.simulator.machine.MachineDataSimulatorAdapter;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.staticproperty.OneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.helpers.Labels;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MachineDataSimulatorMigrationV2Test {

  @Test
  void addsManufacturingOptionsOnce() {
    var selection = new OneOfStaticProperty(MachineDataSimulatorAdapter.SELECTED_SIMULATOR_OPTION, "", "");
    selection.setOptions(new ArrayList<>(List.of(
        new Option("flowrate"), new Option("pressure"), new Option("waterlevel"), new Option("diagnostics"))));
    var adapter = new AdapterDescription();
    adapter.setConfig(new ArrayList<>(List.of(selection)));
    var migration = new MachineDataSimulatorMigrationV2();

    var result = migration.migrate(adapter, null);
    migration.migrate(result.element(), null);

    var options = optionNames(result.element().getConfig().get(0));
    assertEquals(List.of("flowrate", "pressure", "waterlevel", "diagnostics", "cnc", "assembly", "quality"), options);
    assertTrue(result.success());
    assertEquals(1, migration.config().fromVersion());
    assertEquals(2, migration.config().toVersion());
  }

  @Test
  void keepsOtherPropertiesUntouched() {
    var waitTime = StaticProperties.integerFreeTextProperty(Labels.withId("wait-time-ms"), 1000);
    var adapter = new AdapterDescription();
    adapter.setConfig(new ArrayList<>(List.of(waitTime)));

    var result = new MachineDataSimulatorMigrationV2().migrate(adapter, null);

    assertEquals(1, result.element().getConfig().size());
    assertTrue(result.success());
  }

  private static List<String> optionNames(StaticProperty property) {
    return ((OneOfStaticProperty) property).getOptions().stream().map(Option::getName).toList();
  }
}
