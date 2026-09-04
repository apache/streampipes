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

package org.apache.streampipes.sinks.internal.jvm.datalake.migrations;

import org.apache.streampipes.extensions.api.extractor.IDataSinkParameterExtractor;
import org.apache.streampipes.extensions.api.migration.IDataSinkMigrator;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.model.staticproperty.OneOfStaticProperty;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sinks.internal.jvm.datalake.DataLakeSink;

public class DataLakeSinkMigrationV1 implements IDataSinkMigrator {

  @Override
  public ModelMigratorConfig config() {
    return new ModelMigratorConfig(
        "org.apache.streampipes.sinks.internal.jvm.datalake",
        SpServiceTagPrefix.DATA_SINK,
        0,
        1
    );
  }

  /**
   * Adds the static property for schema update to the sink and selects the option to update the
   * schema as a default
   */
  @Override
  public MigrationResult<DataSinkInvocation> migrate(
      DataSinkInvocation element,
      IDataSinkParameterExtractor extractor
  ) throws RuntimeException {
    if (!isSchemaUpdateStrategyPresent(element)) {
      var oneOfStaticProperty = createDefaultSchemaUpdateStrategy();

      element.getStaticProperties()
          .add(oneOfStaticProperty);
    }

    return MigrationResult.success(element);
  }

  private boolean isSchemaUpdateStrategyPresent(DataSinkInvocation element) {
    return element.getStaticProperties()
        .stream()
        .anyMatch(sp -> sp.getInternalName().equals(DataLakeSink.SCHEMA_UPDATE_KEY));
  }

  private static OneOfStaticProperty createDefaultSchemaUpdateStrategy() {
    var label = Labels.from(
        DataLakeSink.SCHEMA_UPDATE_KEY,
        "Schema Update",
        "Update existing schemas with the new one or extend the existing schema with new properties"
    );
    var schemaUpdateStaticProperty = new OneOfStaticProperty(
        label.getInternalId(),
        label.getLabel(),
        label.getDescription()
    );

    var options = Options.from(DataLakeSink.SCHEMA_UPDATE_OPTION, DataLakeSink.EXTEND_EXISTING_SCHEMA_OPTION);
    options.get(0)
           .setSelected(true);
    schemaUpdateStaticProperty.setOptions(options);
    return schemaUpdateStaticProperty;
  }
}
