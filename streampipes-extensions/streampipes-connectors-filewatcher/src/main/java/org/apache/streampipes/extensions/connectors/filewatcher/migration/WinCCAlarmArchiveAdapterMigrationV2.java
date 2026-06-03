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

package org.apache.streampipes.extensions.connectors.filewatcher.migration;

import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.migration.IAdapterMigrator;
import org.apache.streampipes.extensions.connectors.filewatcher.adapter.WinCCAlarmArchiveAdapter;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.model.staticproperty.SlideToggleStaticProperty;
import org.apache.streampipes.sdk.helpers.Labels;

public class WinCCAlarmArchiveAdapterMigrationV2 implements IAdapterMigrator {

  @Override
  public ModelMigratorConfig config() {
    return new ModelMigratorConfig(
        WinCCAlarmArchiveAdapter.ID,
        SpServiceTagPrefix.ADAPTER,
        1,
        2
    );
  }

  @Override
  public MigrationResult<AdapterDescription> migrate(AdapterDescription element,
                                                     IStaticPropertyExtractor extractor) throws RuntimeException {
    var label = Labels.withId(WinCCAlarmArchiveAdapter.CONSIDER_LAST_MODIFIED);
    var staticProperty = new SlideToggleStaticProperty(
        label.getInternalId(),
        label.getLabel(),
        label.getDescription(),
        true
    );
    staticProperty.setSelected(true);
    element.getConfig().add(5, staticProperty);

    return MigrationResult.success(element);
  }
}
