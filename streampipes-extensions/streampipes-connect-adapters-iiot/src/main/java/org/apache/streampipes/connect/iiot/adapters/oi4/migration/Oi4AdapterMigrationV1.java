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
package org.apache.streampipes.connect.iiot.adapters.oi4.migration;

import org.apache.streampipes.connect.iiot.adapters.oi4.Oi4Adapter;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.migration.IAdapterMigrator;
import org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;

import java.util.List;

public class Oi4AdapterMigrationV1 implements IAdapterMigrator {

    @Override
    public ModelMigratorConfig config() {
        return new ModelMigratorConfig(
                Oi4Adapter.ID,
                SpServiceTagPrefix.ADAPTER,
                0,
                1);

    }

    @Override
    public MigrationResult<AdapterDescription> migrate(AdapterDescription element,
            IStaticPropertyExtractor extractor) throws RuntimeException {

        changeUrlDescription(element);

        accessModeDescription(element);

        migrateSecurity((StaticPropertyAlternatives) element.getConfig().get(1));

        return MigrationResult.success(element);
    }

    private void migrateSecurity(StaticPropertyAlternatives securityAlternatives) {
        migrateGroup(securityAlternatives.getAlternatives());
    }

    private void changeUrlDescription(AdapterDescription element) {
        var url = (FreeTextStaticProperty) element.getConfig().get(0);
        url.setDescription(
                "Example: tcp://test-server.com:1883 (Protocol required. Port required), with TLS ssl://test-server.com:8883 (Protocol required. Port required)");
        element.getConfig().set(0, url);
    }

    private void accessModeDescription(AdapterDescription element) {
        var accessmode = (StaticPropertyAlternatives) element.getConfig().get(1);

        accessmode.setLabel("User Authentication");
        accessmode.setDescription(
                "Choose an authentication method for the user");
        element.getConfig().set(1, accessmode);
    }

    private void migrateGroup(List<StaticPropertyAlternative> alternatives) {
        alternatives.add(MqttConnectUtils.getClientCertAccess());

    }

}
