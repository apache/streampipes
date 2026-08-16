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

package org.apache.streampipes.sinks.databases.jvm.postgresql.migration;

import org.apache.streampipes.extensions.api.extractor.IDataSinkParameterExtractor;
import org.apache.streampipes.extensions.api.migration.IDataSinkMigrator;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.SlideToggleStaticProperty;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sinks.databases.jvm.postgresql.PostgreSqlSink;
import org.apache.streampipes.vocabulary.XSD;

/**
 * Migrates the PostgreSQL sink from model version 0 to 1.
 * <p>
 * Version 1 adds two configuration fields: a toggle that allows the sink to create the target
 * table, and a batch size for grouping events into a single insert. Existing pipelines keep
 * their previous behavior through the default values: table creation allowed and batch size 1.
 */
public class PostgreSqlSinkMigrationV1 implements IDataSinkMigrator {

  /*
   * Position of the toggle in a pipeline stored with model version 0, where the fields are
   * host, port, database, table, user, password and ssl mode. The sink renders the toggle
   * right below the table name, so a migrated pipeline has to show it in the same place.
   */
  private static final int ALLOW_NEW_TABLE_CREATION_INDEX = 4;

  @Override
  public ModelMigratorConfig config() {
    return new ModelMigratorConfig(
        PostgreSqlSink.ID,
        SpServiceTagPrefix.DATA_SINK,
        0,
        1
    );
  }

  @Override
  public MigrationResult<DataSinkInvocation> migrate(DataSinkInvocation element,
                                                     IDataSinkParameterExtractor extractor)
      throws RuntimeException {
    addAllowNewTableCreationToggle(element);
    addBatchSize(element);
    return MigrationResult.success(element);
  }

  private void addAllowNewTableCreationToggle(DataSinkInvocation element) {
    var label = Labels.from(
        PostgreSqlSink.ALLOW_NEW_TABLE_CREATION_KEY,
        "Allow New Table Creation",
        "Let the sink create the table entered above if it does not exist yet"
    );
    var staticProperty = new SlideToggleStaticProperty(
        label.getInternalId(),
        label.getLabel(),
        label.getDescription(),
        true
    );
    staticProperty.setSelected(true);

    element.getStaticProperties().add(ALLOW_NEW_TABLE_CREATION_INDEX, staticProperty);
  }

  private void addBatchSize(DataSinkInvocation element) {
    var label = Labels.from(
        PostgreSqlSink.BATCH_SIZE_KEY,
        "Batch Size",
        "How many events should the sink collect before writing them together to the database?"
    );
    var staticProperty = new FreeTextStaticProperty(
        label.getInternalId(),
        label.getLabel(),
        label.getDescription()
    );
    staticProperty.setRequiredDatatype(XSD.INTEGER);
    staticProperty.setValue("1");

    element.getStaticProperties().add(staticProperty);
  }
}
