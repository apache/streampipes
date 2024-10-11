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

package org.apache.streampipes.extensions.connectors.opcua.migration;

import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.migration.IAdapterMigrator;
import org.apache.streampipes.extensions.connectors.opcua.adapter.OpcUaAdapter;
import org.apache.streampipes.extensions.connectors.opcua.config.SharedUserConfiguration;
import org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.helpers.Labels;

public class OpcUaAdapterMigrationV3 implements IAdapterMigrator {
  @Override
  public ModelMigratorConfig config() {
    return new ModelMigratorConfig(
        OpcUaAdapter.ID,
        SpServiceTagPrefix.ADAPTER,
        2,
        3
    );
  }

  @Override
  public MigrationResult<AdapterDescription> migrate(AdapterDescription element, IStaticPropertyExtractor extractor) throws RuntimeException {
    var oneOfProperty = SharedUserConfiguration.getIncompleteEventConfig();
    oneOfProperty.getOptions().get(0).setSelected(true);
    element.getConfig().forEach(sp -> {
      if (sp.getInternalName().equalsIgnoreCase(OpcUaLabels.ADAPTER_TYPE.name())) {
        var alternatives = (StaticPropertyAlternatives) sp;
        var pullAlternative = alternatives.getAlternatives().get(0);
        var pullInterval = pullAlternative.getStaticProperty();
        var group = StaticProperties.group(
            Labels.withId(OpcUaAdapter.PULL_GROUP),
            false,
            pullInterval,
            oneOfProperty
        );
        group.setHorizontalRendering(false);
        pullAlternative.setStaticProperty(
            group
        );
      }
    });
    return MigrationResult.success(element);
  }
}
