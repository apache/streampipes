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
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;

public class OpcUaAdapterMigrationV1 implements IAdapterMigrator {

  private static final String OldNamespaceIndexKey = "NAMESPACE_INDEX";
  private static final String OldNodeId = "NODE_ID";

  @Override
  public ModelMigratorConfig config() {
    return new ModelMigratorConfig("org.apache.streampipes.connect.iiot.adapters.opcua", SpServiceTagPrefix.ADAPTER, 0,
            1);
  }

  @Override
  public MigrationResult<AdapterDescription> migrate(AdapterDescription element, IStaticPropertyExtractor extractor)
          throws RuntimeException {

    element.getConfig().removeIf(c -> c.getInternalName().equals(OldNamespaceIndexKey));
    element.getConfig().removeIf(c -> c.getInternalName().equals(OldNodeId));

    return MigrationResult.success(element);
  }
}
