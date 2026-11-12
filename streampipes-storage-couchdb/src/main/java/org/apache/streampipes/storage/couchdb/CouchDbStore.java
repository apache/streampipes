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
import org.apache.streampipes.couchdb.client.TypedCollection;
import org.apache.streampipes.couchdb.client.codec.DocumentCodec;

/**
 * A database together with the codec one storage reads and writes it with.
 *
 * @param database the database
 * @param codec    the codec
 */
public record CouchDbStore(CouchDatabase database, DocumentCodec codec) {

  public <T> TypedCollection<T> typed(Class<T> type) {
    return database.typed(type, codec);
  }
}
