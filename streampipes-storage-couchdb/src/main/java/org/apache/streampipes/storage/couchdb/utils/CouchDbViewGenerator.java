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

package org.apache.streampipes.storage.couchdb.utils;

import org.apache.streampipes.couchdb.client.CouchServer;
import org.apache.streampipes.couchdb.client.DatabaseOptions;
import org.apache.streampipes.couchdb.client.design.DesignDocument;
import org.apache.streampipes.couchdb.client.exception.CouchDbException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.streampipes.storage.couchdb.constants.GenericCouchDbConstants.DB_NAME;
import static org.apache.streampipes.storage.couchdb.constants.GenericCouchDbConstants.DESIGN_DOC_NAME;

public class CouchDbViewGenerator {

  private static final Logger LOG = LoggerFactory.getLogger(CouchDbViewGenerator.class);

  private static final String APP_DOC_TYPE_MAP =
      "function (doc) {\nif (doc._id) {\nemit([doc.appDocType, doc._id], 1)\n}\n}";

  private final CouchServer server;

  public CouchDbViewGenerator(CouchServer server) {
    this.server = server;
  }

  public void createGenericDatabaseIfNotExists() {
    LOG.info("Checking if generic database {} exists...", DB_NAME);

    try {
      var database = server.database(DB_NAME, DatabaseOptions.existing());
      if (database.create()) {
        LOG.info("Database {} successfully created", DB_NAME);
        LOG.info("Initializing database views...");
        database.design().sync(DesignDocument.named(DESIGN_DOC_NAME).withView(DESIGN_DOC_NAME, APP_DOC_TYPE_MAP));
        LOG.info("View {} successfully created", DESIGN_DOC_NAME);
      } else {
        LOG.info("Database {} already present", DB_NAME);
      }
    } catch (CouchDbException e) {
      LOG.error("Could not connect to CouchDB storage", e);
    }
  }
}
