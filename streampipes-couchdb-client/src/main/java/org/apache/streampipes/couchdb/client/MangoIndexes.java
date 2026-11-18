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

package org.apache.streampipes.couchdb.client;

import org.apache.streampipes.couchdb.client.query.MangoIndex;
import org.apache.streampipes.couchdb.client.transport.CouchRequest;

/**
 * Mango indexes of one database.
 */
public final class MangoIndexes {

  private final CouchDatabase database;

  MangoIndexes(CouchDatabase database) {
    this.database = database;
  }

  /**
   * Creates the index unless an identical one exists. Idempotent; the index lives in a design document, which
   * {@code _all_docs} results of this client leave out.
   *
   * @param index index definition
   * @return true if the index was created, false if it existed
   */
  public boolean ensure(MangoIndex index) {
    return database.execute(CouchRequest.post(database.path("/_index"), CouchJson.write(index.toMap())),
        response -> "created".equals(CouchJson.readTree(response).path("result").asText()));
  }
}
