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
import org.apache.streampipes.extensions.connectors.kafka.shared.kafka.KafkaBootstrapServersMerger;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;

/**
 * Replaces the single-broker configuration (host and port) of the Kafka adapter with a single
 * {@code bootstrap-servers} configuration that accepts a comma-separated list of brokers.
 */
public class KafkaAdapterMigrationV3 implements IAdapterMigrator {

  @Override
  public ModelMigratorConfig config() {
    return new ModelMigratorConfig(
        KafkaProtocol.ID,
        SpServiceTagPrefix.ADAPTER,
        2,
        3
    );
  }

  @Override
  public MigrationResult<AdapterDescription> migrate(AdapterDescription element,
                                                     IStaticPropertyExtractor extractor) throws RuntimeException {
    if (!KafkaBootstrapServersMerger.merge(element.getConfig())) {
      return MigrationResult.failure(
          element, "Could not merge the broker of the Kafka adapter, no former host was found.");
    }
    return MigrationResult.success(element);
  }
}
