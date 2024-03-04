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
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.vocabulary.XSD;

import static org.apache.streampipes.extensions.connectors.plc.adapter.modbus.Plc4xModbusAdapter.PLC_PORT;

public class Plc4xModbusAdapterMigrationV1 implements IAdapterMigrator {
  @Override
  public ModelMigratorConfig config() {
    return new ModelMigratorConfig(
      "org.apache.streampipes.connect.iiot.adapters.plc4x.modbus",
      SpServiceTagPrefix.ADAPTER,
      0,
      1
    );
  }

  @Override
  public MigrationResult<AdapterDescription> migrate(AdapterDescription element,
                                                     IStaticPropertyExtractor extractor) throws RuntimeException {

    var portProperty = extractPortProperty(element);
    portProperty.setRequiredDatatype(XSD.INTEGER);

    return MigrationResult.success(element);
  }

  protected FreeTextStaticProperty extractPortProperty(AdapterDescription adapterDescription) {
    return (FreeTextStaticProperty) adapterDescription.getConfig().stream()
        .filter(this::isPortConfig)
        .findFirst()
        .orElseThrow();
  }

  private boolean isPortConfig(StaticProperty config) {
    return config.getInternalName().equals(PLC_PORT);
  }
}
