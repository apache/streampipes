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

import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.migration.MigrationResult;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.SlideToggleStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sinks.databases.jvm.postgresql.PostgreSqlSink;
import org.apache.streampipes.vocabulary.XSD;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link PostgreSqlSinkMigrationV1}.
 */
class PostgreSqlSinkMigrationV1Test {

  private static final String APPEND_TO_EXISTING_KEY = "append_to_existing";
  private static final String BATCH_SIZE_KEY = "batch_size";
  private static final List<String> VERSION_ZERO_FIELDS = List.of(
      "db_host", "db_port", "db_name", "db_table", "db_user", "db_password", "ssl_mode"
  );

  /**
   * Runs the migration on a pipeline saved with model version 0 and returns it afterward.
   * The migration moves fields around without reading them, so plain text fields are enough.
   *
   * @return the migrated pipeline element.
   */
  private DataSinkInvocation runMigration() {
    List<StaticProperty> properties = new ArrayList<>();
    VERSION_ZERO_FIELDS.forEach(name -> properties.add(
        new FreeTextStaticProperty(name, name, "", XSD.STRING)));

    DataSinkInvocation pipelineElement = new DataSinkInvocation();
    pipelineElement.setStaticProperties(properties);

    MigrationResult<DataSinkInvocation> result =
        new PostgreSqlSinkMigrationV1().migrate(pipelineElement, null);
    assertTrue(result.success(), "the migration should report success");
    return pipelineElement;
  }

  /**
   * Returns the field with the given internal name, or fails the test if the migration did not add it.
   */
  private StaticProperty findField(DataSinkInvocation element, String internalName) {
    return element.getStaticProperties().stream()
        .filter(property -> internalName.equals(property.getInternalName()))
        .findFirst()
        .orElseThrow(() -> new AssertionError("migration did not add a field named '" + internalName + "'"));
  }

  @Test
  void testConfig_migration_fromVersionZeroToOne() {
    ModelMigratorConfig config = new PostgreSqlSinkMigrationV1().config();

    String expectedAppId = PostgreSqlSink.ID;
    String actualAppId = config.targetAppId();
    assertEquals(expectedAppId, actualAppId, "migration should target the PostgreSQL sink");

    SpServiceTagPrefix expectedModelType = SpServiceTagPrefix.DATA_SINK;
    SpServiceTagPrefix actualModelType = config.modelType();
    assertEquals(expectedModelType, actualModelType, "migration should apply to a data sink");

    int expectedFromVersion = 0;
    int actualFromVersion = config.fromVersion();
    assertEquals(expectedFromVersion, actualFromVersion, "migration should start at version 0");

    int expectedToVersion = 1;
    int actualToVersion = config.toVersion();
    assertEquals(expectedToVersion, actualToVersion, "migration should target version 1");
  }

  @Test
  void testMigrate_pipelineWithoutTheNewFields_addsTwoFields() {
    DataSinkInvocation migrated = runMigration();

    List<String> expected = List.of(
        "db_host", "db_port", "db_name", "db_table", APPEND_TO_EXISTING_KEY,
        "db_user", "db_password", "ssl_mode", BATCH_SIZE_KEY
    );
    List<String> actual = migrated.getStaticProperties().stream()
        .map(StaticProperty::getInternalName)
        .toList();

    assertEquals(expected, actual, "the toggle belongs below the table name and the batch size last");
  }

  @Test
  void testMigrate_addsUseExistingTableToggleTurnedOff() {
    DataSinkInvocation migrated = runMigration();
    SlideToggleStaticProperty toggle =
        (SlideToggleStaticProperty) findField(migrated, APPEND_TO_EXISTING_KEY);

    boolean expected = false;
    boolean actual = toggle.isSelected();

    assertEquals(expected, actual, "the toggle must default to off to preserve the old behavior");
  }

  @Test
  void testMigrate_addsBatchSizeDefaultingToOne() {
    DataSinkInvocation migrated = runMigration();
    FreeTextStaticProperty batchSize =
        (FreeTextStaticProperty) findField(migrated, BATCH_SIZE_KEY);

    String expectedValue = "1";
    String actualValue = batchSize.getValue();
    assertEquals(expectedValue, actualValue, "the batch size must default to 1");

    URI expectedType = XSD.INTEGER;
    URI actualType = batchSize.getRequiredDatatype();

    assertEquals(expectedType, actualType, "the batch size should be restricted to integers");
  }
}