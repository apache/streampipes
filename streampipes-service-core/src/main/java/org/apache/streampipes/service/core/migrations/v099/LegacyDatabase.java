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

import org.apache.streampipes.couchdb.client.CouchDatabase;
import org.apache.streampipes.couchdb.client.exception.CouchDbException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * A database a migration retires. Checks whether it is still there without creating it, and reports a failed removal
 * as {@link IOException}, which leaves the migration to be retried on the next start.
 *
 * @param database handle that does not create the database
 */
record LegacyDatabase(CouchDatabase database) {

  private static final Logger LOG = LoggerFactory.getLogger(LegacyDatabase.class);
  private static final int NOT_FOUND = 404;

  boolean exists() {
    try {
      return database.exists();
    } catch (CouchDbException e) {
      LOG.warn("Could not determine whether CouchDB database '{}' exists", database.name(), e);
      return false;
    }
  }

  void remove() throws IOException {
    try {
      database.delete();
      LOG.info("Removed CouchDB database '{}'", database.name());
    } catch (CouchDbException e) {
      if (e.getStatus() != NOT_FOUND) {
        throw new IOException("Could not delete CouchDB database '" + database.name() + "'", e);
      }
    }
  }
}
