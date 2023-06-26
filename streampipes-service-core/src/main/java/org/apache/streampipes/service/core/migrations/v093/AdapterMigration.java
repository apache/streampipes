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

package org.apache.streampipes.service.core.migrations.v093;

import org.apache.streampipes.model.connect.adapter.migration.MigrationHelpers;
import org.apache.streampipes.model.connect.adapter.migration.utils.AdapterModels;
import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.service.core.migrations.v093.migrator.AdapterMigrator;
import org.apache.streampipes.service.core.migrations.v093.migrator.GenericAdapterMigrator;
import org.apache.streampipes.service.core.migrations.v093.migrator.SpecificAdapterMigrator;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import com.google.gson.JsonObject;
import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.streampipes.model.connect.adapter.migration.utils.AdapterModels.GENERIC_STREAM;

public class AdapterMigration implements Migration {

  private static final Logger LOG = LoggerFactory.getLogger(AdapterMigration.class);

  private static final String ROWS = "rows";
  private final CouchDbClient adapterInstanceClient;
  private final CouchDbClient adapterDescriptionClient;
  private final List<JsonObject> adaptersToMigrate;
  private final List<JsonObject> adapterDescriptionsToDelete;

  private final MigrationHelpers helpers;


  public AdapterMigration() {
    this.adapterInstanceClient = Utils.getCouchDbAdapterInstanceClient();
    this.adapterDescriptionClient = Utils.getCouchDbAdapterDescriptionClient();
    this.adaptersToMigrate = new ArrayList<>();
    this.adapterDescriptionsToDelete = new ArrayList<>();
    this.helpers = new MigrationHelpers();
  }

  @Override
  public boolean shouldExecute() {
    var adapterInstanceUri = getAllDocsUri(adapterInstanceClient);
    var adapterDescriptionUri = getAllDocsUri(adapterDescriptionClient);

    findDocsToMigrate(adapterInstanceClient, adapterInstanceUri, adaptersToMigrate);
    findDocsToMigrate(adapterDescriptionClient, adapterDescriptionUri, adapterDescriptionsToDelete);

    return adaptersToMigrate.size() > 0 || adapterDescriptionsToDelete.size() > 0;
  }

  private void findDocsToMigrate(CouchDbClient adapterClient,
                                 String uri,
                                 List<JsonObject> collector) {
    var existingAdapters = adapterClient.findAny(JsonObject.class, uri);
    if (existingAdapters.size() > 0 && existingAdapters.has(ROWS)) {
      var rows = existingAdapters.get(ROWS);
      rows.getAsJsonArray().forEach(row -> {
        var doc = row.getAsJsonObject().get("doc").getAsJsonObject();
        var docType = doc.get("type").getAsString();
        if (AdapterModels.shouldMigrate(docType)) {
          collector.add(doc);
        }
      });
    }
  }

  @Override
  public void executeMigration() throws IOException {
    var adapterInstanceBackupClient = Utils.getCouchDbAdapterInstanceBackupClient();

    LOG.info("Deleting {} adapter descriptions, which will be regenerated after migration",
        adapterDescriptionsToDelete.size());

    adapterDescriptionsToDelete.forEach(ad -> {
      String docId = helpers.getDocId(ad);
      String rev = helpers.getRev(ad);
      adapterDescriptionClient.remove(docId, rev);
    });

    LOG.info("Migrating {} adapter models", adaptersToMigrate.size());

    LOG.info("Performing backup of old models to database adapterinstance_backup");

    adaptersToMigrate.forEach(adapter -> {
      new AdapterBackupWriter(adapterInstanceBackupClient, new MigrationHelpers()).writeBackup(adapter);
    });

    LOG.info("Performing migration of adapters");

    adaptersToMigrate.forEach(adapter -> {
      var adapterType = adapter.get("type").getAsString();
      if (AdapterModels.isSetAdapter(adapterType)) {
        LOG.warn("Data Set adapters are no longer supported and can't be migrated - consult docs for an alternative");
      } else {
        getAdapterMigrator(adapterType).migrate(adapterInstanceClient, adapter);
      }
    });

    LOG.info("Adapter migration finished");
  }

  @Override
  public String getDescription() {
    return "Migrate all adapters to new data model";
  }

  private String getAllDocsUri(CouchDbClient client) {
    return client.getDBUri().toString() + "_all_docs" + "?include_docs=true";
  }

  private AdapterMigrator getAdapterMigrator(String adapterType) {
    if (adapterType.equals(GENERIC_STREAM)) {
      return new GenericAdapterMigrator(new MigrationHelpers());
    } else {
      return new SpecificAdapterMigrator(new MigrationHelpers());
    }
  }
}
