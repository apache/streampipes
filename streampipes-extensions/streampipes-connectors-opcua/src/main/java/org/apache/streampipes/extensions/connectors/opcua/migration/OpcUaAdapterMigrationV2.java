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
import org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaUtil;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Labels;

import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.HOST_PORT;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_HOST;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_HOST_OR_URL;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_SERVER_HOST;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_SERVER_PORT;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_SERVER_URL;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.OPC_URL;




public class OpcUaAdapterMigrationV2 implements IAdapterMigrator {
  @Override
  public ModelMigratorConfig config() {
    return new ModelMigratorConfig(
      "org.apache.streampipes.connect.iiot.adapters.opcua",
      SpServiceTagPrefix.ADAPTER,
      1,
      2);
  }

  @Override
  public MigrationResult<AdapterDescription> migrate(AdapterDescription element,
                                                     IStaticPropertyExtractor extractor) throws RuntimeException {
    var newConfigs = element.getConfig().stream().map(config->{
      if (isHostOrUrlConfig(config)){
        var alternatives = modifiedAlternatives(extractor);
        alternatives.setIndex(config.getIndex());
        return alternatives;
      } else {
        return config;
      }
    }).toList();

    element.setConfig(newConfigs);
    return MigrationResult.success(element);
  }

  private StaticProperty modifiedAlternatives(IStaticPropertyExtractor extractor) {

    var serverAddressValue =
        extractor.singleValueParameter(OPC_SERVER_URL.name(), String.class);
    var hostValue = OpcUaUtil.addOpcPrefixIfNotExists(
        extractor.singleValueParameter(OPC_SERVER_HOST.name(), String.class)
    );
    var portValue = extractor.singleValueParameter(OPC_SERVER_PORT.name(), int.class);

    return StaticProperties.alternatives(Labels.withId(OPC_HOST_OR_URL),
      Alternatives.from(
        Labels.withId(OPC_URL),
        StaticProperties.stringFreeTextProperty(
          Labels.withId(OPC_SERVER_URL),
          serverAddressValue))
      ,
      Alternatives.from(Labels.withId(OPC_HOST),
        StaticProperties.group(
          Labels.withId(HOST_PORT),
          StaticProperties.stringFreeTextProperty(
            Labels.withId(OPC_SERVER_HOST), hostValue),
          StaticProperties.integerFreeTextProperty(
            Labels.withId(OPC_SERVER_PORT), portValue)
        )));
  }

  private boolean isHostOrUrlConfig(StaticProperty config){
    return config.getInternalName().equals(OPC_HOST_OR_URL.name());
  }
}
