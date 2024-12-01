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

import org.apache.streampipes.extensions.api.extractor.IParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.migration.IAdapterMigrator;
import org.apache.streampipes.extensions.connectors.opcua.adapter.OpcUaAdapter;
import org.apache.streampipes.extensions.connectors.opcua.utils.SecurityUtils;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.SecretStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Labels;

import java.util.List;

import static org.apache.streampipes.extensions.connectors.opcua.config.SharedUserConfiguration.SECURITY_MODE;
import static org.apache.streampipes.extensions.connectors.opcua.config.SharedUserConfiguration.SECURITY_POLICY;
import static org.apache.streampipes.extensions.connectors.opcua.config.SharedUserConfiguration.USER_AUTHENTICATION;
import static org.apache.streampipes.extensions.connectors.opcua.config.SharedUserConfiguration.USER_AUTHENTICATION_ANONYMOUS;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.PASSWORD;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.USERNAME;
import static org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaLabels.USERNAME_GROUP;

public class OpcUaAdapterMigrationV4 implements IAdapterMigrator {
  @Override
  public ModelMigratorConfig config() {
    return new ModelMigratorConfig(
        OpcUaAdapter.ID,
        SpServiceTagPrefix.ADAPTER,
        3,
        4
    );
  }

  @Override
  public MigrationResult<AdapterDescription> migrate(AdapterDescription element,
                                                     IStaticPropertyExtractor extractor) throws RuntimeException {
    var config = element.getConfig();
    element.setConfig(migrate(config, extractor));

    return MigrationResult.success(element);
  }

  public List<StaticProperty> migrate(List<StaticProperty> staticProperties,
                                      IParameterExtractor extractor) {
    var securityMode =
        StaticProperties.singleValueSelection(
            Labels.withId(SECURITY_MODE),
            SecurityUtils.getAvailableSecurityModes().stream().map(mode -> new Option(mode.k, mode.v)).toList()
        );
    securityMode.getOptions().get(0).setSelected(true);

    var securityPolicy = StaticProperties.singleValueSelection(
        Labels.withId(SECURITY_POLICY),
        SecurityUtils.getAvailableSecurityPolicies().stream().map(p -> new Option(p.name())).toList()
    );
    securityPolicy.getOptions().get(0).setSelected(true);

    boolean anonymous = true;
    var currentAuthSettings = extractor.selectedAlternativeInternalId(
        "ACCESS_MODE"
    );
    if (currentAuthSettings.equals("USERNAME_GROUP")) {
      anonymous = false;
    }
    var authentication = StaticProperties.alternatives(Labels.withId(USER_AUTHENTICATION),
        Alternatives.from(Labels.withId(USER_AUTHENTICATION_ANONYMOUS)),
        Alternatives.from(Labels.withId(USERNAME_GROUP),
            StaticProperties.group(
                Labels.withId(USERNAME_GROUP),
                StaticProperties.stringFreeTextProperty(
                    Labels.withId(USERNAME)),
                StaticProperties.secretValue(Labels.withId(PASSWORD))
            ))
    );
    if (anonymous) {
      authentication.getAlternatives().get(0).setSelected(true);
    } else {
      authentication.getAlternatives().get(1).setSelected(true);
      var username = extractor.singleValueParameter("USERNAME", String.class);
      var password = extractor.secretValue("PASSWORD");
      var group = (StaticPropertyGroup) authentication.getAlternatives().get(1).getStaticProperty();
      ((FreeTextStaticProperty) group.getStaticProperties().get(0)).setValue(username);
      ((SecretStaticProperty) group.getStaticProperties().get(1)).setValue(password);
      ((SecretStaticProperty) group.getStaticProperties().get(1)).setEncrypted(false);
    }

    // remove old authentication property, add new properties for securityMode, policy and authentication options
    staticProperties.remove(1);
    staticProperties.add(1, securityMode);
    staticProperties.add(2, securityPolicy);
    staticProperties.add(3, authentication);

    return staticProperties;
  }
}
