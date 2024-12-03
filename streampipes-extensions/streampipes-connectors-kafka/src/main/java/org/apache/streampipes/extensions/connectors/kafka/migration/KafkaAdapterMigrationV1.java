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

package org.apache.streampipes.extensions.connectors.kafka.migration;

import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.migration.IAdapterMigrator;
import org.apache.streampipes.extensions.connectors.kafka.adapter.KafkaProtocol;
import org.apache.streampipes.extensions.connectors.kafka.shared.kafka.KafkaConfigProvider;
import org.apache.streampipes.extensions.management.connect.adapter.parser.AvroParser;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.helpers.Labels;

public class KafkaAdapterMigrationV1 implements IAdapterMigrator {
  @Override
  public ModelMigratorConfig config() {
    return new ModelMigratorConfig(
        KafkaProtocol.ID,
        SpServiceTagPrefix.ADAPTER,
        0,
        1
    );
  }

  @Override
  public MigrationResult<AdapterDescription> migrate(AdapterDescription element,
                                                     IStaticPropertyExtractor extractor) throws RuntimeException {

    migrateSecurity((StaticPropertyAlternatives) element.getConfig().get(0));
    migrateAvro((StaticPropertyAlternatives) element.getConfig().get(6));
    element.getConfig().add(3, makeConsumerGroup());
    return MigrationResult.success(element);
  }

  public void migrateSecurity(StaticPropertyAlternatives securityAlternatives) {
    migrateGroup(securityAlternatives.getAlternatives().get(2));
    migrateGroup(securityAlternatives.getAlternatives().get(3));
  }

  public void migrateAvro(StaticPropertyAlternatives formatAlternatives) {
    var parser = new AvroParser();
    var avroParserDescription = new StaticPropertyAlternative(
        parser.declareDescription().getName(),
        parser.declareDescription().getName(),
        parser.declareDescription().getDescription());

    avroParserDescription.setStaticProperty(parser.declareDescription().getConfig());
    formatAlternatives.getAlternatives().add(
        avroParserDescription
    );
  }

  private StaticPropertyAlternatives makeConsumerGroup() {
    var consumerGroupAlternatives = StaticProperties.alternatives(
        KafkaConfigProvider.getConsumerGroupLabel(),
        KafkaConfigProvider.getAlternativesRandomGroupId(),
        KafkaConfigProvider.getAlternativesGroupId()
    );
    consumerGroupAlternatives.getAlternatives().get(0).setSelected(true);
    return consumerGroupAlternatives;
  }

  private void migrateGroup(StaticPropertyAlternative alternative) {
    boolean selected = alternative.getSelected();
    var securityMechanism = StaticProperties.singleValueSelection(
        Labels.withId(KafkaConfigProvider.SECURITY_MECHANISM),
        KafkaConfigProvider.makeSecurityMechanism());
    securityMechanism.getOptions().get(0).setSelected(selected);
    ((StaticPropertyGroup) alternative.getStaticProperty()).setHorizontalRendering(false);
    ((StaticPropertyGroup) alternative.getStaticProperty()).getStaticProperties().add(
        0,
        securityMechanism
    );
  }
}
