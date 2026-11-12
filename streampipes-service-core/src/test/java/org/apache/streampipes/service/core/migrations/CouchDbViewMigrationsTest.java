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


package org.apache.streampipes.service.core.migrations;

import org.apache.streampipes.manager.setup.design.UserDesignDocument;
import org.apache.streampipes.manager.setup.tasks.AddDatasetMetadataViewTask;
import org.apache.streampipes.service.core.migrations.v0980.AddDatasetMetadataViewMigration;
import org.apache.streampipes.service.core.migrations.v099.AddAssetManagementViewMigration;
import org.apache.streampipes.service.core.migrations.v099.AddFunctionStateViewMigration;
import org.apache.streampipes.service.core.migrations.v099.AddRefreshTokenViewsMigration;
import org.apache.streampipes.service.core.migrations.v099.AddScriptTemplateViewMigration;
import org.apache.streampipes.storage.couchdb.CouchDbStores;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CouchDbViewMigrationsTest {

  private final InMemoryCouchTransport couch = new InMemoryCouchTransport();
  private final CouchDbStores stores = couch.stores();

  @Test
  void datasetViewMigrationRunsUntilTheViewIsCurrent() throws IOException {
    var migration = new AddDatasetMetadataViewMigration(stores);
    assertTrue(migration.shouldExecute(), "no design document");

    couch.store("dataset", """
        {"_id":"_design/measurement","language":"javascript",
         "views":{"by-measure-name":{"map":"function (doc) { emit(doc.old, doc); }"}}}""");
    assertTrue(migration.shouldExecute(), "outdated function");

    migration.executeMigration();

    assertFalse(migration.shouldExecute());
    assertEquals(AddDatasetMetadataViewTask.MAP_FUNCTION,
        couch.document("dataset", "_design/measurement").at("/views/by-measure-name/map").asText());
  }

  @Test
  void genericStorageViewMigrationsAddTheirView() throws IOException {
    var migrations = List.of(
        new AddAssetManagementViewMigration(stores),
        new AddScriptTemplateViewMigration(stores),
        new AddFunctionStateViewMigration(stores));

    for (var migration : migrations) {
      assertTrue(migration.shouldExecute(), migration.getDescription());
      migration.executeMigration();
      assertFalse(migration.shouldExecute(), migration.getDescription());
      assertTrue(couch.document("genericstorage", migration.getDesignDocumentName())
          .path("views").has(migration.getViewName()));
    }
  }

  @Test
  void genericStorageViewMigrationRunsIfTheViewIsMissing() {
    couch.createDatabase("genericstorage");
    couch.store("genericstorage", """
        {"_id":"_design/assets","language":"javascript","views":{"other":{"map":"function(doc) {}"}}}""");

    assertTrue(new AddAssetManagementViewMigration(stores).shouldExecute());
  }

  @Test
  void refreshTokenMigrationCompletesTheUserDesignDocument() throws IOException {
    var migration = new AddRefreshTokenViewsMigration(stores);
    assertTrue(migration.shouldExecute(), "no design document");

    couch.store("users", """
        {"_id":"_design/users","language":"javascript","views":{"password":{"map":"function(doc) {}"}}}""");
    assertTrue(migration.shouldExecute(), "views are missing");

    migration.executeMigration();

    assertFalse(migration.shouldExecute());
    var views = couch.document("users", "_design/users").get("views");
    assertEquals(UserDesignDocument.REFRESH_TOKEN_BY_HASH_MAP_FUNCTION,
        views.at("/refresh-token-by-hash/map").asText());
    assertEquals(new UserDesignDocument().make().views().size(), views.size());
  }
}
