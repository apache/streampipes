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
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.SlideToggleStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sinks.databases.jvm.postgresql.PostgreSqlSink;
import org.apache.streampipes.vocabulary.XSD;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the implementation of the {@link PostgreSqlSinkMigrationV1} class.
 */
class PostgreSqlSinkMigrationV1Test {

  private static final List<StaticProperty> VERSION_ZERO_FIELDS = List.of(
      new FreeTextStaticProperty("db_host", "db_host", "", XSD.STRING),
      new FreeTextStaticProperty("db_port", "db_port", "", XSD.STRING),
      new FreeTextStaticProperty("db_name", "db_name", "", XSD.STRING),
      new FreeTextStaticProperty("db_table", "db_table", "", XSD.STRING),
      new FreeTextStaticProperty("db_user", "db_user", "", XSD.STRING),
      new FreeTextStaticProperty("db_password", "db_password", "", XSD.STRING),
      new FreeTextStaticProperty("ssl_mode", "ssl_mode", "", XSD.STRING)
  );

  private DataSinkInvocation dataSink;

  /**
   * Set up a PostgreSQL sink as it was saved with version 0 and run the migration on it.
   */
  @BeforeEach
  void setUp() {
    dataSink = new DataSinkInvocation();
    dataSink.setStaticProperties(new ArrayList<>(VERSION_ZERO_FIELDS));
    new PostgreSqlSinkMigrationV1().migrate(dataSink, null);
  }

  /**
   * Collect the names of all fields of the migrated data sink, in the order they are shown.
   *
   * @return the internal names of its fields.
   */
  List<String> getFieldNames() {
    return dataSink.getStaticProperties().stream()
        .map(StaticProperty::getInternalName)
        .toList();
  }

  /**
   * Look up a single field of the migrated data sink.
   *
   * @param name the internal name of the field.
   * @return the field the migration added under that name.
   */
  StaticProperty findField(String name) {
    return dataSink.getStaticProperties().stream()
        .filter(field -> name.equals(field.getInternalName()))
        .findFirst()
        .orElseThrow(() -> new AssertionError("migration did not add a field named '" + name + "'"));
  }

  @Test
  void testConfig_migratesFromZeroToOne() {
    ModelMigratorConfig config = new PostgreSqlSinkMigrationV1().config();
    assertEquals(0, config.fromVersion());
    assertEquals(1, config.toVersion());
  }

  @Test
  void testConfig_targetsPostgreSqlSink() {
    ModelMigratorConfig config = new PostgreSqlSinkMigrationV1().config();
    assertEquals(PostgreSqlSink.ID, config.targetAppId());
    assertEquals(SpServiceTagPrefix.DATA_SINK, config.modelType());
  }

  @Test
  void testMigrate_keepsExistingFieldsAndAddsNewSinkOptions() {
    List<String> expected = List.of(
        "db_host", "db_port", "db_name", "db_table", "allow_new_table_creation",
        "db_user", "db_password", "ssl_mode", "batch_size"
    );
    assertEquals(expected, getFieldNames());
  }

  @Test
  void testMigrate_turnsToggleOn() {
    SlideToggleStaticProperty toggle =
        (SlideToggleStaticProperty) findField(PostgreSqlSink.ALLOW_NEW_TABLE_CREATION_KEY);
    assertTrue(toggle.isSelected(), "version 0 sinks created missing tables, so the toggle has to be on");
  }

  @Test
  void testMigrate_setsBatchSizeToOne() {
    FreeTextStaticProperty batchSize = (FreeTextStaticProperty) findField(PostgreSqlSink.BATCH_SIZE_KEY);
    assertEquals("1", batchSize.getValue());
    assertEquals(XSD.INTEGER, batchSize.getRequiredDatatype());
  }

  @Test
  void testMigrate_labelsNewSinkOptions() {
    StaticProperty toggle = findField(PostgreSqlSink.ALLOW_NEW_TABLE_CREATION_KEY);
    StaticProperty batchSize = findField(PostgreSqlSink.BATCH_SIZE_KEY);

    assertFalse(toggle.getLabel().isBlank());
    assertFalse(toggle.getDescription().isBlank());
    assertFalse(batchSize.getLabel().isBlank());
    assertFalse(batchSize.getDescription().isBlank());
  }
}
