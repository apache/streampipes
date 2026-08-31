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

import org.apache.streampipes.service.core.migrations.Migration;
import org.apache.streampipes.storage.couchdb.utils.Utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MigrateDataLakeDatabaseToDatasetMigration implements Migration {

  private static final Logger LOG = LoggerFactory.getLogger(MigrateDataLakeDatabaseToDatasetMigration.class);

  @Override
  public boolean shouldExecute() {
    if (!databaseExists(Utils.LEGACY_DATA_LAKE_DB_NAME)) {
      return false;
    }

    if (!databaseExists(Utils.DATA_LAKE_DB_NAME)) {
      return true;
    }

    return getDocumentCount(Utils.DATA_LAKE_DB_NAME) < getDocumentCount(Utils.LEGACY_DATA_LAKE_DB_NAME);
  }

  @Override
  public void executeMigration() throws IOException {
    var payload = String.format(
        "{\"source\":\"%s\",\"target\":\"%s\",\"create_target\":true}",
        Utils.LEGACY_DATA_LAKE_DB_NAME,
        Utils.DATA_LAKE_DB_NAME
    );

    var response = Utils.postRequest(Utils.getDatabaseRoute("") + "_replicate", payload)
        .execute()
        .returnResponse();

    int statusCode = response.getStatusLine().getStatusCode();
    if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_ACCEPTED) {
      LOG.info("Legacy data lake database replicated to dataset database");
    } else {
      throw new IOException("Unexpected response while replicating legacy data lake database: " + statusCode);
    }
  }

  @Override
  public String getDescription() {
    return "Migrate legacy data lake database to dataset database";
  }

  protected boolean databaseExists(String databaseName) {
    try {
      var response = Utils.getRequest(Utils.getDatabaseRoute(databaseName))
          .execute()
          .returnResponse();
      int statusCode = response.getStatusLine().getStatusCode();
      return statusCode == HttpStatus.SC_OK;
    } catch (IOException e) {
      LOG.warn("Could not determine whether CouchDB database '{}' exists", databaseName, e);
      return false;
    }
  }

  protected int getDocumentCount(String databaseName) {
    try {
      var response = Utils.getRequest(Utils.getDatabaseRoute(databaseName))
          .execute()
          .returnContent()
          .asString();
      JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
      return jsonObject.get("doc_count").getAsInt();
    } catch (IOException e) {
      LOG.warn("Could not determine document count for CouchDB database '{}'", databaseName, e);
      return 0;
    }
  }
}
