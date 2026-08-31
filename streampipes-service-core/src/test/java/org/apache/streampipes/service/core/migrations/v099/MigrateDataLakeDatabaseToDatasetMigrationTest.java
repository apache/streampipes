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

package org.apache.streampipes.service.core.migrations.v099;

import org.apache.streampipes.storage.couchdb.utils.Utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MigrateDataLakeDatabaseToDatasetMigrationTest {

  private final Map<String, Boolean> existingDatabases = new HashMap<>();
  private final Map<String, Integer> documentCounts = new HashMap<>();
  private boolean replicated;
  private MigrateDataLakeDatabaseToDatasetMigration migration;

  @BeforeEach
  void setUp() {
    this.replicated = false;
    this.migration = new MigrateDataLakeDatabaseToDatasetMigration() {
      @Override
      protected boolean databaseExists(String databaseName) {
        return existingDatabases.getOrDefault(databaseName, false);
      }

      @Override
      protected int getDocumentCount(String databaseName) {
        return documentCounts.getOrDefault(databaseName, 0);
      }

      @Override
      protected void copyDocuments(String sourceDatabaseName,
                                   String targetDatabaseName) {
        MigrateDataLakeDatabaseToDatasetMigrationTest.this.replicated = true;
      }
    };
  }

  @Test
  void shouldExecuteReturnsFalseWhenLegacyDatabaseDoesNotExist() {
    existingDatabases.put(Utils.LEGACY_DATA_LAKE_DB_NAME, false);

    assertFalse(migration.shouldExecute());
  }

  @Test
  void shouldExecuteReturnsTrueWhenDatasetDatabaseDoesNotExistYet() {
    existingDatabases.put(Utils.LEGACY_DATA_LAKE_DB_NAME, true);
    existingDatabases.put(Utils.DATA_LAKE_DB_NAME, false);

    assertTrue(migration.shouldExecute());
  }

  @Test
  void shouldExecuteReturnsTrueWhenDatasetDatabaseHasFewerDocuments() {
    existingDatabases.put(Utils.LEGACY_DATA_LAKE_DB_NAME, true);
    existingDatabases.put(Utils.DATA_LAKE_DB_NAME, true);
    documentCounts.put(Utils.LEGACY_DATA_LAKE_DB_NAME, 4);
    documentCounts.put(Utils.DATA_LAKE_DB_NAME, 1);

    assertTrue(migration.shouldExecute());
  }

  @Test
  void shouldExecuteReturnsFalseWhenDatasetDatabaseAlreadyContainsAllDocuments() {
    existingDatabases.put(Utils.LEGACY_DATA_LAKE_DB_NAME, true);
    existingDatabases.put(Utils.DATA_LAKE_DB_NAME, true);
    documentCounts.put(Utils.LEGACY_DATA_LAKE_DB_NAME, 2);
    documentCounts.put(Utils.DATA_LAKE_DB_NAME, 2);

    assertFalse(migration.shouldExecute());
  }

  @Test
  void executeMigrationReplicatesLegacyDatabase() throws IOException {
    migration.executeMigration();

    assertTrue(replicated);
  }
}
