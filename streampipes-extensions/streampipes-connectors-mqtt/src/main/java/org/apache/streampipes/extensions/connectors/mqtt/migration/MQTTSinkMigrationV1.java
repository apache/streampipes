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
package org.apache.streampipes.extensions.connectors.mqtt.migration;

import org.apache.streampipes.extensions.api.extractor.IDataSinkParameterExtractor;
import org.apache.streampipes.extensions.api.migration.IDataSinkMigrator;
import org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.OneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.sdk.StaticProperties;

public class MQTTSinkMigrationV1 implements IDataSinkMigrator {

        @Override
        public ModelMigratorConfig config() {
                return new ModelMigratorConfig(
                                "org.apache.streampipes.sinks.brokers.jvm.mqtt",
                                SpServiceTagPrefix.DATA_SINK,
                                0,
                                1);

        }

        @Override
        public MigrationResult<DataSinkInvocation> migrate(DataSinkInvocation element,
                        IDataSinkParameterExtractor extractor) throws RuntimeException {
                // migrate Topic
                var topic = migrateTopicToNewNaming(element.getStaticProperties().get(0));
                // Migrate DAta from Host +Port + Protocol to URI
                migrateData(element);
                // SORT THE ITEMS
                element.getStaticProperties().set(4, topic);
                // Remove TLS
                element.getStaticProperties().remove(2);
                // Remove Port
                element.getStaticProperties().remove(1);
                // Add Certificate Option
                migrateSecurity(element);
                return MigrationResult.success(element);
        }

        private StaticProperty migrateTopicToNewNaming(StaticProperty topic) {
                topic.setLabel(MqttConnectUtils.getTopicLabel().getLabel());
                topic.setInternalName(MqttConnectUtils.getTopicLabel().getInternalId());
                return topic;

        }

        private String buildBrokerURI(DataSinkInvocation element) {

                var host = ((FreeTextStaticProperty) element.getStaticProperties().get(1)).getValue();
                var port = ((FreeTextStaticProperty) element.getStaticProperties().get(2)).getValue();
                var encryptionAlternative = ((OneOfStaticProperty) element.getStaticProperties().get(4))
                                .getOptions();
                var encryption = "";
                for (var i = 0; i < encryptionAlternative.size(); i++) {
                        Option alternative = encryptionAlternative.get(i);

                        if (alternative.isSelected()) {
                                encryption = alternative.getName();
                        }
                }
                String protocol = "tcp";

                if ("SSL".equalsIgnoreCase(encryption)) {
                        protocol = "ssl";
                }

                var brokerUri = protocol + "://" + host + ":" + port;
                return brokerUri;

        }

        private void migrateData(DataSinkInvocation element) {

                var brokerUri = buildBrokerURI(element);

                var broker = StaticProperties.stringFreeTextProperty(MqttConnectUtils.getBrokerUrlLabel(), brokerUri);

                element.getStaticProperties().set(0, broker);

        }

        private void migrateSecurity(DataSinkInvocation element) {
                var oldSecurityAlternatives = (StaticPropertyAlternatives) element.getStaticProperties().get(1);

                var securityAlternative = StaticProperties.alternatives(MqttConnectUtils.getAccessModeLabel(),
                                MqttConnectUtils.getAnonymousAccess(),
                                MqttConnectUtils.getUsernameAccess(), MqttConnectUtils.getClientCertAccess());
                for (var i = 0; i < oldSecurityAlternatives.getAlternatives().size(); i++) {
                        StaticPropertyAlternative alternative = oldSecurityAlternatives.getAlternatives().get(i);
                        if (alternative.getSelected()) {
                                securityAlternative.getAlternatives().get(i).setSelected(true);
                        } else {
                                securityAlternative.getAlternatives().get(i).setSelected(false);
                        }
                }
                element.getStaticProperties().set(1, securityAlternative);
        }

}
