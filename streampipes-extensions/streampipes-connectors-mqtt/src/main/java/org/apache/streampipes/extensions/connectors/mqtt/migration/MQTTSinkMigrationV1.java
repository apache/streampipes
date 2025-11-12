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
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.helpers.Labels;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils.BROKER_URL;

public class MQTTSinkMigrationV1 implements IDataSinkMigrator {

        private static final Logger LOG = LoggerFactory.getLogger(MQTTSinkMigrationV1.class);

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

                
                LOG.info("Start Mig ");

               
                // migrate Topic

                var topic = element.getStaticProperties().get(0);


                LOG.info("Start Data Mig ");
                // Migrate DAta from Host +Port + Protocpl to URI
                migrateData(element);

                LOG.info("Data Mig Finished ");

                LOG.info("Start Sorting Items  ");
                // SORT THE ITEMS
                element.getStaticProperties().set(4, topic);
                //Remove TLS
                element.getStaticProperties().remove(2);
                //Remobve Port 
                element.getStaticProperties().remove(1);

                LOG.info("FInished Sorting Items  ");
  
                // Add Certificate Option
                migrateSecurity((StaticPropertyAlternatives) element.getStaticProperties().get(1));

                //Migrate Text 

                return MigrationResult.success(element);
        }

        private String buildBrokerURI(DataSinkInvocation element) {

                var host = ((FreeTextStaticProperty) element.getStaticProperties().get(1)).getValue();
                LOG.info("host " + host);
                var port = ((FreeTextStaticProperty) element.getStaticProperties().get(2)).getValue();
                LOG.info("port " + port);
                var encryptionAlternative = ((OneOfStaticProperty) element.getStaticProperties().get(4))
                                .getOptions();
                LOG.info("Successfully got encryption Alt");
                var encryption = "";
                for (var i = 0; i < encryptionAlternative.size(); i++) {
                        LOG.info("For LOOP");
                        Option alternative = encryptionAlternative.get(i);
                        LOG.info("alternative " + alternative);

                        if (alternative.isSelected()) {
                                encryption = alternative.getName();
                        }
                }
                String protocol = "tcp";

                if ("SSL".equalsIgnoreCase(encryption)) {
                        protocol = "ssl";
                }

                var brokerUri = protocol + "://" + host + ":" + port;

                LOG.info("broker uri " + brokerUri);
                return brokerUri;

        }

        private void migrateData(DataSinkInvocation element) {

                var brokerUri = buildBrokerURI(element);

                var broker = StaticProperties.stringFreeTextProperty(Labels.withId(BROKER_URL), brokerUri);

                element.getStaticProperties().set(0, broker);

        }

        private void migrateSecurity(StaticPropertyAlternatives securityAlternatives) {
                LOG.info("Get Alternative");
                migrateGroup(securityAlternatives.getAlternatives());
        }

        private void migrateGroup(List<StaticPropertyAlternative> alternatives) {
 
                alternatives.add(MqttConnectUtils.getClientCertAccess());

        }

}
