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

package org.apache.streampipes.storage.couchdb;

import org.apache.streampipes.couchdb.client.CouchDatabase;
import org.apache.streampipes.couchdb.client.CouchServer;
import org.apache.streampipes.couchdb.client.DatabaseOptions;

/**
 * Entry point of the storage implementations to CouchDB: hands out databases of one shared {@link CouchServer},
 * paired with the codec of the storage. Databases are created on first use.
 */
public class CouchDbStores {

  public static final String USER_DB_NAME = "users";
  public static final String DATA_LAKE_DB_NAME = "dataset";

  private final CouchServer server;
  private final CouchDbCodecs codecs;

  public CouchDbStores(CouchServer server, CouchDbCodecs codecs) {
    this.server = server;
    this.codecs = codecs;
  }

  public CouchServer server() {
    return server;
  }

  public CouchDbCodecs codecs() {
    return codecs;
  }

  /**
   * A database that is created if it does not exist yet.
   *
   * @param name database name
   * @return the database
   */
  public CouchDatabase database(String name) {
    return server.database(name, DatabaseOptions.createIfMissing());
  }

  public CouchDbStore gson(String database) {
    return new CouchDbStore(database(database), codecs.gson());
  }

  public CouchDbStore adapter(String database) {
    return new CouchDbStore(database(database), codecs.adapter());
  }

  public CouchDbStore principal(String database) {
    return new CouchDbStore(database(database), codecs.principal());
  }
}
