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
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Labels;

import java.util.List;

import static org.apache.streampipes.extensions.connectors.opcua.config.SharedUserConfiguration.X509_GROUP;
import static org.apache.streampipes.extensions.connectors.opcua.config.SharedUserConfiguration.X509_PRIVATE_KEY_PEM;
import static org.apache.streampipes.extensions.connectors.opcua.config.SharedUserConfiguration.X509_PUBLIC_KEY_PEM;

public class OpcUaAdapterMigrationV6 implements IAdapterMigrator {
  @Override
  public ModelMigratorConfig config() {
    return new ModelMigratorConfig(
        OpcUaAdapter.ID,
        SpServiceTagPrefix.ADAPTER,
        5,
        6
    );
  }

  @Override
  public MigrationResult<AdapterDescription> migrate(AdapterDescription element,
                                                     IStaticPropertyExtractor extractor) throws RuntimeException {
    var config = element.getConfig();
    element.setConfig(migrate(config, 3));

    return MigrationResult.success(element);
  }

  public List<StaticProperty> migrate(List<StaticProperty> staticProperties,
                                      int authenticationConfigIndex) {
    var authentication = staticProperties.get(authenticationConfigIndex);

    if (authentication instanceof StaticPropertyAlternatives) {
      var group = StaticProperties.group(
          Labels.withId(X509_GROUP),
          StaticProperties.secretValue(Labels.withId(X509_PRIVATE_KEY_PEM)),
          StaticProperties.stringFreeTextProperty(Labels.withId(X509_PUBLIC_KEY_PEM), true, false));
      group.setHorizontalRendering(false);
      var x509Alternative = Alternatives.from(Labels.withId(X509_GROUP), group);
      ((StaticPropertyAlternatives) authentication).getAlternatives().add(x509Alternative);
    }
    return staticProperties;
  }
}
