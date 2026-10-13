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

package org.apache.streampipes.extensions.connectors.plc.adapter.migration;

import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.migration.IAdapterMigrator;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.config.AdapterConfigurationProvider;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.config.PlcTagConfiguration;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;

public class Plc4xAdapterMigrationV3 implements IAdapterMigrator {

  private final String protocol;

  public Plc4xAdapterMigrationV3(String protocol) {
    this.protocol = protocol;
  }

  @Override
  public ModelMigratorConfig config() {
    return new ModelMigratorConfig(AdapterConfigurationProvider.ID + protocol, SpServiceTagPrefix.ADAPTER, 2, 3);
  }

  @Override
  public MigrationResult<AdapterDescription> migrate(AdapterDescription element, IStaticPropertyExtractor extractor) {
    var migrated = new AdapterDescription(element);
    var properties = migrated.getConfig();
    for (int i = 0; i < properties.size(); i++) {
      if (Plc4xLabels.PLC_CODE_BLOCK.equals(properties.get(i).getInternalName())) {
        properties.set(i, PlcTagConfiguration.make(properties.get(i),
            properties.stream().anyMatch(property -> Plc4xLabels.PROTOCOL_METADATA.equals(property.getInternalName())),
            protocol));
        return MigrationResult.success(migrated);
      }
    }
    return MigrationResult.failure(element, "Cannot find the existing PLC tag configuration.");
  }
}
