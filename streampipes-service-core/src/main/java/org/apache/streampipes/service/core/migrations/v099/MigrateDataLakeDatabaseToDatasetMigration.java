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

import org.apache.streampipes.couchdb.client.TypedCollection;
import org.apache.streampipes.couchdb.client.exception.CouchDbException;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.couchdb.CouchDbStores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MigrateDataLakeDatabaseToDatasetMigration implements Migration {

  private static final Logger LOG = LoggerFactory.getLogger(MigrateDataLakeDatabaseToDatasetMigration.class);

  static final String LEGACY_DATA_LAKE_DB_NAME = "data-lake";
  static final int PAGE_SIZE = 100;

  private final CouchDbStores stores;
  private final LegacyDatabase legacyDatabase;

  public MigrateDataLakeDatabaseToDatasetMigration(CouchDbStores stores) {
    this.stores = stores;
    this.legacyDatabase = new LegacyDatabase(stores.server().database(LEGACY_DATA_LAKE_DB_NAME));
  }

  @Override
  public boolean shouldExecute() {
    return legacyDatabase.exists();
  }

  @Override
  public void executeMigration() throws IOException {
    try {
      copyDocuments();
    } catch (CouchDbException e) {
      throw new IOException("Could not copy database '" + LEGACY_DATA_LAKE_DB_NAME + "' to '"
          + CouchDbStores.DATA_LAKE_DB_NAME + "'", e);
    }
    legacyDatabase.remove();
  }

  @Override
  public String getDescription() {
    return "Migrate legacy data lake database to dataset database";
  }

  private void copyDocuments() {
    var targetDatabase = stores.database(CouchDbStores.DATA_LAKE_DB_NAME);
    targetDatabase.create();
    var source = legacyDatabase.database().raw();
    var target = targetDatabase.raw();

    String startId = null;
    long copiedDocuments = 0;
    do {
      List<Map<String, Object>> page = documentPage(source, startId);
      int documentsToCopy = Math.min(page.size(), PAGE_SIZE);
      for (int i = 0; i < documentsToCopy; i++) {
        target.upsert(page.get(i));
        copiedDocuments++;
      }
      // The extra row is the inclusive start of the next page, avoiding offset-based scans.
      startId = page.size() > PAGE_SIZE ? (String) page.get(PAGE_SIZE).get("_id") : null;
    } while (startId != null);

    LOG.info("Copied {} documents from '{}' to '{}'",
        copiedDocuments,
        LEGACY_DATA_LAKE_DB_NAME,
        CouchDbStores.DATA_LAKE_DB_NAME);
  }

  private List<Map<String, Object>> documentPage(TypedCollection<Map<String, Object>> source, String startId) {
    // Design documents are part of the copy; the page must also stay complete for the paging above.
    var query = source.allDocs().includeDesignDocuments().limit(PAGE_SIZE + 1);
    if (startId != null) {
      query.startKey(startId);
    }
    return query.docs();
  }
}
