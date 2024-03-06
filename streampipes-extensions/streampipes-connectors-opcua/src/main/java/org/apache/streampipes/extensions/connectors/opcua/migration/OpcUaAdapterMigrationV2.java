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
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.vocabulary.XSD;

import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_HOST;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_HOST_OR_URL;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_SERVER_PORT;


public class OpcUaAdapterMigrationV2 implements IAdapterMigrator {
  @Override
  public ModelMigratorConfig config() {
    return new ModelMigratorConfig(
        "org.apache.streampipes.connect.iiot.adapters.opcua",
        SpServiceTagPrefix.ADAPTER,
        1,
        2
    );
  }

  @Override
  public MigrationResult<AdapterDescription> migrate(
      AdapterDescription element,
      IStaticPropertyExtractor extractor
  ) throws RuntimeException {

    var portStaticProperty = extractPortProperty(element);
    portStaticProperty.setRequiredDatatype(XSD.INTEGER);

    return MigrationResult.success(element);
  }

  /**
   * This method searches for the static property of the port definition
   */
  protected FreeTextStaticProperty extractPortProperty(AdapterDescription adapterDescription) {
    var staticPropertyAlternatives = getOpcHostOrUrl(adapterDescription);
    var staticPropertyGroup = getOpcHost(staticPropertyAlternatives);
    return getOpcServerPort(staticPropertyGroup);
  }

  private StaticPropertyAlternatives getOpcHostOrUrl(AdapterDescription adapterDescription) {
    return (StaticPropertyAlternatives) adapterDescription
        .getConfig()
        .stream()
        .filter(config -> config.getInternalName()
                                .equals(OPC_HOST_OR_URL.name()))
        .findFirst()
        .orElseThrow();
  }

  private StaticPropertyGroup getOpcHost(StaticPropertyAlternatives staticPropertyAlternatives) {
    return (StaticPropertyGroup) staticPropertyAlternatives
        .getAlternatives()
        .stream()
        .filter(alternative -> alternative.getInternalName()
                                          .equals(OPC_HOST.name()))
        .findFirst()
        .orElseThrow()
        .getStaticProperty();
  }

  private FreeTextStaticProperty getOpcServerPort(StaticPropertyGroup staticPropertyGroup) {
    return (FreeTextStaticProperty) staticPropertyGroup
        .getStaticProperties()
        .stream()
        .filter(property -> property.getInternalName()
                                    .equals(OPC_SERVER_PORT.name()))
        .findFirst()
        .orElseThrow();
  }
}
