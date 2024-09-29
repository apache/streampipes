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

import static org.apache.streampipes.extensions.connectors.plc.adapter.s7.Plc4xS7Adapter.CODE_TEMPLATE;
import static org.apache.streampipes.extensions.connectors.plc.adapter.s7.Plc4xS7Adapter.PLC_CODE_BLOCK;
import static org.apache.streampipes.extensions.connectors.plc.adapter.s7.Plc4xS7Adapter.PLC_NODES;
import static org.apache.streampipes.extensions.connectors.plc.adapter.s7.Plc4xS7Adapter.PLC_NODE_INPUT_ALTERNATIVES;
import static org.apache.streampipes.extensions.connectors.plc.adapter.s7.Plc4xS7Adapter.PLC_NODE_INPUT_CODE_BLOCK_ALTIVE;
import static org.apache.streampipes.extensions.connectors.plc.adapter.s7.Plc4xS7Adapter.PLC_NODE_INPUT_COLLECTION_ALTERNATIVE;

import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.migration.IAdapterMigrator;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.model.staticproperty.CollectionStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.CodeLanguage;
import org.apache.streampipes.sdk.helpers.Labels;

import java.util.List;

public class Plc4xS7AdapterMigrationV1 implements IAdapterMigrator {
  @Override
  public ModelMigratorConfig config() {
    return new ModelMigratorConfig("org.apache.streampipes.connect.iiot.adapters.plc4x.s7", SpServiceTagPrefix.ADAPTER,
            0, 1);
  }

  @Override
  public MigrationResult<AdapterDescription> migrate(AdapterDescription element, IStaticPropertyExtractor extractor)
          throws RuntimeException {
    var newConfigs = new java.util.ArrayList<>(element.getConfig().stream().map(config -> {
      if (isCollectionConfig(config)) {
        return modifyCollection((CollectionStaticProperty) config);
      } else {
        return config;
      }
    }).toList());

    newConfigs.removeIf(c -> c.getInternalName().equals(PLC_NODES));
    element.setConfig(newConfigs);

    return MigrationResult.success(element);
  }

  private StaticProperty modifyCollection(CollectionStaticProperty collectionConfig) {

    var alternatives = List.of(
            Alternatives.from(Labels.withId(PLC_NODE_INPUT_COLLECTION_ALTERNATIVE), collectionConfig, true),
            Alternatives.from(Labels.withId(PLC_NODE_INPUT_CODE_BLOCK_ALTIVE), StaticProperties
                    .codeStaticProperty(Labels.withId(PLC_CODE_BLOCK), CodeLanguage.None, CODE_TEMPLATE)));

    return StaticProperties.alternatives(Labels.withId(PLC_NODE_INPUT_ALTERNATIVES), alternatives);
  }

  private boolean isCollectionConfig(StaticProperty config) {
    return config.getInternalName().equals(PLC_NODES);
  }
}
