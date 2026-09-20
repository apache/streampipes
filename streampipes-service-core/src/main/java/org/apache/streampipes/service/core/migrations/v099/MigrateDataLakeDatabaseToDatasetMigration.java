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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MigrateDataLakeDatabaseToDatasetMigration implements Migration {

  private static final Logger LOG = LoggerFactory.getLogger(MigrateDataLakeDatabaseToDatasetMigration.class);

  static final int PAGE_SIZE = 100;

  @Override
  public boolean shouldExecute() {
    return databaseExists(Utils.LEGACY_DATA_LAKE_DB_NAME);
  }

  @Override
  public void executeMigration() throws IOException {
    copyDocuments(Utils.LEGACY_DATA_LAKE_DB_NAME, Utils.DATA_LAKE_DB_NAME);
    removeDatabase(Utils.LEGACY_DATA_LAKE_DB_NAME);
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
      EntityUtils.consume(response.getEntity());
      return statusCode == HttpStatus.SC_OK;
    } catch (IOException e) {
      LOG.warn("Could not determine whether CouchDB database '{}' exists", databaseName, e);
      return false;
    }
  }

  protected void copyDocuments(String sourceDatabaseName,
                               String targetDatabaseName) throws IOException {
    Utils.getCouchDbClient(targetDatabaseName, true).shutdown();

    String startId = null;
    long copiedDocuments = 0;
    do {
      JsonArray rows = getDocumentPage(sourceDatabaseName, startId);
      int documentsToCopy = Math.min(rows.size(), PAGE_SIZE);
      for (int i = 0; i < documentsToCopy; i++) {
        upsertDocument(targetDatabaseName, rows.get(i).getAsJsonObject().getAsJsonObject("doc"));
        copiedDocuments++;
      }
      // The extra row is the inclusive start of the next page, avoiding offset-based scans.
      startId = rows.size() > PAGE_SIZE
          ? rows.get(PAGE_SIZE).getAsJsonObject().get("id").getAsString()
          : null;
    } while (startId != null);

    LOG.info("Copied {} documents from '{}' to '{}'",
        copiedDocuments,
        sourceDatabaseName,
        targetDatabaseName);
  }

  protected JsonArray getDocumentPage(String databaseName, String startId) throws IOException {
    String route = Utils.getDatabaseRoute(databaseName) + "/_all_docs?include_docs=true&limit=" + (PAGE_SIZE + 1);
    if (startId != null) {
      route += "&startkey=" + URLEncoder.encode(new JsonPrimitive(startId).toString(), StandardCharsets.UTF_8);
    }
    var response = Utils.getRequest(route).execute().returnContent().asString();
    return JsonParser.parseString(response).getAsJsonObject().getAsJsonArray("rows");
  }

  protected void upsertDocument(String databaseName,
                                JsonObject document) throws IOException {
    String documentId = document.get("_id").getAsString();

    JsonObject documentToStore = document.deepCopy();
    documentToStore.remove("_rev");

    String targetRoute = Utils.getDatabaseRoute(databaseName) + "/" + Utils.escapePathSegment(documentId);
    String currentRev = getDocumentRev(targetRoute);
    if (currentRev != null) {
      documentToStore.addProperty("_rev", currentRev);
    }

    var response = Utils.putRequest(targetRoute, documentToStore.toString())
        .execute()
        .returnResponse();

    int statusCode = response.getStatusLine().getStatusCode();
    EntityUtils.consume(response.getEntity());
    if (!(statusCode == HttpStatus.SC_CREATED || statusCode == HttpStatus.SC_ACCEPTED
        || statusCode == HttpStatus.SC_OK)) {
      throw new IOException("Unexpected response while copying document '" + documentId + "': " + statusCode);
    }
  }

  protected void removeDatabase(String databaseName) throws IOException {
    var response = Utils.deleteRequest(Utils.getDatabaseRoute(databaseName))
        .execute()
        .returnResponse();
    int statusCode = response.getStatusLine().getStatusCode();
    EntityUtils.consume(response.getEntity());

    if (!(statusCode == HttpStatus.SC_OK
        || statusCode == HttpStatus.SC_ACCEPTED
        || statusCode == HttpStatus.SC_NOT_FOUND)) {
      throw new IOException("Unexpected response while deleting legacy database '" + databaseName + "': "
          + statusCode);
    }
  }

  protected String getDocumentRev(String documentRoute) throws IOException {
    var response = Utils.getRequest(documentRoute).execute().returnResponse();
    try {
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode == HttpStatus.SC_OK) {
        var document = JsonParser.parseString(EntityUtils.toString(response.getEntity())).getAsJsonObject();
        return document.get("_rev").getAsString();
      }
      if (statusCode == HttpStatus.SC_NOT_FOUND) {
        return null;
      }
      throw new IOException("Unexpected response while reading document revision: " + statusCode);
    } finally {
      EntityUtils.consume(response.getEntity());
    }
  }
}
