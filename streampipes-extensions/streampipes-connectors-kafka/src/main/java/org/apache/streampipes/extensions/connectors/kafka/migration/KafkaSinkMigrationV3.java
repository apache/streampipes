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

import org.apache.streampipes.extensions.api.extractor.IDataSinkParameterExtractor;
import org.apache.streampipes.extensions.api.migration.IDataSinkMigrator;
import org.apache.streampipes.extensions.connectors.kafka.shared.kafka.KafkaConfigProvider;
import org.apache.streampipes.extensions.connectors.kafka.sink.KafkaPublishSink;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.model.staticproperty.StaticProperty;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Adds the message key configuration to existing Kafka sinks.
 * The alternative which publishes records without a key is preselected,
 * so that existing pipelines keep their current behavior.
 */
public class KafkaSinkMigrationV3 implements IDataSinkMigrator {

  @Override
  public ModelMigratorConfig config() {
    return new ModelMigratorConfig(
        KafkaPublishSink.ID,
        SpServiceTagPrefix.DATA_SINK,
        2,
        3
    );
  }

  @Override
  public MigrationResult<DataSinkInvocation> migrate(DataSinkInvocation element,
                                                     IDataSinkParameterExtractor extractor)
      throws RuntimeException {
    var staticProperties = element.getStaticProperties();
    staticProperties.add(
        indexOfAccessMode(staticProperties),
        KafkaConfigProvider.getMessageKeyAlternatives());
    return MigrationResult.success(element);
  }

  /**
   * Look up where the access mode sits, so that the message key ends up in the same place as in
   * a newly created sink. If there is no access mode, the message key is added at the end.
   *
   * @param staticProperties the configurations of a stored sink.
   * @return the position of the access mode, or the end of the list if there is none.
   */
  private int indexOfAccessMode(List<StaticProperty> staticProperties) {
    return IntStream.range(0, staticProperties.size())
        .filter(i -> KafkaConfigProvider.ACCESS_MODE.equals(
            staticProperties.get(i).getInternalName()))
        .findFirst()
        .orElse(staticProperties.size());
  }
}
