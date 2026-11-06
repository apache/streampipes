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
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.migration.IAdapterMigrator;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.model.staticproperty.OneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.Option;

import java.util.List;

/**
 * Adds the manufacturing scenarios (cnc, assembly, quality) to the sensor selection of
 * existing machine data simulator adapters.
 */
public class MachineDataSimulatorMigrationV2 implements IAdapterMigrator {

  private static final List<String> NEW_OPTIONS = List.of("cnc", "assembly", "quality");

  @Override
  public ModelMigratorConfig config() {
    return new ModelMigratorConfig(
        MachineDataSimulatorAdapter.ID,
        SpServiceTagPrefix.ADAPTER,
        1,
        2
    );
  }

  @Override
  public MigrationResult<AdapterDescription> migrate(AdapterDescription element,
                                                     IStaticPropertyExtractor extractor) throws RuntimeException {
    element.getConfig().stream()
        .filter(OneOfStaticProperty.class::isInstance)
        .map(OneOfStaticProperty.class::cast)
        .filter(property -> MachineDataSimulatorAdapter.SELECTED_SIMULATOR_OPTION.equals(property.getInternalName()))
        .findFirst()
        .ifPresent(property -> NEW_OPTIONS.stream()
            .filter(name -> property.getOptions().stream().noneMatch(option -> name.equals(option.getName())))
            .forEach(name -> property.getOptions().add(new Option(name))));
    return MigrationResult.success(element);
  }
}
