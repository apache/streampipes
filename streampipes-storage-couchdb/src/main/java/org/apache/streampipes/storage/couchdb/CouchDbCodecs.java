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

import org.apache.streampipes.couchdb.client.codec.DocumentCodec;
import org.apache.streampipes.couchdb.client.codec.GsonCodec;
import org.apache.streampipes.storage.couchdb.serializer.GsonSerializer;

/**
 * The codecs documents are stored with, built once. They read and write the stored format of previous versions,
 * which the tests in {@code storage.couchdb.compat} check.
 *
 * @param gson      default Gson setup
 * @param adapter   Gson setup for adapter descriptions and instances
 * @param principal Gson setup for the {@code users} database (principals, permissions, roles, tokens)
 */
public record CouchDbCodecs(DocumentCodec gson, DocumentCodec adapter, DocumentCodec principal) {

  public static CouchDbCodecs defaults() {
    return new CouchDbCodecs(
        GsonCodec.of(GsonSerializer.getGsonBuilder().create()),
        GsonCodec.of(GsonSerializer.getAdapterGsonBuilder().create()),
        GsonCodec.of(GsonSerializer.getPrincipalGsonBuilder().create()));
  }
}
