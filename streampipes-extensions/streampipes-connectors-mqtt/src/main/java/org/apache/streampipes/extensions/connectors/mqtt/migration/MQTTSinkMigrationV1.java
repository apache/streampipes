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
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Labels;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.apache.streampipes.extensions.connectors.mqtt.sink.MqttPublisherSink.AUTH_ALTERNATIVE_CERT;
import static org.apache.streampipes.extensions.connectors.mqtt.sink.MqttPublisherSink.CERT_GROUP;
import static org.apache.streampipes.extensions.connectors.mqtt.sink.MqttPublisherSink.CLIENT_CERT;
import static org.apache.streampipes.extensions.connectors.mqtt.sink.MqttPublisherSink.CLIENT_KEY;

public class MQTTSinkMigrationV1 implements IDataSinkMigrator {

            private static final Logger LOG = LoggerFactory.getLogger(MQTTSinkMigrationV1.class);

    @Override
    public ModelMigratorConfig config() {
        return new ModelMigratorConfig(
                "sp:org.apache.streampipes.sinks.brokers.jvm.mqtt",
                SpServiceTagPrefix.DATA_SINK,
                0,
                1);

    }

    @Override
    public MigrationResult<DataSinkInvocation> migrate(DataSinkInvocation element,
            IDataSinkParameterExtractor extractor) throws RuntimeException {
        // change Text
        LOG.info("Change Description of Port");
        var port = (FreeTextStaticProperty) element.getStaticProperties().get(2);
        LOG.info(" " + port.getLabel());
        port.setDescription(
                "Port of MQTT broker (default 1883, for TLS often 8883)");
        element.getStaticProperties().set(2, port);

        // change Text
        LOG.info("Change Description of TLS");
        LOG.info(" " + element.getStaticProperties().get(4).getLabel());
        var tls = element.getStaticProperties().get(4);
        LOG.info(" " + tls.getDescription());
        tls.setDescription(
                "Select protocol. TCP (plaintext), SSL/TLS (encrypted)");
        element.getStaticProperties().set(4, tls);
        LOG.info("Migrate Security");
        LOG.info(element.getStaticProperties().get(3).getLabel());
        migrateSecurity((StaticPropertyAlternatives) element.getStaticProperties().get(3));

        return MigrationResult.success(element);
    }

    public void migrateSecurity(StaticPropertyAlternatives securityAlternatives) {
        migrateGroup(securityAlternatives.getAlternatives());
    }

    private void migrateGroup(List<StaticPropertyAlternative> alternatives) {
        var group = StaticProperties.group(Labels.withId(CERT_GROUP),
                StaticProperties.stringFreeTextProperty(Labels.withId(CLIENT_CERT), true, false),
                StaticProperties.secretValue(Labels.withId(CLIENT_KEY)));
        group.setHorizontalRendering(false);
        alternatives.add(Alternatives.from(Labels.withId(AUTH_ALTERNATIVE_CERT),
                group));

    }

}
