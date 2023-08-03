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

import com.google.gson.JsonObject;
import org.lightcouch.CouchDbClient;
import org.lightcouch.NoDocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdapterBackupWriter {

  private static final Logger LOG = LoggerFactory.getLogger(AdapterBackupWriter.class);
  private static final String REV = "_rev";

  private final MigrationHelpers helpers;

  private final CouchDbClient couchDbClient;

  public AdapterBackupWriter(CouchDbClient couchDbClient,
                             MigrationHelpers migrationHelpers) {
    this.couchDbClient = couchDbClient;
    this.helpers = migrationHelpers;
  }

  public void writeBackup(JsonObject jsonObject) {
    var docId = helpers.getDocId(jsonObject);
    if (!isPresent(docId)) {
      jsonObject.add("rev_backup", jsonObject.get(REV));
      jsonObject.remove(REV);
      this.couchDbClient.save(jsonObject);
    } else {
      LOG.warn("Skipping backup of document with id {} since it already exists", docId);
    }
  }

  private boolean isPresent(String docId) {
    try {
      couchDbClient.find(JsonObject.class, docId);
      return true;
    } catch (NoDocumentException e) {
      return false;
    }
  }
}
